package com.whomentors.aqua.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.whomentors.aqua.R


class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // If opened for the first time go to UserInfoActivity
        // else go to Start Activity
        Handler().postDelayed({
            if (getSharedPreferences("user_pref", 0).getBoolean("firstrun", true)) {
                val i1 = Intent(this, UserInfo::class.java)
                startActivity(i1)
                finish()
            } else {
                val i = Intent(this, Start::class.java)
                startActivity(i)
                finish()
            }
        }, 3000)
    }


}
