package com.whomentors.aqua

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.whomentors.aqua.Activity.Start
import com.whomentors.aqua.Activity.UserInfo

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MainTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Setup Back button from nav components
        val navController = this.findNavController(R.id.myNavHostFragment)
        drawerLayout = findViewById(R.id.drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        // Setup Nav Drawer
        val navView = findViewById<NavigationView>(R.id.navView)
        NavigationUI.setupWithNavController(navView, navController)
        navView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.rateApp -> {
                    // Try opening app on the PlayStore if its installed otherwise open the
                    // PlayStore url
                    try {
                        startActivity(
                            Intent(
                                "android.intent.action.VIEW",
                                Uri.parse("market://details?id=$packageName")
                            )
                        )
                    } catch (unused: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                "android.intent.action.VIEW",
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                        )
                    }
                }
                R.id.shareApp -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=" + getPackageName()
                    )
                    intent.type = "text/plain"
                    startActivity(intent)
                }
                R.id.privacyPolicy -> {
                    val url = "https://www.google.com/"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
                else -> navController.navigate(item.itemId)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}