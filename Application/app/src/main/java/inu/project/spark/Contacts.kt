package inu.project.spark
import androidx.room.*

@Entity(tableName = "tb_contacts")
data class Contacts (
    @PrimaryKey(autoGenerate = false) val NUM:Int,
    var M_DATE:String,
    var M_TIME:String,
    var REGION:String,
    var EVENT:String,
    var CONTENT:String
)