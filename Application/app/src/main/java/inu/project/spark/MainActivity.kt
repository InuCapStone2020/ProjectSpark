package inu.project.spark

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        class navigationListner : View.OnClickListener{
            override fun onClick(v:View?) {
                Toast.makeText(applicationContext,
                "onclick button",
                Toast.LENGTH_SHORT).show()
            }
        }
        val lis = navigationListner()
        val mToolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        mToolbar.setNavigationOnClickListener(lis)

    }
}