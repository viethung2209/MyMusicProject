package com.hunglee.mymusicproject.helpers

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by sev_user on 12/20/2016.
 */
class SharePreferencesController {
    fun getString(key: String?, value: String?): String? {
        return mSharedPreferences!!.getString(key, value)
    }

    fun putString(key: String?, value: String?) {
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun getBoolean(key: String?, value: Boolean): Boolean {
        return mSharedPreferences!!.getBoolean(key, value)
    }

    fun putBoolean(key: String?, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun getInt(key: String?, value: Int): Int {
        return mSharedPreferences!!.getInt(key, value)
    }

    fun putInt(key: String?, value: Int) {
        editor!!.putInt(key, value)
        editor!!.commit()
    }

    companion object {
        private const val FILE_NAME = "setting_play_music"
        private val preferencesManager = SharePreferencesController()
        private var mSharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        fun getInstance(context: Context): SharePreferencesController {
            mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            editor = mSharedPreferences!!.edit()
            return preferencesManager
        }
    }
}