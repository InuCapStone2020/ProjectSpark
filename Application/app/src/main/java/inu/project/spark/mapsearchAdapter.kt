package inu.project.spark

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

class mapsearchAdapter (list:List<document>) : RecyclerView.Adapter<mapsearchAdapter.MyViewHolder>() {
    private var List:List<document> = list
    private var mListener:OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(text:String,longitude: Double,latitude: Double)
    }
    fun setOnItemClickListner(listener:OnItemClickListener){
        mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mapsearchAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.map_search_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.latitude = List[position].y.toDouble()
        holder.longitude = List[position].x.toDouble()
        holder.textView.text = List[position].address_name
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView: TextView
        var layout:View
        var longitude:Double = 0.0
        var latitude:Double = 0.0
        init{
            textView = itemView.findViewById<TextView>(R.id.map_search_body)
            layout = itemView.findViewById<View>(R.id.map_search_items)
            layout.setOnClickListener{
                if(mListener != null){
                    mListener!!.onItemClick(textView.text.toString(),longitude,latitude)
                }
            }
        }
    }

}