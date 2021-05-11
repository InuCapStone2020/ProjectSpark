package inu.project.spark

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.*


class mapsearchFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    override fun onStart() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as SubActivity).supportFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        super.onStart()
    }
    override fun onStop() {
        callback.remove()
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val mtoolbar = (activity as SubActivity).findViewById<View>(R.id.toolbar_sub) as Toolbar
        mtoolbar.setNavigationOnClickListener {
            (activity as SubActivity).supportFragmentManager.popBackStack()
        }
        return inflater.inflate(R.layout.map_search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val searchMapButton = requireView().findViewById<View>(R.id.map_search_button)
        val mapSearchText = requireView().findViewById<View>(R.id.map_search_text) as TextView
        val recycler = requireView().findViewById<View>(R.id.map_search_recycler) as RecyclerView
        val addresses:MutableList<Address> = mutableListOf()
        val mapsearchadapter = mapsearchAdapter(addresses)

        mapsearchadapter.setOnItemClickListner(object:mapsearchAdapter.OnItemClickListener{
            override fun onItemClick(text:String,logititude: Double,latitude: Double){
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("선택된 위치 이동")
                builder.setMessage(text+"\n해당 주소로 이동하시겠습니까?")
                builder.setCancelable(true)
                builder.setPositiveButton("확인")  { dialogInterface: DialogInterface, i: Int ->
                    (activity as SubActivity).fragmentmapchange(logititude,latitude)
                    (activity as SubActivity).supportFragmentManager.popBackStack()
                }
                builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                }
                builder.create().show()
            }
        })
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = mapsearchadapter
        searchMapButton.setOnClickListener {
            val searchstr = mapSearchText.text.toString()
            val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

            try {
                addresses.clear()
                addresses.addAll(geocoder.getFromLocationName(searchstr,100))
            } catch (ioException: IOException) {
                //네트워크 문제
                Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            } catch (illegalArgumentException:IllegalArgumentException) {
                Toast.makeText(context, "잘못된 주소", Toast.LENGTH_LONG).show()
            }
            if (addresses.size == 0) {
                Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            }
            mapsearchadapter.notifyDataSetChanged()
        }

    }

}