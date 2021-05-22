package inu.project.spark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject

class localAdapter(list:MutableList<String>) : RecyclerView.Adapter<localAdapter.MyViewHolder>() {
    private var jsonList:MutableList<String> = list
    private val local1 = "local1"
    private val local2 = "local2"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): localAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.local_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val strjson = jsonList[position]
        try{
            val tempobject = JSONObject(strjson)
            val templocal1 = tempobject.get(local1).toString()
            val templocal2 = tempobject.get(local2).toString()
            val tempstr = templocal1 + templocal2

            holder.textView1.text = templocal1
            holder.textView2.text = templocal2
        }
        catch(e:JSONException){
            e.printStackTrace()
        }
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView1:TextView
        var textView2:TextView
        var button1:View
        init{
            textView1 = itemView.findViewById<TextView>(R.id.local_text1)
            textView2 = itemView.findViewById<TextView>(R.id.local_text2)
            button1 = itemView.findViewById<View>(R.id.local_delete_button)
            button1.isClickable = true
            button1.setOnClickListener{
                var position = adapterPosition
                remove(position)
            }
        }
    }
    private fun remove(position: Int){
        MyApplication.prefs.deletelocal(position)
        jsonList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,jsonList.size)
    }
}