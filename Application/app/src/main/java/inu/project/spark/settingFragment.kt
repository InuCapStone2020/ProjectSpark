package inu.project.spark

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar

class settingFragment : Fragment() {
    private val alarmFragment = alarmFragment()
    private val localFragment = localFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button_setting_alarm = requireView().findViewById<View>(R.id.button_setting_alarm)
        val button_setting_local = requireView().findViewById<View>(R.id.button_setting_local)
        button_setting_alarm.setOnClickListener{
            (activity as SubActivity).replaceFragment(alarmFragment)
            var title = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub_title) as TextView
            title.setText(R.string.toolbar_alarm_name)
        }
        button_setting_local.setOnClickListener{
            (activity as SubActivity).replaceFragment(localFragment)
            var title = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub_title) as TextView
            title.setText(R.string.toolbar_local_name)
        }
    }

}