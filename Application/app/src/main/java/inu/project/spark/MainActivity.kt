package inu.project.spark

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // define navigationListener
        class navigationListener : View.OnClickListener {
            // confirm close app function
            override fun onClick(v: View?) {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("close")
                        .setMessage("종료하시겠습니까?")
                        .setPositiveButton("네", DialogInterface.OnClickListener(){
                            dialogInterface: DialogInterface, i: Int ->
                            ActivityCompat.finishAffinity(this@MainActivity)
                            System.exit(0)
                        }
                        )
                        .setNegativeButton("아니오",DialogInterface.OnClickListener(){
                            dialogInterface: DialogInterface, i: Int ->
                        })
                        .show()
            }
        }
        val lis = navigationListener()
        // find toolbar button '>' and set listener
        val mToolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        mToolbar.setNavigationOnClickListener(lis)
        // find each button from Id
        val button_main_map = findViewById<View>(R.id.button_main_map)
        val button_main_setting = findViewById<View>(R.id.button_main_setting)
        val button_main_repository = findViewById<View>(R.id.button_main_repository)
        val button_main_search = findViewById<View>(R.id.button_main_search)
        // set goto each layout by button
        button_main_map.setOnClickListener{
            var i = Intent(this, mapActivity::class.java)
            startActivity(i)
        }
        button_main_setting.setOnClickListener{
            var i = Intent(this, settingActivity::class.java)
            startActivity(i)
        }
        button_main_repository.setOnClickListener{
            var i = Intent(this, repositoryActivity::class.java)
            startActivity(i)
        }
        button_main_search.setOnClickListener{
            var i = Intent(this, searchActivity::class.java)
            startActivity(i)
        }



    }
}