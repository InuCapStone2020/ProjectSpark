package inu.project.spark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class repositoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
        // find toolbar button '>' and set listener
        val mToolbar = findViewById<View>(R.id.toolbar_repository) as Toolbar
        mToolbar.setNavigationOnClickListener{
            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        val mBNavBar = findViewById<View>(R.id.Bottom_Navigation) as BottomNavigationView
        mBNavBar.setOnNavigationItemSelectedListener {menuItem: MenuItem ->
            var i:Intent? = null
            when (menuItem.itemId){
                R.id.mapItem -> i = Intent(this, mapActivity::class.java)
                R.id.searchItem -> i = Intent(this, searchActivity::class.java)
                R.id.homeItem-> i = Intent(this, MainActivity::class.java)
                R.id.settingItem -> i = Intent(this, settingActivity::class.java)
            }
            if (i != null){
                startActivity(i)
                return@setOnNavigationItemSelectedListener true
            }
            return@setOnNavigationItemSelectedListener false

        }
    }
}