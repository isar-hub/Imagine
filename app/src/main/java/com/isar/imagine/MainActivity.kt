package com.isar.imagine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.isar.imagine.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val userName = intent.getStringExtra("username")
        Log.e("Test","reached main activity ${userName.toString()}")
        when (userName) {
            "1" -> {
                Log.e("Test", "Navigating to mainPanelFragment")
                startActivity(Intent(this@MainActivity,MainPanelFragment::class.java))
            }
            "2" -> {
                Log.e("Test", "Navigating to billingPanelFragment")
                startActivity(Intent(this@MainActivity, BarCodeScanningActivity::class.java))
            }
            else -> println("Other number")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}