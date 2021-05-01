package inu.project.spark

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import inu.project.spark.MyApplication.Companion.prefs
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.minutes


class MainActivity : AppCompatActivity() {
    private var notificationManager: NotificationManager? = null
    @ExperimentalTime
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
                        .setPositiveButton(
                            "네",
                            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                                ActivityCompat.finishAffinity(this@MainActivity)
                                System.exit(0)
                            }
                        )
                        .setNegativeButton(
                            "아니오",
                            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
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
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_map)
            startActivity(i)
        }
        button_main_setting.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_setting)
            startActivity(i)
        }
        button_main_repository.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_repository)
            startActivity(i)
        }
        button_main_search.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_search)
            startActivity(i)
        }
        // alarm schedule set
        prefs.delBoolean("alarmflag")
        Log.d("boolean",prefs.getBoolean("alarmflag",false).toString())
        if(!prefs.getBoolean("alarmflag",false)){
            val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val serviceComponent = ComponentName(this, MyJobService::class.java)
            val celandar = Calendar.getInstance()
            val minute = (44 - celandar.get(Calendar.MINUTE))*1000*60
            Log.d("minute",minute.toLong().toString())
            val jobInfo = JobInfo.Builder(0, serviceComponent)
                    .setMinimumLatency(minute.toLong())
                    .setOverrideDeadline(minute.toLong())
                    .build()
            js.schedule(jobInfo)
            prefs.setBoolean("alarmflag",true)

             /*
            val celandar = Calendar.getInstance()
            val minute = (60 - celandar.get(Calendar.MINUTE))*60*1000

            val timer = Timer()
            val task = object: TimerTask(){
                override fun run(){
                    notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    //createNotificationChanel("id","name","description")
                }
            }
            timer.schedule(task,minute.toLong(),60*60*1000)

              */
        }

    }
    /*
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel(id:String, name:String, description:String){
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id,name,importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100,200,300,400,500,400,300,200,100)
        notificationManager?.createNotificationChannel(channel)
    }

     */
    /*
    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }
    }

     */
}