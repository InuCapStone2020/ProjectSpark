package inu.project.spark

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import net.daum.android.map.coord.MapCoord
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory


class mapFragment : Fragment(),MapView.MapViewEventListener {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.setNavigationOnClickListener {
            val i = Intent(context, MainActivity::class.java)
            startActivity(i)
        }
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    private lateinit var mapViewContainer:ViewGroup
    private lateinit var gpsTracker:GpsTracker
    private val REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    private var displayFlag = false
    private var markerFlag = false
    private var searchFlag = false
    private var searchedLogitude:Double = 0.0
    private var searchedLatitude:Double = 0.0
    private val localhash = hashMapOf<String,Triple<Int,Double,Double>>()
    private val poiarr = mutableListOf<MapPOIItem>()

    fun changeSearchedCord(logitude:Double,latitude:Double){
        searchedLogitude = logitude
        searchedLatitude = latitude
        Log.d("changeSearchedCord",searchedLogitude.toString() + "/"+ searchedLatitude.toString())
    }

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
                Toast.makeText(context, "gps와 인터넷을 켜주세요",Toast.LENGTH_SHORT).show()
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
                //send request that number of message each city to server
                // if receive json that number of message each city then ping on the map
                localhash_init()
                val baseURL = MyApplication.baseurl
                val retrofit = Retrofit.Builder()
                        .baseUrl(baseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                val api = retrofit.create(spark::class.java)
                val callGetWeekCount = api.getWeekCount()
                callGetWeekCount.enqueue(object: Callback<List<regionCount>> {
                    override fun onResponse(call: Call<List<regionCount>>, response: Response<List<regionCount>>) {
                        if(response.isSuccessful()) {
                            val resData = Gson().toJson(response.body())
                            Log.d("weekcountrequest", "Successful")

                            localhash_update(resData)
                            poiarr.clear()
                            for (l in localhash){
                                if(l.value.first == 0){
                                    continue
                                }
                                val marker0 = MapPOIItem()
                                val point:MapPoint
                                marker0.itemName = l.key + " : " + l.value.first.toString() + "건"
                                marker0.tag = 0
                                marker0.markerType = MapPOIItem.MarkerType.RedPin
                                val tempstr = l.key.split(" ")
                                if (tempstr[0] == "전체"){
                                    continue
                                }
                                else if (tempstr[1] == "전체"){
                                    point = MapPoint.mapPointWithGeoCoord(l.value.second,l.value.third)
                                    marker0.mapPoint = point
                                    marker0.tag = 2
                                    poiarr.add(marker0)
                                    mapView.selectPOIItem(marker0,false)
                                }
                                else{
                                    point = MapPoint.mapPointWithGeoCoord(l.value.second,l.value.third)
                                    marker0.mapPoint = point
                                    poiarr.add(marker0)
                                }
                            }
                            val tempPoint = mutableListOf<MapPOIItem>()
                            if(mapView.zoomLevel < 7){
                                for(p in poiarr){
                                    if(p.tag == 0){
                                        tempPoint.add(p)
                                    }
                                }
                            }
                            else{
                                for(p in poiarr){
                                    if(p.tag == 2){
                                        tempPoint.add(p)
                                    }
                                }
                            }
                            mapView.addPOIItems(tempPoint.toTypedArray())
                        } else {
                            Log.d("weekcountrequest", "notSuccessful")
                        }
                    }

                    override fun onFailure(call: Call<List<regionCount>>, t: Throwable) {
                        Log.e("weekcountrequest", "onFailure")
                    }
                })

            }
            // if displayFlag is True then
            else{
                // delete all of ping on the map
                mapView.removeAllPOIItems()
            }
            displayFlag = !displayFlag
        }
        val searchmapbutton = requireView().findViewById<View>(R.id.search_map_button)
        val nextmapbutton = requireView().findViewById<View>(R.id.next_map_button)
        val localsearchbutton = requireView().findViewById<View>(R.id.localsearch_map_button)
        localsearchbutton.setOnClickListener {
            searchFlag = true
            val marker1 = mapView.findPOIItemByTag(1)
            searchedLatitude = marker1.mapPoint.mapPointGeoCoord.latitude.toDouble()
            searchedLogitude = marker1.mapPoint.mapPointGeoCoord.longitude.toDouble()
            (activity as SubActivity).supportFragmentManager.beginTransaction().replace(R.id.frameLayout,mapsearchFragment()).addToBackStack("map").commit()
        }
        if(searchFlag){
            // inactive button
            viewmapbutton.visibility = View.INVISIBLE
            searchmapbutton.visibility = View.INVISIBLE
            // if displayflag is true then inactive this function
            if (displayFlag){
                mapView.removeAllPOIItems()
                displayFlag = !displayFlag
            }
            // active button
            markerFlag = true
            requireView().findViewById<View>(R.id.search_map_text).visibility = View.VISIBLE
            nextmapbutton.visibility = View.VISIBLE
            localsearchbutton.visibility = View.VISIBLE
            // active function
            val marker1 = MapPOIItem()
            marker1.itemName = ""
            marker1.tag = 1
            val mappoint = MapPoint.mapPointWithGeoCoord(searchedLatitude,searchedLogitude)
            marker1.mapPoint =mappoint
            marker1.markerType = MapPOIItem.MarkerType.BluePin
            marker1.isDraggable = true
            mapView.addPOIItem(marker1)
            mapView.setMapCenterPoint(mappoint,true)

        }
        searchmapbutton.setOnClickListener{
            // inactive button
            viewmapbutton.visibility = View.INVISIBLE
            searchmapbutton.visibility = View.INVISIBLE
            // if displayflag is true then inactive this function
            if (displayFlag){
                mapView.removeAllPOIItems()
                displayFlag = !displayFlag
            }
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

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        if(!searchFlag){
            mapViewContainer.removeAllViews()
        }
        super.onStop()

    }
    fun checkLocationServicesStatus():Boolean {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    // localhash function
    fun localhash_init(){
        localhash.clear()
        val localarray1 = resources.getStringArray(R.array.local_do)
        for (local1 in localarray1){
            var localarray2:Array<String>
            var local_latitude:Array<String>
            var local_longitude:Array<String>
            when (local1) {
                "서울특별시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_seoul)
                    local_latitude = resources.getStringArray(R.array.local_do_seoul_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_seoul_longitude)
                }
                "부산광역시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_busan)
                    local_latitude = resources.getStringArray(R.array.local_do_busan_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_busan_longitude)
                }
                "대구광역시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_daegu)
                    local_latitude = resources.getStringArray(R.array.local_do_daegu_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_daegu_longitude)
                }
                "인천광역시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_incheon)
                    local_latitude = resources.getStringArray(R.array.local_do_incheon_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_incheon_longitude)
                }
                "광주광역시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_gwangju)
                    local_latitude = resources.getStringArray(R.array.local_do_gwangju_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_gwangju_longitude)
                }
                "대전광역시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_daejeon)
                    local_latitude = resources.getStringArray(R.array.local_do_daejeon_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_daejeon_longitude)
                }
                "울산광역시" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_ulsan)
                    local_latitude = resources.getStringArray(R.array.local_do_ulsan_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_ulsan_longitude)
                }
                "경기도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_gyeonggido)
                    local_latitude = resources.getStringArray(R.array.local_do_gyeonggido_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_gyeonggido_longitude)
                }
                "강원도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_gangwondo)
                    local_latitude = resources.getStringArray(R.array.local_do_gangwondo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_gangwondo_longitude)
                }
                "충청북도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_chungcheongbukdo)
                    local_latitude = resources.getStringArray(R.array.local_do_chungcheongbukdo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_chungcheongbukdo_longitude)
                }
                "충청남도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_chungcheongnamdo)
                    local_latitude = resources.getStringArray(R.array.local_do_chungcheongnamdo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_chungcheongnamdo_longitude)
                }
                "전라북도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_jeonlabukdo)
                    local_latitude = resources.getStringArray(R.array.local_do_jeonlabukdo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_jeonlabukdo_longitude)
                }
                "전라남도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_jeonlanamdo)
                    local_latitude = resources.getStringArray(R.array.local_do_jeonlanamdo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_jeonlanamdo_longitude)
                }
                "경상북도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_gyeongsangbukdo)
                    local_latitude = resources.getStringArray(R.array.local_do_gyeongsangbukdo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_gyeongsangbukdo_longitude)
                }
                "경상남도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_gyeongsangnamdo)
                    local_latitude = resources.getStringArray(R.array.local_do_gyeongsangnamdo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_gyeongsangnamdo_longitude)
                }
                "제주특별자치도" -> {
                    localarray2 = resources.getStringArray(R.array.local_do_jejudo)
                    local_latitude = resources.getStringArray(R.array.local_do_jejudo_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_jejudo_longitude)
                }
                else -> {
                    localarray2 = resources.getStringArray(R.array.local_do_all)
                    local_latitude = resources.getStringArray(R.array.local_do_latitude)
                    local_longitude = resources.getStringArray(R.array.local_do_longitude)
                }
            }
            val index:Int = localarray2.size
            for (i in 0..index-1){
                localhash[local1 + " " + localarray2[i]] = Triple(0,local_latitude[i].toDouble(),local_longitude[i].toDouble())
            }
        }
    }
    fun localhash_update(strjson:String){
        try{
            val jarr = JSONArray(strjson)
            val s = jarr.length()
            for (i in 0..s-1){
                val tempobj = jarr.getJSONObject(i)
                val value:Int = tempobj.getInt("count")
                var key = tempobj.getString("region")
                if (key == "세종특별자치시"){
                    key += " 전체"
                }
                localhash[key] = Triple(value,localhash[key]!!.second,localhash[key]!!.third)
            }
        }catch(e:JSONException){
            e.printStackTrace()
            Toast.makeText(requireContext(),"json error",Toast.LENGTH_SHORT).show()
        }
    }
    // overriding MapView.MapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.d("zoomlevelchange",p1.toString())
        if(displayFlag){
            if(p1 < 7){
                if(p0!!.findPOIItemByTag(0)==null){
                    val tempPoint = mutableListOf<MapPOIItem>()
                    for(p in poiarr){
                        if(p.tag == 0){
                            tempPoint.add(p)
                        }
                    }
                    p0.removeAllPOIItems()
                    p0.addPOIItems(tempPoint.toTypedArray())
                }
            }
            else{
                if(p0!!.findPOIItemByTag(2)==null){
                    val tempPoint = mutableListOf<MapPOIItem>()
                    for(p in poiarr){
                        if(p.tag == 2){
                            tempPoint.add(p)
                        }
                    }
                    p0.removeAllPOIItems()
                    p0.addPOIItems(tempPoint.toTypedArray())
                }
            }
        }
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