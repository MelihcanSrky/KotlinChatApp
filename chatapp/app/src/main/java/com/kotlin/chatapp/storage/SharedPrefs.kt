package com.kotlin.chatapp.storage

import android.content.Context

class SharedPrefs private constructor(private val sharedContext: Context) {
    companion object{
        private val SHARED_PREF_NAME = "chat_app_shared_prefs"
        private var mInstance: SharedPrefs? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefs {
            if (mInstance == null) {
                mInstance = SharedPrefs(mCtx)
            }
            return mInstance as SharedPrefs
        }
    }

    var token: String
        get() {
            val sharedPrefs = sharedContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPrefs.getString("token", null).toString()
        }
        set(value) {
            val sharedPrefs = sharedContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()

            editor.putString("token", value)
            editor.apply()
        }

    var user_uuid: String
        get() {
            val sharedPrefs = sharedContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPrefs.getString("user_uuid", null).toString()
        }
        set(value) {
            val sharedPrefs = sharedContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()

            editor.putString("user_uuid", value)
            editor.apply()
        }

    fun clear() {
        val sharedPref = sharedContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}