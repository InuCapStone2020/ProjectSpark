package com.example.dbapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun show(view: View){
        var text1 = findViewById<TextView>(R.id.text1)
        dbHelper = DBHelper(this, "newdb.db", null, 1)
        database = dbHelper.writableDatabase

        var query = "SELECT * FROM mytable;"
        var c = database.rawQuery(query,null)
        var text = ""
        while(c.moveToNext()){
            text += c.getString(c.getColumnIndex("txt"))
            text += "\n"
        }
        text1.setText(text)
    }

    fun btnClick1(view: View) {

        var text1 = findViewById<TextView>(R.id.text1)
        var edit1 = findViewById<EditText>(R.id.edit1)
        var inputtext = edit1.text.toString()

        dbHelper = DBHelper(this, "newdb.db", null, 1)
        database = dbHelper.writableDatabase

        if (inputtext != ""){
            var contentValues = ContentValues()
            contentValues.put("txt",inputtext)
            database.insert("mytable",null,contentValues)
        }

        //insert
        show(view)
        //select
    }
    fun btnClick2(view: View){
        var text1 = findViewById<TextView>(R.id.text1)
        var edit1 = findViewById<EditText>(R.id.edit1)
        var inputtext = edit1.text.toString()

        dbHelper = DBHelper(this, "newdb.db", null, 1)
        database = dbHelper.writableDatabase

        if (inputtext != ""){
            var arr : Array<String> = arrayOf(inputtext)
            database.delete("mytable","txt=?",arr)
        }
        //delete
        show(view)
        //select
    }
}