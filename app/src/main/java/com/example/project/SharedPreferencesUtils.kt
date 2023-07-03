package com.example.project

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {
    private const val PREFS_NAME = "myPrefs"
    private const val KEY_EMAIL = "email"


    fun clearEmail(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
    fun checkEmail(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_EMAIL) == true) {
            return true;
        }
        return  false;
    }
    fun saveEmail(context: Context, username: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_EMAIL, username)
        editor.apply()
    }

    fun getEmail(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EMAIL, null)
    }


}