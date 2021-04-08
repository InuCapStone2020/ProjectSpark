package inu.project.spark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class searchdialogAdapter(list:MutableList<String>) : RecyclerView.Adapter<searchdialogAdapter.MyViewHolder>() {
    private var List:MutableList<String> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchdialogAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.searchdialog_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = List[position]
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView: TextView
        var button1: Button
        init{
            textView = itemView.findViewById<TextView>(R.id.searchdailog_text_item)
            button1 = itemView.findViewById<Button>(R.id.searchdailog_button)
            button1.setOnClickListener{
                val position = adapterPosition
                remove(position)
            }
        }
    }
    private fun remove(position: Int){
        List.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,List.size)
    }
}