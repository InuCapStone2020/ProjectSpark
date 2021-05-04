package inu.project.spark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class repositoryAdapter(list:MutableList<Contacts>) : RecyclerView.Adapter<repositoryAdapter.MyViewHolder>() {
    private var adapterlist:MutableList<Contacts> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): repositoryAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adapterlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = adapterlist[position]
        val tempbody = contact.CONTENT
        val tempregion = contact.REGION
        val temptime = contact.M_DATE + " " + contact.M_TIME

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
        holder.item_date.text = temptime
        holder.item_all.setOnClickListener {


        }

    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var item_region: TextView
        var item_body: TextView
        var item_date: TextView
        var item_all: View
        init{
            item_region = itemView.findViewById<TextView>(R.id.search_item_region)
            item_body = itemView.findViewById<TextView>(R.id.search_item_body)
            item_date = itemView.findViewById<TextView>(R.id.search_item_date)
            item_all = itemView.findViewById<View>(R.id.search_items)
        }
    }

}