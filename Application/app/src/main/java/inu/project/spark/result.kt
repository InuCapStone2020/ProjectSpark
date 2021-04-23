package inu.project.spark

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("NUM")
    var NUM:Int = 0,
    @SerializedName("SUBNUM")
    var SUBNUM:Int = 0,
    @SerializedName("M_DATE")
    var M_DATE:String = "",
    @SerializedName("M_TIME")
    var M_TIME:String = "",
    @SerializedName("REGION")
    var REGION:String = "",
    @SerializedName("EVENT")
    var EVENT:String = "",
    @SerializedName("CONTENT")
    var CONTENT:String = ""
)

data class countM(
    @SerializedName("count")
    var count:Int = 0
)

data class ResultGetSearch(
    var cnt:List<countM>,
    var result: List<Data>
)


data class regionCount(
    @SerializedName("region")
    var region:String = "",
    @SerializedName("count")
    var count:Int = 0
)


data class mindate(
    @SerializedName("mindate")
    var mindate:String = ""
)