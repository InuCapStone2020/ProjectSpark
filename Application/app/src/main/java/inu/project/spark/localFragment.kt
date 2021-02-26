package inu.project.spark

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

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
        val add_button = requireView().findViewById<View>(R.id.add_local_button) as Button
        add_button.setOnClickListener{
            var builder = Dialog(requireContext())
            builder.setContentView(R.layout.local_dialog)
            builder.show()
            val localspinner1:Spinner = builder.findViewById(R.id.local_spinner1) as Spinner
            val localspinner2:Spinner = builder.findViewById(R.id.local_spinner2) as Spinner
            



            val localaddbutton:Button =builder.findViewById(R.id.add_local_dialog) as Button
            val localcancelbutton:Button =builder.findViewById(R.id.cancel_local_dialog) as Button
            localaddbutton.setOnClickListener{
                builder.dismiss()
            }
            localcancelbutton.setOnClickListener{
                builder.dismiss()
            }
        }

    }


}