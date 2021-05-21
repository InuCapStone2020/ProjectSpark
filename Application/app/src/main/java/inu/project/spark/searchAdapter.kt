package inu.project.spark

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject

class searchAdapter(list:MutableList<String>) : RecyclerView.Adapter<searchAdapter.MyViewHolder>() {
    private var jsonList:MutableList<String> = list
    private val content = "CONTENT"
    private val date = "M_DATE"
    private val time = "M_TIME"
    private val region = "REGION"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val strjson = jsonList[position]
        try{
            val tempobject = JSONObject(strjson)
            val tempregion = tempobject.get(region).toString()
            val tempbody = tempobject.get(content).toString()
            val tempdate = tempobject.get(date).toString().split("T")[0]
            val temptime = tempobject.get(time).toString()
            val tempdatetime = "$tempdate $temptime"
            if(tempobject.get("EVENT").toString().equals("전염병")){
                holder.item_all.setBackgroundResource(R.drawable.round_borderline_red)
            }
            else if(tempobject.get("EVENT").toString().equals("자연재해")){
                holder.item_all.setBackgroundResource(R.drawable.round_borderline_blue)
            }
            else{
                holder.item_all.setBackgroundResource(R.drawable.round_borderline_gray)
            }
            val tempregionarr = tempregion.split(" ")
            var x = ""
            for (i in tempregionarr.indices){
                x += "\n"+tempregionarr[i]
            }
            var y = ""
            val tempbodyarr = tempbody.split("\n")
            for (i in tempbodyarr.indices){
                y+=tempbodyarr[i]
            }
            holder.item_region.text = x
            holder.item_body.text = y
            holder.item_date.text = tempdatetime

        }
        catch(e:JSONException){
            e.printStackTrace()
        }
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var item_region:TextView
        var item_body:TextView
        var item_date:TextView
        var item_all:View
        init{
            item_region = itemView.findViewById<TextView>(R.id.search_item_region)
            item_body = itemView.findViewById<TextView>(R.id.search_item_body)
            item_date = itemView.findViewById<TextView>(R.id.search_item_date)
            item_all = itemView.findViewById<View>(R.id.search_items)
        }
    }
}
