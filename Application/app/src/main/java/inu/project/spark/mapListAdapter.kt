package inu.project.spark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class mapListAdapter(l:MutableList<String>,c:MutableList<Boolean>) : RecyclerView.Adapter<mapListAdapter.MyViewHolder>() {
    private var list = l
    private var checkList = c
    fun getCheckList():MutableList<Boolean>{
        return checkList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mapListAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.map_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: mapListAdapter.MyViewHolder, position: Int) {
        holder.textView.text = list[position]
        holder.check.isChecked = checkList[position]
        holder.check.setOnCheckedChangeListener { buttonView, isChecked ->
            checkList[position] = isChecked
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView: TextView
        var check: CheckBox
        init{
            textView = itemView.findViewById(R.id.map_list_item_text)
            check = itemView.findViewById(R.id.map_list_item_check)
        }
    }
}