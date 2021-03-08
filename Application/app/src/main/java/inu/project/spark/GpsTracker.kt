package inu.project.spark

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat

class GpsTracker(context:Context) : Service(),LocationListener {

    var mContext: Context

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES:Float = 10f;
    private var MIN_TIME_BW_UPDATES:Long = 1000 * 60 * 1;
    private var location: Location?
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var locationManager: LocationManager

    init{
        this.mContext = context
        location = getLocation()
    }
    fun getLongitude():Double{
        if(location != null)
        {
            val rlongitude = location!!.getLongitude()
            longitude = location!!.getLongitude()
            return rlongitude
        }
        else{
            return longitude
        }
    }
    fun getLatitude():Double{
        if(location != null)
        {
            val rlatitude = location!!.getLatitude()
            latitude = rlatitude
            return rlatitude
        }
        else{
            return latitude
        }
    }
    fun getLocation():Location?{
        try {
            var locationManager:LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var isGPSEnabled:Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            var isNetworkEnabled:Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                var hasFineLocationPermission:Int = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                var hasCoarseLocationPermission:Int = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION)

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                } else
                    return null

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null)
                        {
                            latitude = location!!.getLatitude()
                            longitude = location!!.getLongitude()
                        }
                    }
                }
                if (isGPSEnabled)
                {
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null)
                            {
                                latitude = location!!.latitude;
                                longitude = location!!.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (e:Exception)
        {
            Log.d("@@@", "" + e.toString())
        }
        return location
    }
    fun stopUsingGPS(){
        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
    }
    override fun onLocationChanged(location: Location) {
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}