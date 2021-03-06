package inu.project.spark

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import net.daum.mf.map.api.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


class mapFragment : Fragment(),MapView.MapViewEventListener {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        backPressedMainActivity()
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
    private var resultFlag = false
    private var searchedLongitude:Double = 0.0
    private var searchedLatitude:Double = 0.0
    private val localhash = hashMapOf<String, Triple<Int, Double, Double>>()
    private val poiarr = mutableListOf<MapPOIItem>()
    private lateinit var POIitemListener:MarkerEventListener
    private lateinit var POIitemListener2:MarkerEventListener2
    private lateinit var callback: OnBackPressedCallback

    fun setResultFlag(b:Boolean){
        this.resultFlag = b
    }
    fun getResultFlag():Boolean{
        return resultFlag
    }
    fun setSearchFlag(b: Boolean){
        this.searchFlag = b
    }
    fun getSearchFlag():Boolean{
        return searchFlag
    }

    fun changeSearchedCord(logitude: Double, latitude: Double){
        searchedLongitude = logitude
        searchedLatitude = latitude
        Log.d("changeSearchedCord", searchedLongitude.toString() + "/" + searchedLatitude.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mapView = MapView(activity)
        mapViewContainer = requireView().findViewById<View>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)
        val nowmapbutton = requireView().findViewById<View>(R.id.map_now_button)
        mapView.setMapViewEventListener(this)
        mapView.setCalloutBalloonAdapter(object : CalloutBalloonAdapter {
            override fun getCalloutBalloon(p0: MapPOIItem?): View? {
                Log.d("getCalloutBallon", "true")
                return null
            }
            override fun getPressedCalloutBalloon(p0: MapPOIItem?): View? {
                Log.d("getPressedCalloutBallon", "true")
                return null
            }
        })
        POIitemListener = MarkerEventListener((activity as SubActivity))
        mapView.setPOIItemEventListener(POIitemListener)
        nowmapbutton.setOnClickListener{
            // service and permission check function
            if(!checkLocationServicesStatus()){
                Toast.makeText(context, "gps와 인터넷을 켜주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                if(checkRunTimePermission()) {
                    gpsTracker = GpsTracker(requireContext())
                    val latitude: Double = gpsTracker.getLatitude()
                    val longitude: Double = gpsTracker.getLongitude()
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
                    Toast.makeText(context, "현재위치 이동 완료", Toast.LENGTH_SHORT).show()
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
                callGetWeekCount.enqueue(object : Callback<List<regionCount>> {
                    override fun onResponse(call: Call<List<regionCount>>, response: Response<List<regionCount>>) {
                        if (response.isSuccessful()) {
                            val resData = Gson().toJson(response.body())
                            Log.d("weekcountrequest", "Successful")

                            localhash_update(resData)
                            poiarr.clear()
                            for (l in localhash) {
                                if (l.value.first == 0) {
                                    continue
                                }
                                val marker0 = MapPOIItem()
                                val point: MapPoint
                                marker0.itemName = l.key + " : " + l.value.first.toString() + "건"
                                marker0.tag = 0
                                marker0.markerType = MapPOIItem.MarkerType.RedPin

                                val tempstr = l.key.split(" ")
                                if (tempstr[0] == "전체") {
                                    continue
                                } else if (tempstr[1] == "전체") {
                                    point = MapPoint.mapPointWithGeoCoord(l.value.second, l.value.third)
                                    marker0.mapPoint = point
                                    marker0.tag = 2
                                    poiarr.add(marker0)
                                    mapView.selectPOIItem(marker0, false)
                                } else {
                                    point = MapPoint.mapPointWithGeoCoord(l.value.second, l.value.third)
                                    marker0.mapPoint = point
                                    poiarr.add(marker0)
                                }
                            }
                            val tempPoint = mutableListOf<MapPOIItem>()
                            if (mapView.zoomLevel < 7) {
                                for (p in poiarr) {
                                    if (p.tag == 0) {
                                        tempPoint.add(p)
                                    }
                                }
                            } else {
                                for (p in poiarr) {
                                    if (p.tag == 2) {
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
        fun backPressedSearchMap(){
            callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewmapbutton.visibility = View.VISIBLE
                    searchmapbutton.visibility = View.VISIBLE
                    requireView().findViewById<View>(R.id.search_map_text).visibility = View.INVISIBLE
                    nextmapbutton.visibility = View.INVISIBLE
                    localsearchbutton.visibility = View.INVISIBLE
                    mapView.removeAllPOIItems()
                    backPressedMainActivity()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        }
        localsearchbutton.setOnClickListener {
            searchFlag = true
            val marker1 = mapView.findPOIItemByTag(1)
            searchedLatitude = marker1.mapPoint.mapPointGeoCoord.latitude.toDouble()
            searchedLongitude = marker1.mapPoint.mapPointGeoCoord.longitude.toDouble()
            (activity as SubActivity).supportFragmentManager.beginTransaction().replace(R.id.frameLayout, mapsearchFragment()).addToBackStack("map").commit()
        }
        if(searchFlag){
            searchFlag = false
            // inactive button
            viewmapbutton.visibility = View.INVISIBLE
            searchmapbutton.visibility = View.INVISIBLE

            // active button
            backPressedSearchMap()
            markerFlag = true
            requireView().findViewById<View>(R.id.search_map_text).visibility = View.VISIBLE
            nextmapbutton.visibility = View.VISIBLE
            localsearchbutton.visibility = View.VISIBLE
            // active function
            val marker1 = MapPOIItem()
            marker1.itemName = ""
            marker1.tag = 1
            val mappoint = MapPoint.mapPointWithGeoCoord(searchedLatitude, searchedLongitude)
            marker1.mapPoint =mappoint
            marker1.markerType = MapPOIItem.MarkerType.BluePin
            marker1.isDraggable = false
            mapView.addPOIItem(marker1)
            mapView.setMapCenterPoint(mappoint, true)

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
            backPressedSearchMap()
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
            marker1.isDraggable = false
            mapView.addPOIItem(marker1)
        }
        val seekbar = requireView().findViewById<SeekBar>(R.id.seekBar2)
        val seekText = requireView().findViewById<View>(R.id.seekText) as TextView
        val check_button = requireView().findViewById<View>(R.id.check_map_button)
        val add_button = requireView().findViewById<View>(R.id.addlocal_map_button)
        val local_button = requireView().findViewById<View>(R.id.map_local_list)
        val recycler = requireView().findViewById<View>(R.id.locallist) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(context)
        val selectedLocalList = mutableListOf<String>()
        val checkedList = mutableListOf<Boolean>()
        val locallistadpater = mapListAdapter(selectedLocalList,checkedList)
        recycler.adapter = locallistadpater
        recycler.setHasFixedSize(true)
        var recyclerflag = false
        fun backPressedNextMap(){
            callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
                    markerFlag = true
                    requireView().findViewById<View>(R.id.search_map_text).visibility = View.VISIBLE
                    nowmapbutton.visibility = View.VISIBLE
                    nextmapbutton.visibility = View.VISIBLE
                    localsearchbutton.visibility = View.VISIBLE
                    recyclerflag = false
                    mapView.removeAllCircles()
                    requireView().findViewById<View>(R.id.seek_layer).visibility = View.INVISIBLE
                    check_button.visibility = View.INVISIBLE
                    add_button.visibility = View.INVISIBLE
                    local_button.visibility = View.INVISIBLE
                    recycler.visibility = View.INVISIBLE
                    backPressedSearchMap()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        }
        nextmapbutton.setOnClickListener{
            // inactive button
            markerFlag = false
            requireView().findViewById<View>(R.id.search_map_text).visibility = View.INVISIBLE
            nowmapbutton.visibility = View.INVISIBLE
            nextmapbutton.visibility = View.INVISIBLE
            localsearchbutton.visibility = View.INVISIBLE
            // active button, function
            backPressedNextMap()
            val circle = MapCircle(mapView.findPOIItemByTag(1).mapPoint, 0, R.color.black, android.graphics.Color.argb(100, 10, 10, 10))
            circle.tag = 0
            mapView.addCircle(circle)
            requireView().findViewById<View>(R.id.seek_layer).visibility = View.VISIBLE
            check_button.visibility = View.VISIBLE
            add_button.visibility = View.VISIBLE
            local_button.visibility = View.VISIBLE
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    seekText.text = ("${progress/100} . ${progress%100}km")
                    circle.radius = progress * 10
                    mapView.removeAllCircles()
                    mapView.addCircle(circle)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    recycler.visibility = View.INVISIBLE
                    recyclerflag = false
                }
            })
        }
        if(resultFlag){
            resultFlag = false
            nowmapbutton.visibility = View.INVISIBLE
            viewmapbutton.visibility = View.INVISIBLE
            searchmapbutton.visibility = View.INVISIBLE

            backPressedNextMap()
            val marker1 = MapPOIItem()
            marker1.itemName = ""
            marker1.tag = 1
            val mappoint = MapPoint.mapPointWithGeoCoord(searchedLatitude, searchedLongitude)
            marker1.mapPoint =mappoint
            marker1.markerType = MapPOIItem.MarkerType.BluePin
            marker1.isDraggable = false
            mapView.addPOIItem(marker1)
            mapView.setMapCenterPoint(mappoint, true)


            val circle = MapCircle(mapView.findPOIItemByTag(1).mapPoint, 0, R.color.black, android.graphics.Color.argb(100, 10, 10, 10))
            circle.tag = 0
            mapView.addCircle(circle)

            requireView().findViewById<View>(R.id.seek_layer).visibility = View.VISIBLE
            check_button.visibility = View.VISIBLE
            add_button.visibility = View.VISIBLE
            local_button.visibility = View.VISIBLE
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    seekText.text = ("${progress/100} . ${progress%100}km")
                    circle.radius = progress * 10
                    mapView.removeAllCircles()
                    mapView.addCircle(circle)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    recycler.visibility = View.INVISIBLE
                    recyclerflag = false
                }
            })

        }
        fun checkButton(){
            var region: String = ""
            val time = Calendar.getInstance()
            val edate = "${time.get(Calendar.YEAR)}/${time.get(Calendar.MONTH) + 1}/${time.get(Calendar.DATE)}"
            time.add(Calendar.DATE, -6)
            val sdate = "${time.get(Calendar.YEAR)}/${time.get(Calendar.MONTH) + 1}/${time.get(Calendar.DATE)}"
            val event = "전염병','자연 재해','기타"
            val page = 1

            val check = locallistadpater.getCheckList()
            val tempList = mutableListOf<String>()
            for (i in 0 until check.size) {
                if (check[i]) {
                    tempList.add(selectedLocalList[i])
                }
            }
            if (tempList.size == 0) {
                Toast.makeText(context, "선택된 지역이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                region = tempList[0]
                for (i in 1 until tempList.size) {
                    region += "','" + tempList[i]
                }
                val baseURL = MyApplication.baseurl
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(spark::class.java)
                val callGetSearch = api.getSearch(region, sdate, edate, event, page)
                callGetSearch.enqueue(object : Callback<ResultGetSearch> {
                    override fun onResponse(call: Call<ResultGetSearch>, response: Response<ResultGetSearch>) {
                        if (response.isSuccessful()) {
                            try {
                                val resData = Gson().toJson(response.body())
                                val cnt = JSONObject(resData).getJSONArray("cnt").getJSONObject(0).getInt("count")
                                mapView.findPOIItemByTag(1).itemName = "선택 지역 : $cnt 건"
                                mapView.selectPOIItem(mapView.findPOIItemByTag(1), true)
                                val resultlist = mutableListOf<String>()
                                val tempobject = JSONObject(resData).getJSONArray("result")
                                for (i in 0 until tempobject.length()){
                                    resultlist.add(tempobject.getJSONObject(i).toString())
                                }
                                var maxpage = cnt/10 + 1
                                if (cnt%10 == 0){
                                    maxpage--
                                }
                                POIitemListener2 = MarkerEventListener2((activity as SubActivity),maxpage,region,sdate,edate,event,resultlist)
                                mapView.setPOIItemEventListener(POIitemListener2)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        } else {
                            Log.d("GetSearch", "responseFail")
                        }
                    }
                    override fun onFailure(call: Call<ResultGetSearch>, t: Throwable) {
                        Log.e("GetSearch", "onFailure")
                    }
                })
            }
        }
        fun addButton(){
            val check = locallistadpater.getCheckList()
            val tempList = mutableListOf<String>()
            for (i in 0 until check.size){
                if(check[i]){
                    tempList.add(selectedLocalList[i])
                }
            }
            for (i in 0 until tempList.size){
                val localTemp = tempList[i].split(" ")
                if(localTemp.size == 1){
                    MyApplication.prefs.savelocal(localTemp[0],"전체")
                }
                else{
                    MyApplication.prefs.savelocal(localTemp[0],localTemp[1])
                }
            }
            Toast.makeText(context,"총 ${tempList.size}개의 지역이 추가 되었습니다.",Toast.LENGTH_SHORT).show()
        }
        fun searchLocal(id:Int){
            recyclerflag = true
            selectedLocalList.clear()
            checkedList.clear()
            val x = mapView.findPOIItemByTag(1).mapPoint.mapPointGeoCoord.longitude
            val y = mapView.findPOIItemByTag(1).mapPoint.mapPointGeoCoord.latitude
            val radius = mapView.findCircleByTag(0).radius
            val url = "http://api.vworld.kr/req/data?service=data&request=GetFeature&data=LT_C_ADSIGG_INFO&key=84F7B2A4-03FC-34AA-B039-11693223A532&&domain=inu.project.spark&crs=EPSG:4019&size=1000" +
                    "&geomFilter=point($x $y)&buffer=$radius"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    Toast.makeText(requireContext(), "connection failed", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val tempstr = response.body?.string().toString()
                    Log.d("vworld", tempstr)
                    try {
                        val json = JSONObject(tempstr).getJSONObject("response").getJSONObject("result").getJSONObject("featureCollection").getJSONArray("features")
                        for (i in 0 until json.length()) {
                            val tempobj = json.getJSONObject(i).getJSONObject("properties").getString("full_nm")
                            val sublocal = tempobj.split(" ")
                            if (sublocal.size > 2) {
                                val str = sublocal[0] + " " + sublocal[1]
                                if (!selectedLocalList.contains(str)) {
                                    selectedLocalList.add(str)
                                    checkedList.add(true)
                                }
                                continue
                            } else if (sublocal.size == 1) {
                                selectedLocalList.add(sublocal[0])
                                checkedList.add(true)
                                continue
                            } else if (sublocal[1].contains('시') && sublocal[1].contains('구')) {
                                val str = sublocal[0] + " " + sublocal[1].split("시")[0] + "시"
                                if (!selectedLocalList.contains(str)) {
                                    selectedLocalList.add(str)
                                    checkedList.add(true)
                                }
                                continue
                            } else {
                                selectedLocalList.add(tempobj)
                                checkedList.add(true)
                            }
                        }
                        (activity as SubActivity).runOnUiThread(Runnable {
                            locallistadpater.notifyDataSetChanged()
                            if(id == local_button.id){
                                recycler.visibility = View.VISIBLE
                            }
                            else if(id ==check_button.id){
                                checkButton()
                            }
                            else if(id == add_button.id){
                                addButton()
                            }
                        })
                        Log.d("response", selectedLocalList.toString())
                    } catch (e: JSONException) {
                        Log.d("JSONExceipton", "error")
                    }
                }
            })
        }
        local_button.setOnClickListener {
            if(!recyclerflag){
                searchLocal(local_button.id)
            }
            else {
                if(recycler.visibility == View.INVISIBLE){
                    recycler.visibility = View.VISIBLE
                }
                else{
                    recycler.visibility = View.INVISIBLE
                }
            }
        }
        check_button.setOnClickListener {
            if (!recyclerflag) {
                searchLocal(check_button.id)
            } else {
                checkButton()
            }
        }
        add_button.setOnClickListener{
            if(!recyclerflag){
                searchLocal(add_button.id)
            }
            else{
                addButton()
            }
        }
    }
    private fun backPressedMainActivity(){
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as SubActivity).gotoMainActiverty()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
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
        if(!searchFlag || !resultFlag){
            mapViewContainer.removeAllViews()
        }
        callback.remove()
        Log.d("resultFlag",resultFlag.toString())
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
                localhash[local1 + " " + localarray2[i]] = Triple(0, local_latitude[i].toDouble(), local_longitude[i].toDouble())
            }
        }
    }
    fun localhash_update(strjson: String){
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
                localhash[key] = Triple(value, localhash[key]!!.second, localhash[key]!!.third)
            }
        }catch (e: JSONException){
            e.printStackTrace()
            Toast.makeText(requireContext(), "json error", Toast.LENGTH_SHORT).show()
        }
    }
    // overriding MapView.MapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.d("zoomlevelchange", p1.toString())
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
                marker1.isDraggable = false
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

    class MarkerEventListener(activity: SubActivity):MapView.POIItemEventListener{
        private val a = activity
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
            if(p1!=null){
                if(p1.tag == 0 || p1.tag == 2){
                    Log.d("onCalloutBalloonOfPOIItemTouched + p2", "true")
                    val region = p1.itemName.split(" : ")[0]
                    val retrofit = Retrofit.Builder()
                            .baseUrl(MyApplication.baseurl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                    val time = Calendar.getInstance()
                    val edate = "${time.get(Calendar.YEAR)}/${time.get(Calendar.MONTH) + 1}/${time.get(Calendar.DATE)}"
                    time.add(Calendar.DATE, -6)
                    val sdate = "${time.get(Calendar.YEAR)}/${time.get(Calendar.MONTH) + 1}/${time.get(Calendar.DATE)}"
                    val event = "전염병','자연 재해','기타"
                    val api = retrofit.create(spark::class.java)
                    val callGetSearch = api.getSearch(region,sdate,edate,event,1)
                    callGetSearch.enqueue(object : Callback<ResultGetSearch> {
                        override fun onResponse(call: Call<ResultGetSearch>, response: Response<ResultGetSearch>) {
                            if (response.isSuccessful()) {
                                try {
                                    val resData = Gson().toJson(response.body())
                                    val cnt = JSONObject(resData).getJSONArray("cnt").getJSONObject(0).getInt("count")
                                    val resultlist = mutableListOf<String>()
                                    val tempobject = JSONObject(resData).getJSONArray("result")
                                    for (i in 0 until tempobject.length()){
                                        resultlist.add(tempobject.getJSONObject(i).toString())
                                    }
                                    var maxpage = cnt/10 + 1
                                    if (cnt%10 == 0){
                                        maxpage--
                                    }
                                    a.fragmentSearchChange(1,maxpage,region,sdate,edate,event,resultlist)
                                    a.backAndReplaceFragment(a.getFragmentSearch())

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                            } else {
                                Log.d("GetSearch", "responseFail")
                            }
                        }
                        override fun onFailure(call: Call<ResultGetSearch>, t: Throwable) {
                            Log.e("GetSearch", "onFailure")
                        }
                    })
                }
            }
        }
        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        }
    }
    class MarkerEventListener2(activity: SubActivity, mp:Int,re:String,sd:String,ed:String,e:String,r:MutableList<String>):MapView.POIItemEventListener{
        private val a = activity
        private val fragment = a.getFragmentMap()
        private val maxpage = mp
        private val region = re
        private val sdate = sd
        private val edate = ed
        private val event = e
        private val resultlist = r

        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {
            if(p1!=null){
                if(p1.tag == 1 && p1.itemName != ""){
                    fragment.resultFlag = true
                    a.fragmentSearchChange(1,maxpage,region,sdate,edate,event,resultlist)

                    a.backAndReplaceFragment(a.getFragmentSearch())
                }
            }
        }
        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        }
    }
}