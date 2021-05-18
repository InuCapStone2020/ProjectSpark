package inu.project.spark

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inu.project.spark.MyApplication.Companion.db


class repositoryAdapter(list:MutableList<Contacts>,checkedlist:MutableList<Boolean>) : RecyclerView.Adapter<repositoryAdapter.MyViewHolder>() {

    private var deleteFlag = false
    fun itemDelete(){
        Log.d("itemDelete","x")
        remove()
        notifyDataSetChanged()
        setDeleteFlag(false)
    }
    fun remove(){
        Log.d("remove","removeitem")
        val tempcheck = mutableListOf<Int>()
        for(i in 0 until checklist.size){
            if(checklist[i]){
                tempcheck.add(i)
            }
        }
        for(position in tempcheck){
            db!!.contactsDao().delete(adapterlist[position])
            adapterlist.removeAt(position)
            checklist.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun setDeleteFlag(flag:Boolean){
        deleteFlag = flag
        Log.d("deleteflag",deleteFlag.toString())
        if (!deleteFlag){
            for (i in 0 until checklist.size){
                checklist[i] = false
            }
        }
        notifyDataSetChanged()
    }

    private var adapterlist:MutableList<Contacts> = list
    private var checklist:MutableList<Boolean> = checkedlist

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
        val tempcheck = checklist[position]
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
        holder.item_check.isChecked = tempcheck
        holder.item_all.setOnClickListener {
            if(deleteFlag){
                holder.item_check.isChecked = !holder.item_check.isChecked
            }
        }
        if(deleteFlag){
            holder.item_check.visibility = View.VISIBLE
        }
        else{
            holder.item_check.visibility = View.GONE
        }
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var item_region: TextView
        var item_body: TextView
        var item_date: TextView
        var item_all: View
        var item_check:CheckBox
        init{
            item_region = itemView.findViewById<TextView>(R.id.search_item_region)
            item_body = itemView.findViewById<TextView>(R.id.search_item_body)
            item_date = itemView.findViewById<TextView>(R.id.search_item_date)
            item_all = itemView.findViewById<View>(R.id.search_items)
            item_check = itemView.findViewById<CheckBox>(R.id.reposit_checkBox)
            item_check.setOnCheckedChangeListener { buttonView, isChecked ->
                checklist[adapterPosition] = isChecked
            }
        }
    }
}