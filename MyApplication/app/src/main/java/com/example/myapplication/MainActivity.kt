package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var text1 = findViewById<TextView>(R.id.text1)
        var text2 = findViewById<TextView>(R.id.text2)

        var text = ""
        //val url = "http://10.0.2.2/PHP_connection.php"
        val url = "http://10.0.2.2/selectregionall.php?REGION=인천광역시+남동구"
        text2.setText(url)
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                text="connection failed"
                runOnUiThread(Runnable { text1.setText(text) })
            }
            override fun onResponse(call: Call, response: Response) {
                text= response?.body?.string().toString()
                println(text)
                runOnUiThread(Runnable { text1.setText(text) })
            }
        })

    }
    fun btnClick(view: View) {
        var edit1 = findViewById<EditText>(R.id.edit1)
        var inputtext = edit1.text.toString()
        Toast.makeText(this, inputtext, Toast.LENGTH_SHORT).show()
    }


}