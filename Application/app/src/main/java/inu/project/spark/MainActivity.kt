package inu.project.spark


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import java.util.*



class MainActivity : AppCompatActivity() {

    override fun onBackPressed() {
        AlertDialog.Builder(this@MainActivity)
                .setTitle("close")
                .setMessage("종료하시겠습니까?")
                .setPositiveButton(
                        "네",
                        DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                            ActivityCompat.finishAffinity(this@MainActivity)
                            System.exit(0)
                        }
                )
                .setNegativeButton(
                        "아니오",
                        DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                        })
                .show()
        //super.onBackPressed()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // define navigationListener
        class navigationListener : View.OnClickListener {
            // confirm close app function
            override fun onClick(v: View?) {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("close")
                        .setMessage("종료하시겠습니까?")
                        .setPositiveButton(
                            "네",
                            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                                ActivityCompat.finishAffinity(this@MainActivity)
                                System.exit(0)
                            }
                        )
                        .setNegativeButton(
                            "아니오",
                            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                            })
                        .show()
            }
        }
        val lis = navigationListener()
        // find toolbar button '>' and set listener
        val mToolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        mToolbar.setNavigationOnClickListener(lis)
        // find each button from Id
        val button_main_map = findViewById<View>(R.id.button_main_map)
        val button_main_setting = findViewById<View>(R.id.button_main_setting)
        val button_main_repository = findViewById<View>(R.id.button_main_repository)
        val button_main_search = findViewById<View>(R.id.button_main_search)
        // set goto each layout by button
        button_main_map.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_map)
            startActivity(i)
        }
        button_main_setting.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_setting)
            startActivity(i)
        }
        button_main_repository.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_repository)
            startActivity(i)
        }
        button_main_search.setOnClickListener{
            val i = Intent(this, SubActivity::class.java)
            i.putExtra("fragment", R.id.button_main_search)
            startActivity(i)
        }
    }
    /*
    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }
    }

     */
}