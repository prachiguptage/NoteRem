package com.prachigupta.noterem;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MapService extends Service {


    LocationManager manager;
    Location current;
    Boolean locationChanged;

    DatabaseHandler databaseHandler;

    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            current = location;
            List<GeoFencing> runningList = databaseHandler.databaseToListGeofencing();
            for (GeoFencing item : runningList) {

                if (item.get_running().equals(Constant.RUNNING)) {

                    current.setLatitude(location.getLatitude());
                    current.setLongitude(location.getLongitude());

                    double latdiff = Math.abs(Double.parseDouble(item.get_latitude()) - current.getLatitude());
                    double longdiff = Math.abs(Double.parseDouble(item.get_longitude()) - current.getLongitude());

                    if (latdiff < 0.00001 && longdiff < 0.00001)
                        locationChanged = true;
                    else
                        locationChanged = false;
                    if (locationChanged) {

                        NoteEntry entry = databaseHandler.getEntry(item.get_noteId());


                        Notification.Builder builder = new Notification.Builder(getApplicationContext());
                        builder.setContentTitle("Location Reminder");
                        builder.setContentText(entry.get_title() != null ? entry.get_title() : entry.get_date());
                        builder.setSmallIcon(R.drawable.ic_query_builder_black_24dp);
                        Notification notification = null;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            notification = builder.build();
                        }

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


                        notification.defaults |= Notification.DEFAULT_SOUND;
                        notification.defaults |= Notification.DEFAULT_VIBRATE;
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;

                        notificationManager.notify(item.get_id(), notification);
                        item.set_running(Constant.FINISHED);
                        databaseHandler.
                                updateGeofencingItemFinished(item);
                    }
                }

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public MapService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("Service Start", "onCreate");

        databaseHandler = DatabaseHandler.getInstance(getApplicationContext());

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Please enable Location in Setting",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Log.i("Service Start", "request Location");
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 0, gpsListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
