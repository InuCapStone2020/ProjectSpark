package inu.project.spark

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class repositoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        return inflater.inflate(R.layout.repository_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerview = requireView().findViewById<RecyclerView>(R.id.repository_RecyclerView)
        val repoistlist = mutableListOf<Contacts>()

        val db = MyApplication.db
        val savedContacts = db!!.contactsDao().getAll()
        if(savedContacts.isNotEmpty()){
            repoistlist.addAll(savedContacts)
        }
        val repositadapter = repositoryAdapter(repoistlist)
        val layoutmanager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutmanager
        recyclerview.adapter = repositadapter
    }
}