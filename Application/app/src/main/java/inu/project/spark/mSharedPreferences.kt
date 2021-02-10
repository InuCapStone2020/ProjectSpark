package inu.project.spark

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.Collections.list

class mSharedPreferences (context: Context){

    private val prefsFilename = "prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    private val alarmkey = "alarm"
    private val alarmarray = "alarmarray"
    private val alarmweek = "week"
    private val alarmstarttime = "start"
    private val alarmendtime = "end"

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
        // create new jsonobject
        val tempjsonobject = JSONObject()
        tempjsonobject.put(alarmweek,checkweek)
        tempjsonobject.put(alarmstarttime,starttime)
        tempjsonobject.put(alarmendtime,endtime)

        // get jsonobject which saved in prefs
        val strjson = prefs.getString(alarmkey,null)
        var alarminfo = JSONObject()
        var arr = JSONArray()
        if (strjson != null){
            try{
                alarminfo = JSONObject(strjson)
                arr = alarminfo.getJSONArray(alarmarray)
            }
            catch(e:JSONException){
                e.printStackTrace()
            }
        }
        arr.put(tempjsonobject)
        alarminfo.put(alarmarray,arr)

        prefs.edit().putString(alarmkey,alarminfo.toString()).apply()
    }
    fun getalarm(): MutableList<String>?{
        // get JsonString in prefs
        val strjson = prefs.getString(alarmkey,null)
        var alarmlist:MutableList<String>? = null
        if (strjson != null){
            try{
                val alarminfo = JSONObject(strjson)
                val arr = alarminfo.getJSONArray(alarmarray)
                val c:Int = arr.length()
                var i:Int = 1
                alarmlist = mutableListOf(arr.getJSONObject(0).toString())
                while(i < c){
                    val tempObject = arr.getJSONObject(i)
                    alarmlist.add(tempObject.toString())
                }
            }
            catch(e:JSONException){
                e.printStackTrace()
            }
        }
        return alarmlist
    }
    fun deletealarm(index:Int){
        var temparr:MutableList<String>? = getalarm()
        if (temparr != null){
            try{
                temparr.removeAt(index)
                val tempjsonarray = JSONArray()
                for(i in temparr){
                    val tempstr = i
                    val tempObject = JSONObject(tempstr)
                    tempjsonarray.put(tempObject)
                }
                val temparrayobject = JSONObject()
                temparrayobject.put(alarmarray,tempjsonarray)
                prefs.edit().putString(alarmkey,temparrayobject.toString()).apply()
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

}