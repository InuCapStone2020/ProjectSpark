package inu.project.spark

import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface spark {
    @GET("/search")
    fun getSearch(@Query("region")region:String,
                  @Query("sdate")sdate:String,
                  @Query("edate")edate:String,
                  @Query("event")event:String,
                  @Query("page")page:Int
    ): Call<ResultGetSearch>

    @GET("/notice")
    fun getNotice(@Query("region")region:String,
                  @Query("interval")interval:Int
    ): Call<List<Data>>


    @GET("/weekdetail")
    fun getWeekDetail(@Query("region")region:String
    ): Call<List<Data>>


    @GET("/weekcount")
    fun getWeekCount(): Call<List<regionCount>>

    @GET("/mindate")
    fun getMindate(): Call<List<mindate>>

}