package inu.project.spark

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data(
        @SerializedName("NUM")
        var NUM: Int = 0,
        @SerializedName("SUBNUM")
        var SUBNUM: Int = 0,
        @SerializedName("M_DATE")
        var M_DATE: String = "",
        @SerializedName("M_TIME")
        var M_TIME: String = "",
        @SerializedName("REGION")
        var REGION: String = "",
        @SerializedName("EVENT")
        var EVENT: String = "",
        @SerializedName("CONTENT")
        var CONTENT: String = ""
)

data class countM(
        @SerializedName("count")
        var count: Int = 0
)

data class ResultGetSearch(
        var cnt: List<countM>,
        var result: List<Data>
)


data class regionCount(
        @SerializedName("region")
        var region: String = "",
        @SerializedName("count")
        var count: Int = 0
)


data class mindate(
        @SerializedName("mindate")
        var mindate: String = ""
)
/*
class addressResponse
    : Parcelable {
    @SerializedName("meta")
    @Expose
    lateinit var meta: meta
    @SerializedName("document")
    @Expose
    lateinit var document: List<document>

    fun getmeta():meta{
        return meta
    }
    fun setmeta(meta:meta){
        this.meta = meta
    }
    fun getDocuments(): List<document> {
        return document
    }
    fun setDocuments(document: List<document>) {
        this.document = document
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(this.meta, flags);
        parcel.writeList(this.document);
    }

    override fun describeContents(): Int {
        return 0
    }
    constructor() {}
    protected constructor(i: Parcel) {
        meta = i.readParcelable(meta::class.java.getClassLoader())!!
        document = ArrayList<document>()
        i.readList(document, document::class.java.getClassLoader())
    }
    companion object CREATOR : Parcelable.Creator<addressResponse> {
        override fun createFromParcel(parcel: Parcel): addressResponse {
            return addressResponse(parcel)
        }

        override fun newArray(size: Int): Array<addressResponse?> {
            return arrayOfNulls(size)
        }
    }
}

class meta(
        @SerializedName("total_count")
        var total_count: Int,
        @SerializedName("pageable_count")
        var pageable_count: Int,
        @SerializedName("is_end")
        var is_end: Boolean
):Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(total_count)
        parcel.writeInt(pageable_count)
        parcel.writeByte(if (is_end) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<meta> {
        override fun createFromParcel(parcel: Parcel): meta {
            return meta(parcel)
        }

        override fun newArray(size: Int): Array<meta?> {
            return arrayOfNulls(size)
        }
    }
}


 */

data class addressResponse(
    @SerializedName("meta")
    var meta: meta,
    @SerializedName("documents")
    var document: List<document>
)
data class meta(
        @SerializedName("total_count")
        var total_count: Int,
        @SerializedName("pageable_count")
        var pageable_count: Int,
        @SerializedName("is_end")
        var is_end: Boolean
)

data class document(
        @SerializedName("address_name")
        var address_name: String,
        @SerializedName("y")
        var y: String,
        @SerializedName("x")
        var x: String,
        @SerializedName("address_type")
        var address_type: String,
        @SerializedName("address")
        var address: address,
        @SerializedName("road_address")
        var road_address: road_address
)

data class address(
        @SerializedName("address_name")
        var address_name: String,
        @SerializedName("region_1depth_name")
        var region_1depth_name: String,
        @SerializedName("region_2depth_name")
        var region_2depth_name: String,
        @SerializedName("region_3depth_name")
        var region_3depth_name: String,
        @SerializedName("region_3depth_h_name")
        var region_3depth_h_name: String,
        @SerializedName("h_code")
        var h_code: String,
        @SerializedName("b_code")
        var b_code: String,
        @SerializedName("mountain_yn")
        var mountain_yn: String,
        @SerializedName("main_address_no")
        var main_address_no: String,
        @SerializedName("sub_address_no")
        var sub_address_no: String,
        @SerializedName("x")
        var x: String,
        @SerializedName("y")
        var y: String
)

data class road_address(
        @SerializedName("address_name")
        var address_name: String,
        @SerializedName("region_1depth_name")
        var region_1depth_name: String,
        @SerializedName("region_2depth_name")
        var region_2depth_name: String,
        @SerializedName("region_3depth_name")
        var region_3depth_name: String,
        @SerializedName("road_name")
        var road_name: String,
        @SerializedName("underground_yn")
        var underground_yn: String,
        @SerializedName("main_building_no")
        var main_building_no: Int,
        @SerializedName("sub_building_no")
        var sub_building_no: String,
        @SerializedName("building_name")
        var building_name: String,
        @SerializedName("zone_no")
        var zone_no: String,
        @SerializedName("y")
        var y: String,
        @SerializedName("x")
        var x: String
)
