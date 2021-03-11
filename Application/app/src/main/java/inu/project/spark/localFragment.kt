package inu.project.spark

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.*


class localFragment : Fragment() {
    private var llistItems:MutableList<String>? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val fragmentSetting: settingFragment = settingFragment()
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.setNavigationOnClickListener {
            (activity as SubActivity).replaceFragment(fragmentSetting)
            var title = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub_title) as TextView
            title.setText(R.string.toolbar_setting_name)
        }
        return inflater.inflate(R.layout.local_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        llistItems = MyApplication.prefs.getlocal()
        val ladpater = localAdapter(llistItems!!)
        val localrecycle = requireView().findViewById<View>(R.id.local_recycle) as RecyclerView
        if (llistItems == null) {
            llistItems = mutableListOf<String>()
            localrecycle.visibility = View.GONE
        }
        val layoutManager = LinearLayoutManager(context)
        localrecycle.layoutManager = layoutManager
        localrecycle.setHasFixedSize(false)
        localrecycle.adapter = ladpater

        val add_button = requireView().findViewById<View>(R.id.add_local_button) as Button
        var adspinner1:ArrayAdapter<String>
        var adspinner2:ArrayAdapter<String>
        add_button.setOnClickListener {
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.local_dialog)
            builder.show()
            val localspinner1: Spinner = builder.findViewById(R.id.local_spinner1) as Spinner
            val localspinner2: Spinner = builder.findViewById(R.id.local_spinner2) as Spinner
            val localarray1 = resources.getStringArray(R.array.local_do)
            adspinner1 = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, localarray1);
            localspinner1.adapter = adspinner1
            localspinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var localarray2 = resources.getStringArray(R.array.local_do_all)
                    when (adspinner1.getItem(position)) {
                        "서울특별시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_seoul)
                        }
                        "부산광역시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_busan)
                        }
                        "대구광역시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_busan)
                        }
                        "인천광역시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_incheon)
                        }
                        "광주광역시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_gwangju)
                        }
                        "대전광역시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_daejeon)
                        }
                        "울산광역시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_ulsan)
                        }
                        "경기도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_gyeonggido)
                        }
                        "강원도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_gangwondo)
                        }
                        "충청북도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_chungcheongbukdo)
                        }
                        "충청남도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_chungcheongnamdo)
                        }
                        "전라북도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_jeonlabukdo)
                        }
                        "전라남도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_jeonlanamdo)
                        }
                        "경상북도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_gyeongsangbukdo)
                        }
                        "경상남도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_gyeongsangnamdo)
                        }
                        "제주특별자치도" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_jejudo)
                        }
                        else -> {
                            localarray2 = resources.getStringArray(R.array.local_do_all)
                        }
                    }
                    adspinner2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, localarray2)
                    localspinner2.adapter = adspinner2
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            var cspinner1: String
            var cspinner2: String
            val localaddbutton: Button = builder.findViewById(R.id.add_local_dialog) as Button
            val localcancelbutton: Button = builder.findViewById(R.id.cancel_local_dialog) as Button
            localaddbutton.setOnClickListener {
                cspinner1 = localspinner1.selectedItem.toString()
                cspinner2 = localspinner2.selectedItem.toString()
                addlocal(cspinner1,cspinner2)
                builder.dismiss()
            }
            localcancelbutton.setOnClickListener {
                builder.dismiss()
            }
        }
        val nowaddbutton = requireView().findViewById<View>(R.id.add_now_button)as Button
        nowaddbutton.setOnClickListener{
            if (!checkLocationServicesStatus()) {
                showDialogForLocationServiceSetting()
            }
            else{
                if(checkRunTimePermission()){
                    gpsTracker = GpsTracker(requireContext())
                    val latitude:Double = gpsTracker.getLatitude()
                    val longitude:Double = gpsTracker.getLongitude()
                    val address:String = getCurrentAddress(latitude,longitude)
                    val test = requireView().findViewById<View>(R.id.testforgps) as TextView
                    test.text = address
                    addDialog(address)
                }
            }
        }
    }
    private val REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    private lateinit var gpsTracker:GpsTracker

    fun getCurrentAddress( latitude:Double, longitude:Double):String {
        val geocoder:Geocoder = Geocoder(context, Locale.getDefault())
        var addresses = listOf<Address>()
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException:IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address:Address = addresses.get(0);
        var str=""
        if (address.countryCode != "KR"){
            Toast.makeText(context, "국내 주소가 아닙니다.", Toast.LENGTH_LONG).show()
            return address.countryCode
        }
        str+=address.getAddressLine(0).toString().split(" ")[1] + " " + address.getAddressLine(0).toString().split(" ")[2]
        return str
    }
    fun checkRunTimePermission():Boolean{
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission:Int = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission:Int = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(context, "이 기능을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE)
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE)
            }
        }
        return false
    }

    fun checkLocationServicesStatus():Boolean {
        val locationManager:LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun addDialog(local:String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("현재 위치 추가")
        builder.setMessage(local + "\n 추가 하시겠습니까?")
        builder.setCancelable(true)
        builder.setPositiveButton("확인")  { dialogInterface: DialogInterface, i: Int ->
            val cspinner1 = local.split(" ")[0]
            val cspinner2 = local.split(" ")[1]
            addlocal(cspinner1,cspinner2)
        }
        builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        }
        builder.create().show()
    }
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n 위치 설정을 수정하시겠습니까?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정")  { dialogInterface: DialogInterface, i: Int ->
            val callGPSSettingIntent: Intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE )
        }
        builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        }
        builder.create().show()
    }
    private fun addlocal(cspinner1:String,cspinner2:String){
        val saveflag = MyApplication.prefs.savelocal(cspinner1, cspinner2)
        val localrecycle = requireView().findViewById<View>(R.id.local_recycle) as RecyclerView
        val ladpater = localAdapter(llistItems!!)
        if (saveflag) {
            val list = MyApplication.prefs.getlocal()
            if (list != null) {
                llistItems!!.add(list[list.size - 1])
                ladpater.notifyItemInserted(list.size)
                ladpater.notifyItemRangeChanged(0, list.size)
                localrecycle.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(context, "해당 지역이 저장 되어 있습니다.", Toast.LENGTH_LONG).show()
        }

    }
}
