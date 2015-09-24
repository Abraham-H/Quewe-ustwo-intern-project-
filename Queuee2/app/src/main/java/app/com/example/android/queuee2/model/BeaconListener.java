package app.com.example.android.queuee2.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by bkach on 9/24/15.
 */
public class BeaconListener {

    private BeaconManager beaconManager;
    private Activity activity;
    private static final String TAG = BeaconListener.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private Observable<String> observable;

    public BeaconListener(Activity ctx){
        this.activity = ctx;
        beaconManager = new BeaconManager(ctx);
        observable = Observable.create(subscriber -> {
            checkBluetooth();
            findBeacon(subscriber);
        });
    }

    private void checkBluetooth(){
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(activity, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }
        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    private void connectToService() {
        beaconManager.connect(() -> {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(activity, "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            });
    }

    private void findBeacon(Subscriber <? super String> subscriber){
        beaconManager.setRangingListener((Region region, final List<Beacon> beacons) -> {
            activity.runOnUiThread(() -> {
                if (beacons.size() > 0) {
                    subscriber.onNext(beacons.get(0).getProximityUUID());
                    beaconManager.disconnect();
                } else {
                    subscriber.onNext("No Beacons Found");
                }
            });
        });
    }

    public Observable<String> getObservable() {
        return observable;
    }
}
