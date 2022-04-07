package com.hunglee.mymusicproject.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hunglee.mymusicproject.extensions.sendIntent
import com.hunglee.mymusicproject.util.FINISH
import com.hunglee.mymusicproject.util.NEXT
import com.hunglee.mymusicproject.util.PLAYPAUSE
import com.hunglee.mymusicproject.util.PREVIOUS

class ControlActionsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            PREVIOUS, PLAYPAUSE, NEXT, FINISH -> context.sendIntent(action)
        }
    }
}
