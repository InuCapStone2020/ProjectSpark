package inu.project.spark

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        //시간 설정
        val datelist:MutableList<String> = mutableListOf()
        datebutton.setOnClickListener{
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.search_dialog)
            builder.window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
            builder.show()
            val add = builder.findViewById<View>(R.id.searchdialog_add_button)
            add.setOnClickListener{
                // insert datepicker
            }
            val recycle = builder.findViewById<View>(R.id.searchdialog_recyclerview) as RecyclerView
            recycle.layoutManager = LinearLayoutManager(context)
            val dateadapter = searchdialogAdapter(datelist)
            recycle.adapter = dateadapter
            val close = builder.findViewById<View>(R.id.searchdialog_ok_button)
            close.setOnClickListener{
                builder.dismiss()
            }
        }
        // 지역 설정
        val locallist:MutableList<String> = mutableListOf()
        localbutton.setOnClickListener{
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.search_dialog)
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
                    // 중복처리함수 추가 => cspinner2 == 전체 일때경우 추가
                    if (totalstr in locallist){
                        Toast.makeText(context,"이미 있는 지역입니다.",Toast.LENGTH_SHORT).show()
                        localbuilder.dismiss()
                    }
                    else if (cspinner1 + "전체" in locallist){
                        Toast.makeText(context,"해당지역이 포함되어 있습니다.",Toast.LENGTH_SHORT).show()
                        localbuilder.dismiss()
                    }
                    else if ("전체 전체" in locallist){
                        Toast.makeText(context,"모든지역이 포함되어 있습니다.",Toast.LENGTH_SHORT).show()
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
    }
    /*

    fun btnClick(view: View) {
        val url = "http://54.147.58.83/selectregionall.php?region="
        var edit1 = findViewById<EditText>(R.id.edit1)
        var inputtext = edit1.text.toString()
        var text = ""
        url = url + inputtext
        var text1 = findViewById<TextView>(R.id.text1)
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                text="connection failed"
                runOnUiThread(Runnable { text1.setText(text) })
            }
            override fun onResponse(call: Call, response: Response) {
                text= response?.body?.string().toString()
                runOnUiThread(Runnable { text1.setText(text) })
            }
        })
    }
    */
}