package inu.project.spark

import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.app.Application
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.min


class alarmFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.alarm_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fragmentSetting: settingFragment = settingFragment()
        /*
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.setNavigationOnClickListener{
            (activity as SubActivity).replaceFragment(fragmentSetting)
            var title = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub_title) as TextView
            title.setText(R.string.toolbar_setting_name)
        }*/
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

        val mcancel = requireView().findViewById<View>(R.id.add_cancel_button) as Button
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
        val adpater = mAdapter(listItems)
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
            MyApplication.prefs.savealarm(checkedweek,mstart.text.toString(),mend.text.toString())
            var list = MyApplication.prefs.getalarm()
            if (list != null) {
                listItems.add(list[list.size-1])
                adpater.notifyItemInserted(list.size)
                adpater.notifyItemRangeChanged(0,list.size)
                mrecycle.visibility = View.VISIBLE
            }

            exitaddalarm()
        }




        // radio button save and default set
        val mradio = requireView().findViewById<View>(R.id.radio_alarm) as RadioGroup
        var radioi = MyApplication.prefs.getInt("alarm_radio",R.id.radio_alarm1)
        mradio.check(radioi)
        mradio.setOnCheckedChangeListener{
            rgroup: RadioGroup?, checkedId: Int ->
            MyApplication.prefs.setInt("alarm_radio",checkedId)
        }
    }

    private fun textViewtoTimePicker(it:TextView) {
        var time = Calendar.getInstance()
        var hour = time.get(Calendar.HOUR)
        var minute = time.get(Calendar.MINUTE)
        var timestring: String = ""

        var timeListener = object : TimePickerDialog.OnTimeSetListener {
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
        var builder = TimePickerDialog(context, android.R.style.Theme_Holo_Dialog_NoActionBar,timeListener, hour, minute, false)
        builder.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
    }
}

