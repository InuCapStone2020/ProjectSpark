package inu.project.spark

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log
import java.util.*


class MyApplication : Application(){
    companion object{
        val baseurl = "http://54.156.38.187:3000"
        lateinit var prefs : mSharedPreferences
        private val alarmminute = 15
    }
    override fun onCreate(){
        prefs = mSharedPreferences(applicationContext)
        // alarm schedule set
        //prefs.delBoolean("alarmflag")
        Log.d("boolean",prefs.getBoolean("alarmflag",false).toString())
        if(!prefs.getBoolean("alarmflag",false)){
            val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val serviceComponent = ComponentName(this, MyJobService::class.java)
            js.cancelAll()
            val celandar = Calendar.getInstance()
            val minute = ((60 + alarmminute - celandar.get(Calendar.MINUTE))%60)*1000*60
            Log.d("minute",minute.toLong().toString())
            val jobInfo = JobInfo.Builder(0, serviceComponent)
                    .setMinimumLatency(minute.toLong())
                    .setOverrideDeadline(minute.toLong())
                    .build()
            js.schedule(jobInfo)
            prefs.setBoolean("alarmflag",true)
        }
        super.onCreate()
    }
}
