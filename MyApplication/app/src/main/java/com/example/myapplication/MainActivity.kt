package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var text1 = findViewById<TextView>(R.id.text1)

    }
    fun btnClick(view: View) {
        var url = "http://54.147.58.83/selectregionall.php?region="
        var edit1 = findViewById<EditText>(R.id.edit1)
        var inputtext = edit1.text.toString()
        var text = ""
        url = url + inputtext
        var text1 = findViewById<TextView>(R.id.text1)
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                text="connection failed"
                runOnUiThread(Runnable { text1.setText(text) })
            }
            override fun onResponse(call: Call, response: Response) {
                text= response?.body?.string().toString()
                runOnUiThread(Runnable { text1.setText(text) })
            }
        })


    }


}