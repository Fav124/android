package com.example.deisaapplication.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.deisaapplication.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME   = "deisa_session"
        private const val KEY_TOKEN    = "auth_token"
        private const val KEY_USER     = "auth_user"
        private const val KEY_LOGGED_IN = "is_logged_in"
    }

    fun getPrefs(): SharedPreferences = prefs

    // ─── Token ────────────────────────────────────────────────────────────────

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    // ─── User ─────────────────────────────────────────────────────────────────

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USER, gson.toJson(user))
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return runCatching { gson.fromJson(json, User::class.java) }.getOrNull()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false) && getToken() != null

    // ─── Logout ───────────────────────────────────────────────────────────────

    fun clear() = prefs.edit().clear().apply()

    // ─── Role helpers (convenience) ───────────────────────────────────────────

    fun canManageData(): Boolean = getUser()?.canManageData() ?: false
    fun canAccessHealth(): Boolean = getUser()?.canAccessHealth() ?: false
    fun isSuperAdmin(): Boolean  = getUser()?.isSuperAdmin() ?: false
    fun getRole(): String        = getUser()?.role ?: ""
}
