package inu.project.spark

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class MyJobService : JobService() {
    companion object{
        private val TAG = "MyJobService"
        private val alarmperiodic = 15
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        createNotificationChannel()
        val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceComponent = ComponentName(this, MyJobService1::class.java)
        val jobInfo = JobInfo.Builder(0, serviceComponent)
                .setPeriodic(TimeUnit.MINUTES.toMillis(alarmperiodic.toLong()))
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
            val name = "Project Spark"
            val descriptionText = "Project Spark's alarm"
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
        private val title = "새로운 재난 문자"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // set notification
        val savedlocallist = MyApplication.prefs.getlocal()
        var locallist = "전체 전체"
        if(savedlocallist != null){
            val firstinfo = JSONObject(savedlocallist[0])
            val size = savedlocallist.size
            locallist = firstinfo.getString("local1") + " " +firstinfo.getString("local2")
            for(i in 1 until size){
                try{
                    val localinfo = JSONObject(savedlocallist[i])
                    locallist += "','" + localinfo.getString("local1") + " " +localinfo.getString("local2")
                }
                catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        val baseURL = MyApplication.baseurl
        val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(spark::class.java)

        val callGetNotice = api.getNotice(locallist)
        callGetNotice.enqueue(object: Callback<List<Data>> {
            override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                if(response.isSuccessful()) {
                    val message = Gson().toJson(response.body())
                    val arr = JSONArray(message)
                    val size = arr.length()
                    if (size != 0){
                        Log.d("getNotice",message.toString())
                        // save db in data

                        // create pending intent
                        val resultIntent = Intent(applicationContext,SubActivity::class.java)
                        resultIntent.putExtra("fragment", R.id.button_main_repository)
                        val pendingIntent = PendingIntent.getActivity(applicationContext,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                        val text = size.toString() + "건"
                        val builder = NotificationCompat.Builder(applicationContext,"channal")
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .build()
                        val notificationManager: NotificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(0,builder)
                    }

                    Log.d("getNotice", "Successful")
                } else {

                    Log.d("getNotice", "notSuccessful")
                }
            }
            override fun onFailure(call: Call<List<Data>>, t: Throwable) {

                Log.e("getNotice", "onFailure")
            }
        })
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

}