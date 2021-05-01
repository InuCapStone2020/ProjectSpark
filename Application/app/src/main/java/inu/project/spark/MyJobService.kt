package inu.project.spark

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class MyJobService : JobService() {
    companion object{
        private val TAG = "MyJobService"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        createNotificationChannel()
        val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceComponent = ComponentName(this, MyJobService1::class.java)
        val jobInfo = JobInfo.Builder(0, serviceComponent)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .build()
        js.schedule(jobInfo)
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channal name"
            val descriptionText = "channal descirpiton text"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channal", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
class MyJobService1 : JobService(){
    companion object{
        private val TAG = "MyJobService1"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // set notification
        val savedlocallist = MyApplication.prefs.getlocal()
        var text = ""
        if(savedlocallist == null) {
            text = "all"
        }
        else{
            for(l in savedlocallist){
                try{
                    val localinfo = JSONObject(l)
                    text += localinfo.getString("local1") + " " +localinfo.getString("local2")
                }
                catch(e: Exception){
                    e.printStackTrace()
                }

            }
        }
        val builder = NotificationCompat.Builder(this,"channal")
                .setSmallIcon(R.drawable.ic_iconmonstr_bell_8)
                .setContentTitle("Title")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0,builder)
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

}