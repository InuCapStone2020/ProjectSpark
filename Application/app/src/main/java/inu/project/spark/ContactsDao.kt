package inu.project.spark

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactsDao {
    @Query("SELECT * FROM tb_contacts order by NUM desc")
    fun getAll(): List<Contacts>
    @Query("SELECT * FROM tb_contacts WHERE NUM= :num")
    fun getFromNum(num:Int): List<Contacts>
    @Insert
    fun insertAll(vararg contacts: Contacts)

    @Delete
    fun delete(contacts: Contacts)

}