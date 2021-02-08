package inu.project.spark

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class mSharedPreferences (context: Context){

    private val prefsFilename = "prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    private val alarmkey = "alarm"

    fun getString(key:String, defValue:String):String{
        return prefs.getString(key,defValue).toString()
    }
    fun setString(key:String, str:String){
        prefs.edit().putString(key, str).apply()
    }

    fun getInt(key:String, defValue: Int):Int{
        return prefs.getInt(key,defValue).toInt()
    }
    fun setInt(key:String, int: Int){
        prefs.edit().putInt(key, int).apply()
    }
    fun savealarm(checkweek:String, starttime:String,endtime:String){
        
    }
    fun deletealarm(){

    }

}