package inu.project.spark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

class searchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        // find toolbar button '>' and set listener
        val mToolbar = findViewById<View>(R.id.toolbar_search) as Toolbar
        mToolbar.setNavigationOnClickListener{
            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}