package inu.project.spark

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class SubActivity : AppCompatActivity(){
    private val fragmentSetting: settingFragment = settingFragment()
    private val fragmentSearch: searchFragment = searchFragment()
    private val fragmentRepository: repositoryFragment = repositoryFragment()
    private val fragmentMap: mapFragment = mapFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
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
                    i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
                R.id.mapItem -> {
                    replaceFragment(fragmentMap)
                    //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentMap).commit()
                    menutext.setText(R.string.toolbar_map_name)
                }
                R.id.searchItem -> {
                    replaceFragment(fragmentSearch)
                    //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentSearch).commit()
                    menutext.setText(R.string.toolbar_search_name)
                }
                R.id.settingItem -> {
                    replaceFragment(fragmentSetting)
                    menutext.setText(R.string.toolbar_setting_name)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }
    fun gotoMainActiverty(){
        var i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}