package model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

/**
 * Created by bkach on 9/24/15.
 */
public class BeaconListener {

    private BeaconManager mBeaconManager;
    private Subscriber mSubscriber;
    private BroadcastReceiver mReciever;
    private boolean mRecieverRegistered;
    private boolean mBluetoothDenied;
    private boolean mConnecting;
    private Beacon lastBeacon;
    private Activity mActivity;
    private static final String TAG = BeaconListener.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private Observable<String> mObservable;

    public BeaconListener(Activity activity){
        this.mActivity = activity;
        mBeaconManager = new BeaconManager(activity);
        mRecieverRegistered = false;
    }

    public void connect(Action1<String> onSuccess, Action1<Throwable> onFailure, boolean bluetoothDenied){
        if (!mConnecting) {
            mConnecting = true;
            mBluetoothDenied = bluetoothDenied;
            createObservable()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onSuccess, onFailure);
        }
        mConnecting = false;
    }

    private void checkBluetooth() {
        if (mBeaconManager.hasBluetooth()) {
            registerReciever();
            if (mBeaconManager.isBluetoothEnabled()) {
                connectToService();
                findBeacon();
            } else {
                if (!mBluetoothDenied) {
                    requestBluetooth();
                }
            }
        } else {
            Toast.makeText(mActivity, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            mSubscriber.onError(new Exception("myException"));
        }
    }

    private void registerReciever(){
        if(mReciever == null || !mRecieverRegistered) {
            mReciever = createBroadcastReceiver();
            mActivity.registerReceiver(mReciever, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            mRecieverRegistered = true;
        }
    }

    private void requestBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void connectToService() {
        mBeaconManager.connect(() -> {
            mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
        });
    }

    private void findBeacon(){
        mBeaconManager.setRangingListener(
                (Region region, final List<Beacon> beacons) -> {
                    if (beacons.size() > 0) {
                        Beacon beacon = beacons.get(0);
                        String uuid = beacon.getProximityUUID().toString();
                        int minor = beacon.getMinor();
                        String queueId = null;

                        if (uuid.equals("b9407f30-f5f8-466e-aff9-25556b57fe6d")) {
                            if(minor == 1) {
                                queueId = "queue1";
                            } else if (minor == 2) {
                                queueId = "queue2";
                            } else if (minor == 3) {
                                queueId = "queue3";
                            }
                        }
                        if (!beacon.equals(lastBeacon)) {
                            lastBeacon = beacon;
                            mSubscriber.onNext(queueId);
                        }
                    }
                }
        );
    }

    private BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG, "bluetooth state off");
                            requestBluetooth();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(TAG, "bluetooth state turning off");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "bluetooth state on");
                            connectToService();
                            findBeacon();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG, "bluetooth state turning on");
                            break;
                    }
                }
            }
        };
    }

    public void disconnect(){
        if (mBeaconManager != null) {
            mBeaconManager.disconnect();
            stopRanging();
        }
        if (mRecieverRegistered) {
            mActivity.unregisterReceiver(mReciever);
            mRecieverRegistered = false;
        }
        mSubscriber.onCompleted();
    }

    public void stopRanging() {
        mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
    }

    public Observable<String> createObservable() {
        mObservable = Observable.create(subscriber -> {
            mSubscriber = subscriber;
            checkBluetooth();
        });
        return mObservable;
    }
}
