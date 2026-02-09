// Added by me (Abhinav Tripathi)
package com.example.sutraapp

import android.content.Context
import androidx.core.content.edit
class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("sutra_prefs", Context.MODE_PRIVATE)

    fun setLoggedIn(value: Boolean) {
        prefs.edit { putBoolean("logged_in", value) }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("logged_in", false)
    }
}

