package com.hunglee.mymusicproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hunglee.mymusicproject.acitivity.NavigationDrawer
import com.hunglee.mymusicproject.media.MediaManager

class SplashActivity : AppCompatActivity() {

    val SPLASH_DISPLAY_LENGTH = 2000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        MediaManager.getAllSongFromStorage(this)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this@SplashActivity, NavigationDrawer::class.java)
                startActivity(intent)
                finish()
            }, 2000
        )
    }
}