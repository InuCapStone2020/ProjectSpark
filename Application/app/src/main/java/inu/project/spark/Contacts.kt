package inu.project.spark
import androidx.room.*

@Entity(primaryKeys = arrayOf("NUM","SUBNUM"), tableName = "tb_contacts")
data class Contacts (
    val NUM:Int,
    val SUBNUM:Int,
    var M_DATE:String,
    var M_TIME:String,
    var REGION:String,
    var EVENT:String,
    var CONTENT:String
)