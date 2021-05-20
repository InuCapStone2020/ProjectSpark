package inu.project.spark

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
        val addresses:MutableList<document> = mutableListOf()
        val mapsearchadapter = mapsearchAdapter(addresses)

        mapsearchadapter.setOnItemClickListner(object:mapsearchAdapter.OnItemClickListener{
            override fun onItemClick(text:String, longitude:Double, latitude: Double){
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("선택된 위치 이동")
                builder.setMessage(text+"\n해당 주소로 이동하시겠습니까?")
                builder.setCancelable(true)
                builder.setPositiveButton("확인")  { dialogInterface: DialogInterface, i: Int ->
                    (activity as SubActivity).fragmentmapchange(longitude,latitude)
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
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://dapi.kakao.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val api = retrofit.create(mapAPI::class.java)
            api.getSearchAddress(getString(R.string.rest_api_key),searchstr,15).enqueue(object : Callback<addressResponse>{
                override fun onResponse(call: Call<addressResponse>, response: Response<addressResponse>) {
                    Log.d("getSearchAddress",response.body().toString())
                    if(response.isSuccessful()){

                        addresses.clear()
                        addresses.addAll(response.body()!!.document)
                        mapsearchadapter.notifyDataSetChanged()
                        Log.d("getSearchAddress","success")
                    }
                    else{
                        Log.d("getSearchAddress",response.body().toString())
                    }
                }
                override fun onFailure(call: Call<addressResponse>, t: Throwable) {

                }

            })

        }

    }

}