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

class localFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.local_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*
        val fragmentSetting: settingFragment = settingFragment()
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.setNavigationOnClickListener{
            (activity as SubActivity).replaceFragment(fragmentSetting)
            var title = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub_title) as TextView
            title.setText(R.string.toolbar_setting_name)
        }
        */
        val add_button = requireView().findViewById<View>(R.id.add_local_button) as Button
        add_button.setOnClickListener{
            /*
            Dialog.Builder aBuilder = AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.report_dialog,null);

            aBuilder.setTitle("커스텀 다이얼로그");       // 제목 설정
            aBuilder.setMessage("커스텀 다이얼로그 연습.");   // 내용 설정

            // 스피너 설정
            final Spinner sp = (Spinner)mView.findViewById(R.id.spinner);

            // 스피너 어댑터 설정
            ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
            sp.setAdapter(yearAdapter);

            // editText 설정
            final EditText et = (EditText)findViewById(R.id.editT);
             */
        }

    }
}