package inu.project.spark

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class alarmFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    override fun onStart() {
        val fragmentSetting: settingFragment = settingFragment()
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as SubActivity).replaceFragment(fragmentSetting)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        super.onStart()
    }
    override fun onStop() {
        callback.remove()
        super.onStop()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentSetting: settingFragment = settingFragment()
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.setNavigationOnClickListener {
            (activity as SubActivity).replaceFragment(fragmentSetting)
            val title = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub_title) as TextView
            title.setText(R.string.toolbar_setting_name)
        }
        return inflater.inflate(R.layout.alarm_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val maddlayout = requireView().findViewById<View>(R.id.add_alarm_layout)

        val madd = requireView().findViewById<View>(R.id.add_alarm_text) as TextView
        madd.setOnClickListener{
            maddlayout.visibility = View.VISIBLE
        }

        val mweek1 = requireView().findViewById<View>(R.id.add_alarm_checkBox1) as CheckBox
        val mweek2 = requireView().findViewById<View>(R.id.add_alarm_checkBox2) as CheckBox
        val mweek3 = requireView().findViewById<View>(R.id.add_alarm_checkBox3) as CheckBox
        val mweek4 = requireView().findViewById<View>(R.id.add_alarm_checkBox4) as CheckBox
        val mweek5 = requireView().findViewById<View>(R.id.add_alarm_checkBox5) as CheckBox
        val mweek6 = requireView().findViewById<View>(R.id.add_alarm_checkBox6) as CheckBox
        val mweek7 = requireView().findViewById<View>(R.id.add_alarm_checkBox7) as CheckBox

        val checklist:List<CheckBox> = listOf(mweek1,mweek2,mweek3,mweek4,mweek5,mweek6,mweek7)

        val mstart = requireView().findViewById<View>(R.id.add_start_alarm) as TextView
        mstart.setOnClickListener{
            textViewtoTimePicker(mstart)
        }
        val mend = requireView().findViewById<View>(R.id.add_end_alarm) as TextView
        mend.setOnClickListener{
            textViewtoTimePicker(mend)
        }
        fun exitaddalarm(){
            for (checkbox in checklist){
                checkbox.setChecked(false)
            }
            mstart.text = "오전 00시 00분"
            mend.text = "오전 00시 00분"
            maddlayout.visibility = View.GONE
        }

        val mcancel = requireView().findViewById<View>(R.id.add_cancel_button)
        mcancel.setOnClickListener{
            exitaddalarm()
        }

        val msave = requireView().findViewById<View>(R.id.add_save_button) as Button

        var listItems:MutableList<String>? = MyApplication.prefs.getalarm()
        val mrecycle = requireView().findViewById<View>(R.id.alarm_recycle) as RecyclerView
        if (listItems == null) {
            listItems = mutableListOf<String>()
            mrecycle.visibility = View.GONE
        }
        val layoutManager = LinearLayoutManager(context)
        mrecycle.layoutManager = layoutManager
        mrecycle.setHasFixedSize(false)
        val adpater = alarmAdapter(listItems)
        mrecycle.adapter = adpater

        msave.setOnClickListener{
            var checkedweek:String = ""
            for (checkbox in checklist){
                if (checkbox.isChecked()){
                    if (checkedweek == "")
                    {
                        checkedweek = checkbox.text.toString()
                    }
                    else{
                        checkedweek = checkedweek + ", " + checkbox.text.toString()
                    }
                }
            }
            if(mstart.text.toString() == "오전 00시 00분" || mend.text.toString() == "오전 00시 00분"){
                Toast.makeText(context,"시간설정을 해주세요",Toast.LENGTH_SHORT).show()
            }
            else{
                MyApplication.prefs.savealarm(checkedweek,mstart.text.toString(),mend.text.toString())
                val list = MyApplication.prefs.getalarm()
                if (list != null) {
                    listItems.add(list[list.size-1])
                    adpater.notifyItemInserted(list.size)
                    adpater.notifyItemRangeChanged(0,list.size)
                    mrecycle.visibility = View.VISIBLE
                }
                exitaddalarm()
            }
        }
        // radio button save and default set
        val mradio = requireView().findViewById<View>(R.id.radio_alarm) as RadioGroup
        val radioi = MyApplication.prefs.getInt("alarm_radio",R.id.radio_alarm1)
        mradio.check(radioi)
        mradio.setOnCheckedChangeListener{
            rgroup: RadioGroup?, checkedId: Int ->
            MyApplication.prefs.setInt("alarm_radio",checkedId)
        }
    }

    private fun textViewtoTimePicker(it:TextView) {
        val time = Calendar.getInstance()
        val hour = time.get(Calendar.HOUR)
        val minute = time.get(Calendar.MINUTE)
        var timestring: String = ""

        val timeListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                var ampm = "오전"
                var h = hourOfDay
                if (hourOfDay >= 12){
                    ampm = "오후"
                    if (hourOfDay != 12){
                        h = hourOfDay%12
                    }
                }
                else if (hourOfDay == 0){
                    h = 12
                }
                timestring = "${ampm} ${h}시 ${minute}분"
                it.text = timestring
            }
        }
        val builder = TimePickerDialog(context, android.R.style.Theme_Holo_Dialog_NoActionBar,timeListener, hour, minute, false)
        builder.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
    }
}

