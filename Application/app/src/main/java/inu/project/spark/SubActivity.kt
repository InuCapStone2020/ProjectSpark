package inu.project.spark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import inu.project.spark.AppDatabase

class SubActivity : AppCompatActivity(){
    private val fragmentSetting: settingFragment = settingFragment()
    private val fragmentSearch: searchFragment = searchFragment()
    private val fragmentRepository: repositoryFragment = repositoryFragment()
    private val fragmentMap: mapFragment = mapFragment()

    fun fragmentmapchange(logititude: Double,latitude: Double){
        fragmentMap.changeSearchedCord(logititude,latitude)
    }
    private lateinit var callback: OnBackPressedCallback
    private fun initMapFragment(){
        if(fragmentMap.getSearchFlag()){
            fragmentMap.setSearchFlag(false)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                initMapFragment()
                gotoMainActiverty()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
        // find toolbar button '>' and set listener
        val mToolbar = findViewById<View>(R.id.toolbar_sub) as Toolbar
        val mBNavBar = findViewById<View>(R.id.Bottom_Navigation) as BottomNavigationView
        val menutext = findViewById<View>(R.id.toolbar_sub_title) as TextView
        mToolbar.setNavigationOnClickListener{
            gotoMainActiverty()
        }
        val extras = intent.extras ?:return
        val fragment = extras.getInt("fragment")
        when (fragment){
            R.id.button_main_map->{
                replaceFragment(fragmentMap)
                //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentMap).commit()
                menutext.setText(R.string.toolbar_map_name)
                mBNavBar.selectedItemId = R.id.mapItem
            }
            R.id.button_main_setting->{
                replaceFragment(fragmentSetting)
                //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentSetting).commit()
                menutext.setText(R.string.toolbar_setting_name)
                mBNavBar.selectedItemId = R.id.settingItem
            }
            R.id.button_main_repository->{
                replaceFragment(fragmentRepository)
                //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentRepository).commit()
                menutext.setText(R.string.toolbar_repository_name)
            }
            R.id.button_main_search->{
                replaceFragment(fragmentSearch)
                //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentSearch).commit()
                menutext.setText(R.string.toolbar_search_name)
                mBNavBar.selectedItemId = R.id.searchItem
            }
        }
        mBNavBar.setOnNavigationItemSelectedListener{ menuItem: MenuItem ->
            var i:Intent? = null
            when (menuItem.itemId){
                R.id.homeItem->{
                    initMapFragment()
                    i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
                R.id.mapItem -> {
                    replaceFragment(fragmentMap)
                    //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentMap).commit()
                    menutext.setText(R.string.toolbar_map_name)
                }
                R.id.searchItem -> {
                    initMapFragment()
                    replaceFragment(fragmentSearch)
                    //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentSearch).commit()
                    menutext.setText(R.string.toolbar_search_name)
                }
                R.id.settingItem -> {
                    initMapFragment()
                    replaceFragment(fragmentSetting)
                    menutext.setText(R.string.toolbar_setting_name)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onDestroy() {
        callback.remove()
        super.onDestroy()
    }
    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }
    fun gotoMainActiverty(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}