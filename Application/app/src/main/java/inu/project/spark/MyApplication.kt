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
        private val initflag = true
    }
    override fun onCreate(){
        prefs = mSharedPreferences(applicationContext)
        // alarm schedule set
        if (initflag){
            prefs.delBoolean("alarmflag")
        }
        if(!prefs.getBoolean("alarmflag",false)){
            //db

            //jobscheduler
            val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val serviceComponent = ComponentName(this, MyJobService::class.java)
            js.cancelAll()
            val celandar = Calendar.getInstance()
            val minute = ((60 + alarmminute - celandar.get(Calendar.MINUTE))%60)*1000*60
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
