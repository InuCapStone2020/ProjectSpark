package inu.project.spark

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class MyJobService : JobService() {
    companion object{
        private val TAG = "MyJobService"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceComponent = ComponentName(this, MyJobService1::class.java)
        val jobInfo = JobInfo.Builder(0, serviceComponent)
                .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                .build()
        js.schedule(jobInfo)
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }
}
class MyJobService1 : JobService(){
    companion object{
        private val TAG = "MyJobService1"
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ${params!!.jobId}")
        return false
    }
}