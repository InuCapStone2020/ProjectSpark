package inu.project.spark

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class mapFragment : Fragment(),MapView.CurrentLocationEventListener,MapView.MapViewEventListener {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    private lateinit var mapViewContainer:ViewGroup
    private lateinit var gpsTracker:GpsTracker
    private val REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    private var displayFlag = false
    private var markerFlag = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapView = MapView(activity)
        mapViewContainer = requireView().findViewById<View>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)
        val nowmapbutton = requireView().findViewById<View>(R.id.map_now_button)
        mapView.setMapViewEventListener(this)
        nowmapbutton.setOnClickListener{
            // service and permission check function
            if(!checkLocationServicesStatus()){
                //showDialogForLocationSetting()
            }
            else{
                if(checkRunTimePermission()) {
                    gpsTracker = GpsTracker(requireContext())
                    val latitude: Double = gpsTracker.getLatitude()
                    val longitude: Double = gpsTracker.getLongitude()
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
                    Toast.makeText(context, "현재위치 이동 완료",Toast.LENGTH_SHORT).show()
                }
            }
        }
        val viewmapbutton = requireView().findViewById<View>(R.id.view_map_button)
        viewmapbutton.setOnClickListener{
            // if displayFlag is False then
            if(!displayFlag){
                //send query that number of message each city to server
                // if receive json that number of message each city then ping on the map
            }
            // if displayFlag is True then
            else{
                // delete all of ping on the map

            }
            displayFlag = !displayFlag
        }
        val searchmapbutton = requireView().findViewById<View>(R.id.search_map_button)
        val nextmapbutton = requireView().findViewById<View>(R.id.next_map_button)
        val localsearchbutton = requireView().findViewById<View>(R.id.localsearch_map_button)
        searchmapbutton.setOnClickListener{
            // inactive button
            viewmapbutton.visibility = View.INVISIBLE
            searchmapbutton.visibility = View.INVISIBLE
            // if displayflag is true then inactive this function
            // active button
            markerFlag = true
            requireView().findViewById<View>(R.id.search_map_text).visibility = View.VISIBLE
            nextmapbutton.visibility = View.VISIBLE
            localsearchbutton.visibility = View.VISIBLE
            // active function
            val marker1 = MapPOIItem()
            marker1.itemName = ""
            marker1.tag = 1
            marker1.mapPoint = mapView.mapCenterPoint
            marker1.markerType = MapPOIItem.MarkerType.BluePin
            marker1.isDraggable = true
            mapView.addPOIItem(marker1)
        }
        nextmapbutton.setOnClickListener{
            // inactive button
            markerFlag = false
            requireView().findViewById<View>(R.id.search_map_text).visibility = View.INVISIBLE
            nextmapbutton.visibility = View.INVISIBLE
            localsearchbutton.visibility = View.INVISIBLE
            // active button, function


        }

    }

    fun checkRunTimePermission():Boolean{
        val hasFineLocationPermission:Int = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission:Int = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(context, "이 기능을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE)
            }
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        mapViewContainer.removeAllViews()
    }
    fun checkLocationServicesStatus():Boolean {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // overriding MapView.CurrentLocationEventListener
    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {

    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {

    }

    // overriding MapView.MapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        if (markerFlag){
            val mapview = mapViewContainer.get(0) as MapView
            val poi = mapview.findPOIItemByTag(1)
            if (poi == null)
            {
                val marker1 = MapPOIItem()
                marker1.itemName = ""
                marker1.tag = 1
                marker1.mapPoint = mapview.mapCenterPoint
                marker1.markerType = MapPOIItem.MarkerType.BluePin
                marker1.isDraggable = true
                mapview.addPOIItem(marker1)
            }
            else{
                poi.mapPoint = p1
            }
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }
}