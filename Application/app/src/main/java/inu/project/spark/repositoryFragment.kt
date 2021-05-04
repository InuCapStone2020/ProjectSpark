package inu.project.spark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val db = AppDatabase.getInstance(requireContext())

    }
}