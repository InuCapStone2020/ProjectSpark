package inu.project.spark

import android.app.Application


class MyApplication : Application(){
    companion object{
        lateinit var prefs : mSharedPreferences
    }
    override fun onCreate(){
        prefs = mSharedPreferences(applicationContext)
        super.onCreate()
    }
}
