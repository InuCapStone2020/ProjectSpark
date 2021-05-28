package com.example.retrofittest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        request()
    }

    private fun request() {
        val baseURL = "http://100.26.178.18:3000"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(spark::class.java)

        // weekdetail
        val callGetWeekDetail = api.getWeekDetail("인천광역시 연수구")

        callGetWeekDetail.enqueue(object :
            Callback<List<Data>> {
            override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                if(response.isSuccessful()) {
                    var resData = response.body()
                    Log.d("TEST", Gson().toJson(resData))
                } else {
                    Log.d("TEST", "사실상 실패")
                }
            }
            override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                Log.e("TEST", "실패")
            }
        })


        // search
        val callGetSearch = api.getSearch("인천광역시 연수구", "2021/02/23", "2021/04/14", "전염병','자연 재해", 1)

        callGetSearch.enqueue(object : Callback<ResultGetSearch> {
            override fun onResponse(call: Call<ResultGetSearch>, response: Response<ResultGetSearch>) {
                if(response.isSuccessful()) {
                    var resData = response.body()
                    Log.d("TEST", Gson().toJson(resData))
                } else {
                    Log.d("TEST", "사실상 실패")
                }
            }

            override fun onFailure(call: Call<ResultGetSearch>, t: Throwable) {
                Log.e("TEST", "실패")
            }
        })


        // notice
        val callGetNotice = api.getNotice("인천광역시 연수구")

        callGetNotice.enqueue(object : Callback<List<Data>> {
            override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                if(response.isSuccessful()) {
                    var resData = response.body()
                    Log.d("TEST", Gson().toJson(resData))
                } else {
                    Log.d("TEST", "사실상 실패")
                }
            }

            override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                Log.e("TEST", "실패")
            }
        })


        // mindate
        val callGetMindate = api.getMindate()

        callGetMindate.enqueue(object: Callback<List<mindate>> {
            override fun onResponse(call: Call<List<mindate>>, response: Response<List<mindate>>) {
                if(response.isSuccessful()) {
                    var resData = response.body()
                    Log.d("TEST", Gson().toJson(resData))
                } else {
                    Log.d("TEST", "사실상 실패")
                }
            }

            override fun onFailure(call: Call<List<mindate>>, t: Throwable) {
                Log.e("TEST", "실패")
            }
        })


        // weekcount
        val callGetWeekCount = api.getWeekCount()

        callGetWeekCount.enqueue(object: Callback<List<regionCount>> {
            override fun onResponse(call: Call<List<regionCount>>, response: Response<List<regionCount>>) {
                if(response.isSuccessful()) {
                    var resData = response.body()
                    Log.d("TEST", Gson().toJson(resData))
                } else {
                    Log.d("TEST", "사실상 실패")
                }
            }

            override fun onFailure(call: Call<List<regionCount>>, t: Throwable) {
                Log.e("TEST", "실패")
            }
        })

    }
}

