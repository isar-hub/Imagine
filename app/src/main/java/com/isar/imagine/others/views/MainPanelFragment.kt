package com.isar.imagine.others.views

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.isar.imagine.R
import com.isar.imagine.barcode_scenning.BarCodeScanningActivity
import com.isar.imagine.databinding.FragmentFirstBinding


class MainPanelFragment : AppCompatActivity() {


    private lateinit var binding: FragmentFirstBinding

    private lateinit var drawerLayout: DrawerLayout

    //navigation bar
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("MainPanelFragment", "onViewCreated called")


        bindToolbar()
        setUpToolbar()
        navigation()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED
        )
        navigationView.setNavigationItemSelectedListener { menuItem ->

            Log.e("Test", "Clicked on ${menuItem.itemId}")
            when (menuItem.itemId) {
                R.id.dashboard -> {
                    navController.navigate(R.id.dashboardFragment)
                }

                R.id.inventory -> {
                    navController.navigate(R.id.inventoryFragment)
                }

                R.id.transactions -> {
                    navController.navigate(R.id.transactionsFragment)
                }

                R.id.warranty -> {
                    navController.navigate(R.id.warrantyFragment)
                }

                R.id.retailer -> {
                    navController.navigate(R.id.retailerFragment)
                }

                R.id.customer -> {
                    navController.navigate(R.id.customerFragment)
                }
                R.id.allProducts->{
                    navController.navigate(R.id.allProductFragment)
                }
                R.id.billingPanel -> {
                    startActivity(
                        Intent(
                            this@MainPanelFragment,
                            BarCodeScanningActivity::class.java
                        )
                    )
                }

                R.id.signout -> {
                    startActivity(Intent(
                        this@MainPanelFragment, LoginActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                    })
                }

                else -> {
                    Log.e("Test", "Clicked on else")
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            true
        }

    }

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.drawer_nav_view)
        val desiredWidth = (resources.displayMetrics.widthPixels * 0.5).toInt()
        val layoutParams = navigationView.layoutParams
        layoutParams.width = desiredWidth
        navigationView.layoutParams = layoutParams
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun navigation() {

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.inventoryFragment,
                R.id.transactionsFragment,
                R.id.warrantyFragment,
                R.id.retailerFragment,
                R.id.customerFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }


}
