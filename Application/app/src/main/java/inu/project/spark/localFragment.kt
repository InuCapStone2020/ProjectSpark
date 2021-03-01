package inu.project.spark

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class localFragment : Fragment() {
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

        var listItems:MutableList<String>? = MyApplication.prefs.getlocal()
        val localrecycle = requireView().findViewById<View>(R.id.local_recycle) as RecyclerView
        if (listItems == null) {
            listItems = mutableListOf<String>()
            localrecycle.visibility = View.GONE
        }
        val layoutManager = LinearLayoutManager(context)
        localrecycle.layoutManager = layoutManager
        localrecycle.setHasFixedSize(false)
        val adpater = localAdapter(listItems)
        localrecycle.adapter = adpater

        val add_button = requireView().findViewById<View>(R.id.add_local_button) as Button
        var adspinner1:ArrayAdapter<String>
        var adspinner2:ArrayAdapter<String>
        add_button.setOnClickListener{
            val builder = Dialog(requireContext())
            builder.setContentView(R.layout.local_dialog)
            builder.show()
            val localspinner1:Spinner = builder.findViewById(R.id.local_spinner1) as Spinner
            val localspinner2:Spinner = builder.findViewById(R.id.local_spinner2) as Spinner
            val localarray1 = resources.getStringArray(R.array.local_do)
            adspinner1 = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item,localarray1);
            localspinner1.adapter = adspinner1
            localspinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var localarray2 = resources.getStringArray(R.array.local_do_all)
                    when(adspinner1.getItem(position)){
                        "서울특별시" -> {
                            localarray2 = resources.getStringArray(R.array.local_do_seoul)
                        }
                        "부산광역시" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_busan)
                        }
                        "대구광역시" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_busan)
                        }
                        "인천광역시" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_incheon)
                        }
                        "광주광역시" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_gwangju)
                        }
                        "대전광역시" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_daejeon)
                        }
                        "울산광역시" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_ulsan)
                        }
                        "경기도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_gyeonggido)
                        }
                        "강원도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_gangwondo)
                        }
                        "충청북도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_chungcheongbukdo)
                        }
                        "충청남도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_chungcheongnamdo)
                        }
                        "전라북도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_jeonlabukdo)
                        }
                        "전라남도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_jeonlanamdo)
                        }
                        "경상북도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_gyeongsangbukdo)
                        }
                        "경상남도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_gyeongsangnamdo)
                        }
                        "제주특별자치도" ->{
                            localarray2 = resources.getStringArray(R.array.local_do_jejudo)
                        }
                        else ->{
                            localarray2 = resources.getStringArray(R.array.local_do_all)
                        }
                    }
                    adspinner2 = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item,localarray2);
                    localspinner2.adapter = adspinner2
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            var cspinner1:String = ""
            var cspinner2:String = ""
            val localaddbutton:Button =builder.findViewById(R.id.add_local_dialog) as Button
            val localcancelbutton:Button =builder.findViewById(R.id.cancel_local_dialog) as Button
            localaddbutton.setOnClickListener{
                cspinner1 = localspinner1.selectedItem.toString()
                cspinner2 = localspinner2.selectedItem.toString()
                val saveflag = MyApplication.prefs.savelocal(cspinner1,cspinner2)
                if (saveflag){
                    var list = MyApplication.prefs.getlocal()
                    if (list != null) {
                        listItems.add(list[list.size-1])
                        adpater.notifyItemInserted(list.size)
                        adpater.notifyItemRangeChanged(0,list.size)
                        localrecycle.visibility = View.VISIBLE
                    }
                }
                else{
                    //toast 해당 지역이 저장되어있습니다.
                }
                builder.dismiss()
            }
            localcancelbutton.setOnClickListener{
                builder.dismiss()
            }
        }
    }
}