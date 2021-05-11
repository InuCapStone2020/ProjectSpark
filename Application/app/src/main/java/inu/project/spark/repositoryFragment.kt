package inu.project.spark

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class repositoryFragment : Fragment() {
    companion object{
        private val db = MyApplication.db
        private val repoistlist = mutableListOf<Contacts>()
        private val checkedlist = mutableListOf<Boolean>()
        private val repositadapter = repositoryAdapter(repoistlist,checkedlist)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.inflateMenu(R.menu.menu_repository)
        mtoolbar.setNavigationOnClickListener {
            val i = Intent(context, MainActivity::class.java)
            startActivity(i)
        }
        mtoolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menu_repository_all->{ // 전체 삭제
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("전체 삭제")
                    builder.setMessage("전체 삭제 하시겠습니까?")
                    builder.setCancelable(true)
                    builder.setPositiveButton("확인")  { dialogInterface: DialogInterface, i: Int ->
                        db!!.contactsDao().deleteAll()
                        repoistlist.clear()
                        checkedlist.clear()
                        repositadapter.notifyDataSetChanged()
                    }
                    builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.cancel()
                    }
                    builder.create().show()
                }
                R.id.menu_repository_select->{ // 선택 삭제
                    repositadapter.setDeleteFlag(true)
                    val deletebutton = requireView().findViewById<View>(R.id.repository_delete)
                    val canclebutton = requireView().findViewById<View>(R.id.repository_cancle)
                    val buttonLayout = requireView().findViewById<View>(R.id.repository_button_layout)
                    buttonLayout.visibility = View.VISIBLE
                    deletebutton.setOnClickListener{
                        repositadapter.itemDelete()
                        buttonLayout.visibility = View.GONE
                    }
                    canclebutton.setOnClickListener {
                        repositadapter.setDeleteFlag(false)
                        buttonLayout.visibility = View.GONE
                    }
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
            return@setOnMenuItemClickListener true

        }
        return inflater.inflate(R.layout.repository_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerview = requireView().findViewById<RecyclerView>(R.id.repository_RecyclerView)

        val savedContacts = db!!.contactsDao().getAll()
        if(savedContacts.isNotEmpty()){
            repoistlist.addAll(savedContacts)
            for(i in 0 until repoistlist.size){
                checkedlist.add(false)
            }
            repositadapter.notifyDataSetChanged()
        }
        val layoutmanager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutmanager
        recyclerview.adapter = repositadapter
    }

    override fun onPause() {
        repositadapter.setDeleteFlag(false)
        Log.d("onstop","repoist")
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.menu.clear()
        super.onPause()
    }
}