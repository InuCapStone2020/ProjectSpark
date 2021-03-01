package inu.project.spark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject

class alarmAdapter(list:MutableList<String>) : RecyclerView.Adapter<alarmAdapter.MyViewHolder>() {
    private var jsonList:MutableList<String> = list
    private val alarmweek = "week"
    private val alarmstarttime = "start"
    private val alarmendtime = "end"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): alarmAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.alarm_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val strjson = jsonList[position]
        try{
            val tempobject = JSONObject(strjson)
            val tempweek = tempobject.get(alarmweek).toString()
            val tempstart = tempobject.get(alarmstarttime).toString()
            val tempend = tempobject.get(alarmendtime).toString()
            val temptime = tempstart + " ~ " + tempend

            holder.textView1.text = tempweek
            holder.textView2.text = temptime
        }
        catch(e:JSONException){
            e.printStackTrace()
        }
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView1:TextView
        var textView2:TextView
        var button1:Button
        init{
            textView1 = itemView.findViewById<TextView>(R.id.alarm_item1)
            textView2 = itemView.findViewById<TextView>(R.id.alarm_item2)
            button1 = itemView.findViewById<Button>(R.id.alarm_delete)
            button1.setOnClickListener{
                var position = adapterPosition
                remove(position)
            }
        }
    }
    private fun remove(position: Int){
        MyApplication.prefs.deletealarm(position)
        jsonList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,jsonList.size)
    }
}