package inu.project.spark

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.Phaser

class searchFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val datebutton = requireView().findViewById<View>(R.id.search_time_text)
        val localbutton = requireView().findViewById<View>(R.id.search_local_text)
        val eventbutton = requireView().findViewById<View>(R.id.search_event_text)
        //check network service 추가
        //mindate 서버로부터 가져오기
        var minstr:String = "start"

        val baseURL = "http://54.156.38.187:3000"
        // try catch 설정
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(spark::class.java)
        val callGetMindate = api.getMindate()

        callGetMindate.enqueue(object: Callback<List<mindate>> {
            override fun onResponse(call: Call<List<mindate>>, response: Response<List<mindate>>) {
                if(response.isSuccessful()) {
                    minstr = Gson().toJson(response.body())
                    Log.d("getMindate", "Successful")
                } else {
                    minstr = "connection failed"
                    Log.d("getMindate", "notSuccessful")
                }
            }
            override fun onFailure(call: Call<List<mindate>>, t: Throwable) {
                minstr = "connection failed"
                Log.e("getMindate", "onFailure")
            }
        })
        //시간 설정
        var datelist:String = ""
        datebutton.setOnClickListener{
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.search_date_dialog)
            builder.setTitle("날짜 설정")
            builder.window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
            builder.show()
            val start = builder.findViewById<View>(R.id.searchdialog_startdate) as TextView
            val end = builder.findViewById<View>(R.id.searchdialog_enddate)as TextView

            if (datelist !=""){
                start.text = datelist.split("~")[0]
                end.text = datelist.split("~")[1]
            }

            while(true) {
                if (minstr != "start") {
                    Log.d("test",minstr)
                    break
                }
            }
            // connection failed
            if (minstr == "connection failed"){
                Toast.makeText(requireContext(),"서버로부터 데이터 불러오기가 안됩니다.\n 인터넷설정이 제대로 되어있는지 확인하여 주십시오",Toast.LENGTH_LONG).show()
                builder.dismiss()
            }
            // datepicker dialog
            val time = Calendar.getInstance()
            fun l(v:TextView,min:Long,max:Long){
                val year = time.get(Calendar.YEAR)
                val month = time.get(Calendar.MONTH)
                val day = time.get(Calendar.DATE)
                var datestring: String = ""
                val dateListener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        datestring = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                        v.text = datestring
                    }
                }
                val datebuilder = DatePickerDialog(requireContext(), dateListener,year,month,day)
                datebuilder.datePicker.maxDate = max
                datebuilder.datePicker.minDate = min
                datebuilder.show()
            }
            start.setOnClickListener{
                // jsonstring parse to string
                val temparr = JSONArray(minstr)
                val tempobj = temparr.getJSONObject(0)
                val temp = tempobj.getString("mindate")
                val a = temp.split("-")
                val b = Calendar.getInstance()
                b.set(a[0].toInt(),a[1].toInt()-1,(a[2].split("T")[0]).toInt())
                if (end.text.toString() == ""){
                    l(start,b.timeInMillis,time.timeInMillis)
                }
                else{
                    val temp2 = end.text.toString()
                    val a2 = temp2.split("-")
                    val b2 = Calendar.getInstance()
                    b2.set(a2[0].toInt(),a2[1].toInt()-1,a2[2].toInt())
                    l(start,b.timeInMillis,b2.timeInMillis)
                }
            }
            end.setOnClickListener{
                if(start.text.toString() == ""){
                    val temparr = JSONArray(minstr)
                    val tempobj = temparr.getJSONObject(0)
                    val temp = tempobj.getString("mindate")
                    val a = temp.split("-")
                    val b = Calendar.getInstance()
                    b.set(a[0].toInt(),a[1].toInt()-1,(a[2].split("T")[0]).toInt())
                    l(end,b.timeInMillis,time.timeInMillis)
                }
                else{
                    val temp2 = start.text.toString()
                    val a2 = temp2.split("-")
                    val b2 = Calendar.getInstance()
                    b2.set(a2[0].toInt(),a2[1].toInt()-1,a2[2].toInt())
                    l(end,b2.timeInMillis,time.timeInMillis)
                }
            }
            val add = builder.findViewById<View>(R.id.searchdatedialog_ok_button)
            add.setOnClickListener{
                if(start.text.toString() != "" && end.text.toString() != ""){
                    datelist = start.text.toString() + "~" + end.text.toString()
                }
                builder.dismiss()
            }
            val cancle = builder.findViewById<View>(R.id.searchdatedialog_cancle_button)
            cancle.setOnClickListener{
                builder.dismiss()
            }
        }
        // 지역 설정
        val locallist:MutableList<String> = mutableListOf()
        localbutton.setOnClickListener{
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.search_local_dialog)
            builder.window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
            builder.show()
            val recycle = builder.findViewById<View>(R.id.searchdialog_recyclerview) as RecyclerView
            recycle.layoutManager = LinearLayoutManager(context)
            val localadapter = searchdialogAdapter(locallist)
            recycle.adapter = localadapter
            val add = builder.findViewById<View>(R.id.searchdialog_add_button)
            add.setOnClickListener{
                val localbuilder = Dialog(requireContext())
                localbuilder.setContentView(R.layout.local_dialog)
                localbuilder.show()
                val localspinner1: Spinner = localbuilder.findViewById(R.id.local_spinner1) as Spinner
                val localspinner2: Spinner = localbuilder.findViewById(R.id.local_spinner2) as Spinner
                val localarray1 = resources.getStringArray(R.array.local_do)
                val adspinner1 = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, localarray1);
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
                                localarray2 = resources.getStringArray(R.array.local_do_daegu)
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
                        val adspinner2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, localarray2)
                        localspinner2.adapter = adspinner2
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
                var cspinner1: String
                var cspinner2: String
                val localaddbutton: Button = localbuilder.findViewById(R.id.add_local_dialog) as Button
                val localcancelbutton: Button = localbuilder.findViewById(R.id.cancel_local_dialog) as Button
                localaddbutton.setOnClickListener {
                    cspinner1 = localspinner1.selectedItem.toString()
                    cspinner2 = localspinner2.selectedItem.toString()
                    val totalstr = cspinner1 + " " + cspinner2
                    if (totalstr in locallist){
                        Toast.makeText(context,"이미 있는 지역입니다.",Toast.LENGTH_SHORT).show()
                        localbuilder.dismiss()
                    }
                    else if (cspinner1 + " 전체" in locallist){
                        Toast.makeText(context,"해당지역이 포함되어 있습니다.",Toast.LENGTH_SHORT).show()
                        localbuilder.dismiss()
                    }
                    else if ("전체 전체" in locallist){
                        Toast.makeText(context,"모든지역이 포함되어 있습니다.",Toast.LENGTH_SHORT).show()
                        localbuilder.dismiss()
                    }
                    else if (cspinner1 == "전체"){
                        locallist.clear()
                        locallist.add(totalstr)
                        localadapter.notifyDataSetChanged()
                        localbuilder.dismiss()
                    }
                    else if (cspinner2 == "전체"){
                        var t=0
                        while (t < locallist.size){
                            if (cspinner1 == locallist[t].split(" ")[0]){
                                locallist.removeAt(t)
                                t--
                            }
                            t++
                        }
                        Toast.makeText(requireContext(),"${cspinner1}의 지역이 전체설정으로 바뀌었습니다.",Toast.LENGTH_SHORT).show()
                        locallist.add(totalstr)
                        localadapter.notifyDataSetChanged()
                        localbuilder.dismiss()
                    }
                    else{
                        locallist.add(totalstr)
                        // adapter update
                        localadapter.notifyDataSetChanged()
                        localbuilder.dismiss()
                    }
                }
                localcancelbutton.setOnClickListener {
                    localbuilder.dismiss()
                }
            }
            val close = builder.findViewById<View>(R.id.searchdialog_ok_button)
            close.setOnClickListener{
                builder.dismiss()
            }
        }
        val eventlist:MutableList<String> = mutableListOf()
        eventbutton.setOnClickListener{
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.search_event_dialog)
            builder.show()
            val chkall = builder.findViewById<View>(R.id.search_event_chkall) as CheckBox
            val chk1 = builder.findViewById<View>(R.id.search_event_chk1) as CheckBox
            val chk2 = builder.findViewById<View>(R.id.search_event_chk2) as CheckBox
            val chk3 = builder.findViewById<View>(R.id.search_event_chk3) as CheckBox
            if (eventlist.size == 3){
                chkall.isChecked = true
                chk1.isChecked = true
                chk2.isChecked = true
                chk3.isChecked = true
            }
            else{
                for (a in eventlist){
                    if (a == chk1.text.toString()){
                        chk1.isChecked = true
                    }
                    else if (a == chk2.text.toString()){
                        chk2.isChecked = true
                    }
                    else if (a == chk3.text.toString()){
                        chk3.isChecked = true
                    }
                }
            }
            chkall.setOnClickListener{
                if(chkall.isChecked){
                    chk1.isChecked = true
                    chk2.isChecked = true
                    chk3.isChecked = true
                }
                else{
                    chk1.isChecked = false
                    chk2.isChecked = false
                    chk3.isChecked = false
                }
            }

            chk1.setOnCheckedChangeListener { buttonView, isChecked ->
                if(!isChecked){
                    chkall.isChecked = false
                }
                else{
                    if(chk2.isChecked && chk3.isChecked){
                        chkall.isChecked = true
                    }
                }
            }
            chk2.setOnCheckedChangeListener { buttonView, isChecked ->
                if(!isChecked){
                    chkall.isChecked = false
                }
                else{
                    if(chk1.isChecked && chk3.isChecked){
                        chkall.isChecked = true
                    }
                }
            }
            chk3.setOnCheckedChangeListener { buttonView, isChecked ->
                if(!isChecked){
                    chkall.isChecked = false
                }
                else{
                    if(chk2.isChecked && chk1.isChecked){
                        chkall.isChecked = true
                    }
                }
            }
            val cancle = builder.findViewById<View>(R.id.search_event_cancle)
            val ok = builder.findViewById<View>(R.id.search_event_ok)
            cancle.setOnClickListener{
                builder.dismiss()
            }

            ok.setOnClickListener{
                eventlist.clear()
                if(chk1.isChecked){
                    eventlist.add(chk1.text.toString())
                }
                if(chk2.isChecked){
                    eventlist.add(chk2.text.toString())
                }
                if(chk3.isChecked){
                    eventlist.add(chk3.text.toString())
                }
                builder.dismiss()
            }
        }
        val searchbutton = requireView().findViewById<View>(R.id.search_search_button) as Button
        val resultRecycler = requireView().findViewById<View>(R.id.search_recyclerview) as RecyclerView
        val resultbuttonset = requireView().findViewById<View>(R.id.search_button_layout)
        val resultprevbutton = requireView().findViewById<View>(R.id.search_prev_button)
        val resultprevprevbutton = requireView().findViewById<View>(R.id.search_prev_prev_button)
        val resultnextbutton = requireView().findViewById<View>(R.id.search_next_button)
        val resultnextnextbutton = requireView().findViewById<View>(R.id.search_next_next_button)
        val resultpage1 =  requireView().findViewById<View>(R.id.search_page1) as TextView
        val resultpage2 =  requireView().findViewById<View>(R.id.search_page2) as TextView
        val resultpage3 =  requireView().findViewById<View>(R.id.search_page3) as TextView
        val resultpage4 =  requireView().findViewById<View>(R.id.search_page4) as TextView
        val resultpage5 =  requireView().findViewById<View>(R.id.search_page5) as TextView
        val pagelist = listOf(resultpage1,resultpage2,resultpage3,resultpage4,resultpage5)
        // recycler manager define
        val layoutManager = LinearLayoutManager(context)
        resultRecycler.layoutManager = layoutManager
        resultRecycler.setHasFixedSize(true)
        // recycler adapter define
        val resultlist:MutableList<String> = mutableListOf()
        val resultadpater = searchAdapter(resultlist)
        resultRecycler.adapter = resultadpater
        // data list init

        var page = 0
        var maxpage = 0
        var region:String = ""
        var sdate:String = ""
        var edate:String = ""
        var event:String = ""

        fun search(selectpage:Int){
            val callGetSearch = api.getSearch(region, sdate, edate, event, page)
            callGetSearch.enqueue(object : Callback<ResultGetSearch> {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onResponse(call: Call<ResultGetSearch>, response: Response<ResultGetSearch>) {
                    if(response.isSuccessful()) {
                        val resData =  Gson().toJson(response.body())
                        Log.d("TEST", resData)
                        resultlist.clear()
                        val tempobject = JSONObject(resData).getJSONArray("result")
                        for (i in 0 until tempobject.length()){
                            resultlist.add(tempobject.getJSONObject(i).toString())
                        }
                        // notify adapter
                        resultadpater.notifyDataSetChanged()
                        layoutManager.scrollToPosition(0)
                        // page number update
                        if (selectpage != 1 && selectpage != 2 && selectpage != maxpage && selectpage != maxpage-1){
                            var x = -2
                            for (p in pagelist) {
                                p.text = (page + x).toString()
                                p.background = requireContext().getDrawable(R.drawable.borderline)
                                p.setTextColor(Color.BLACK)
                                x++
                            }
                            pagelist[2].background = requireContext().getDrawable(R.drawable.selectedbox)
                            pagelist[2].setTextColor(Color.WHITE)
                        }
                        else if (selectpage == 1 || selectpage == 2){
                            var x = 1
                            for (p in pagelist) {
                                p.text = (x).toString()
                                p.background = requireContext().getDrawable(R.drawable.borderline)
                                p.setTextColor(Color.BLACK)
                                x++
                            }
                            pagelist[selectpage-1].background = requireContext().getDrawable(R.drawable.selectedbox)
                            pagelist[selectpage-1].setTextColor(Color.WHITE)
                        }
                        else{
                            var x = maxpage-4
                            for (p in pagelist) {
                                p.text = (x).toString()
                                p.background = requireContext().getDrawable(R.drawable.borderline)
                                p.setTextColor(Color.BLACK)
                                x++
                            }
                            pagelist[4+selectpage-maxpage].background = requireContext().getDrawable(R.drawable.selectedbox)
                            pagelist[4+selectpage-maxpage].setTextColor(Color.WHITE)
                        }

                    } else {
                        Log.d("TEST", "사실상 실패")
                    }
                }
                override fun onFailure(call: Call<ResultGetSearch>, t: Throwable) {
                    Log.e("TEST", "실패")
                }
            })
        }


        searchbutton.setOnClickListener{
            // region update
            if (locallist.size == 0){
                region = "전체 전체"
            }
            else{
                region = locallist[0]
                for (i in 1 until locallist.size){
                    region += "','" + locallist[i]
                }
            }
            // date update
            if (datelist != ""){
                sdate = datelist.split("~")[0]
                edate = datelist.split("~")[1]
                sdate.replace('-','/')
                edate.replace('-','/')
            }
            else{
                sdate = "2000/01/01"
                edate = "9999/12/31"
            }
            // event update
            if (eventlist.size == 0){
                event = "전염병','자연 재해','기타"
            }
            else{
                event = eventlist[0]
                for (i in (1 until eventlist.size)){
                    event += "','" + eventlist[i]
                }
            }
            page = 1
            val callGetSearch = api.getSearch(region, sdate, edate, event, page)
            callGetSearch.enqueue(object : Callback<ResultGetSearch> {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onResponse(call: Call<ResultGetSearch>, response: Response<ResultGetSearch>) {
                    if(response.isSuccessful()) {
                        val resData =  Gson().toJson(response.body())
                        Log.d("TEST", resData)
                        resultlist.clear()
                        val cnt = JSONObject(resData).getJSONArray("cnt").getJSONObject(0).getInt("count")
                        maxpage = cnt/10 + 1
                        if (cnt%10 == 0){
                            maxpage--
                        }
                        val tempobject = JSONObject(resData).getJSONArray("result")
                        for (i in 0 until tempobject.length()){
                            resultlist.add(tempobject.getJSONObject(i).toString())
                        }
                        // notify adapter
                        resultadpater.notifyDataSetChanged()
                        resultRecycler.visibility = View.VISIBLE
                        resultbuttonset.visibility = View.VISIBLE
                        // page init
                        var x = 1
                        for (p in pagelist){
                            p.text = x.toString()
                            p.background = requireContext().getDrawable(R.drawable.borderline)
                            p.setTextColor(Color.BLACK)
                            p.setOnClickListener {
                                page = Integer.parseInt(p.text.toString())
                                search(page)
                            }
                            x++
                        }
                        pagelist[0].background = requireContext().getDrawable(R.drawable.selectedbox)
                        pagelist[0].setTextColor(Color.WHITE)
                        Log.d("TEST", maxpage.toString())
                    } else {
                        Log.d("TEST", "사실상 실패")
                    }
                }
                override fun onFailure(call: Call<ResultGetSearch>, t: Throwable) {
                    Log.e("TEST", "실패")
                }
            })
        }
        resultprevbutton.setOnClickListener{
            if(page == 1){
                Toast.makeText(context,"첫번쨰 페이지입니다.",Toast.LENGTH_SHORT).show()
            }
            else {
                page -= 1
                search(page)
            }
        }
        resultnextbutton.setOnClickListener{
            if (page == maxpage){
                Toast.makeText(context,"마지막 페이지입니다.",Toast.LENGTH_SHORT).show()
            }
            else{
                page+=1
                search(page)
            }
        }
        resultprevprevbutton.setOnClickListener{
            if(page == 1){
                Toast.makeText(context,"첫번쨰 페이지입니다.",Toast.LENGTH_SHORT).show()
            }
            else {
                page = 1
                search(page)
            }
        }
        resultnextnextbutton.setOnClickListener{
            if (page == maxpage){
                Toast.makeText(context,"마지막 페이지입니다.",Toast.LENGTH_SHORT).show()
            }
            else{
                page=maxpage
                search(page)
            }
        }
    }
}