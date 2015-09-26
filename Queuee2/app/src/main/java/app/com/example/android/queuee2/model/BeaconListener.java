package app.com.example.android.queuee2.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by bkach on 9/24/15.
 */
public class BeaconListener {

    private BeaconManager mBeaconManager;
    private BroadcastReceiver mReciever;
    private boolean mRecieverRegistered;
    private Activity mActivity;
    private static final String TAG = BeaconListener.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private Observable<String> mObservable;

    public BeaconListener(Activity activity){
        this.mActivity = activity;
        mBeaconManager = new BeaconManager(activity);
        mRecieverRegistered = false;
        mObservable = Observable.create(subscriber -> checkBluetooth(subscriber));
    }

    private void checkBluetooth(Subscriber <? super String> subscriber) {
        if (mBeaconManager.hasBluetooth()) {
            registerReciever(subscriber);
            if (mBeaconManager.isBluetoothEnabled()) {
                connectToService();
                findBeacon(subscriber);
            } else {
                requestBluetooth();
            }
        } else {
            Toast.makeText(mActivity, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            subscriber.onError(new Exception("myException"));
        }
    }

    private void registerReciever(Subscriber <? super String> subscriber){
        mRecieverRegistered = true;
        mReciever = createBroadcastReceiver(subscriber);
        mActivity.registerReceiver(mReciever, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private void requestBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void connectToService() {
        mBeaconManager.connect(() -> {
            try {
                mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            } catch (RemoteException e) {
                Toast.makeText(mActivity, "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Cannot start ranging", e);
            }
        });
    }

    private void findBeacon(Subscriber <? super String> subscriber){
        mBeaconManager.setRangingListener(
                (Region region, final List<Beacon> beacons) -> {
                    if (beacons.size() > 0) {
                        Beacon beacon = beacons.get(0);
                        if (Utils.computeAccuracy(beacon) < 0.1) {
                            subscriber.onNext(beacon.getProximityUUID());
                            mBeaconManager.disconnect();
                        }
                    }
                }
        );
    }

    private BroadcastReceiver createBroadcastReceiver(Subscriber<? super String> subscriber) {
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
                            findBeacon(subscriber);
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
    }

    public void stopRanging() {
        try {
            mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Toast.makeText(mActivity, "Cannot stop ranging, something terrible happened", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Cannot stop ranging", e);
        }
    }

    public Observable<String> getObservable() {
        return mObservable;
    }
}
