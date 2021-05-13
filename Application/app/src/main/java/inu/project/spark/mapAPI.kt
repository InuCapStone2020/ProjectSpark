package inu.project.spark

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


interface mapAPI {
    @GET("/v2/local/search/address.json")
    fun getSearchAddress(
            @Header("Authorization") token: String,
            @Query("query") query: String,
            @Query("size")size:Int
    ): Call<addressResponse>
}