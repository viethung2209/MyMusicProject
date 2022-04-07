package com.hunglee.mymusicproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hunglee.mymusicproject.acitivity.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private val homeFragment: HomeFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}