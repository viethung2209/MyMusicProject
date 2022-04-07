package com.hunglee.mymusicproject.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hunglee.mymusicproject.services.MusicService

@SuppressLint("NewApi")
fun Context.sendIntent(action: String) {
    Intent(this, MusicService::class.java).apply {
        this.action = action
        try {
            if (isOreoPlus()) {
                startForegroundService(this)
            } else {
                startService(this)
            }
        } catch (ignored: Exception) {
        }
    }
}

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
