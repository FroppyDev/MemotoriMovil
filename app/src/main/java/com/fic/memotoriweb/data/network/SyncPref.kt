package com.fic.memotoriweb.data.network

import android.content.Context

class SyncPrefs(context: Context) {

    private val prefs = context.getSharedPreferences(
        "sync_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_LAST_SYNC = "last_sync"
        private const val USER_ID = "user_id"
        private const val KEY_SYNC_RUNNING = "sync_running"
    }

    fun getLastSync(): Long {
        return prefs.getLong(KEY_LAST_SYNC, 0L)
    }

    fun saveLastSync(timeMillis: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putLong(KEY_LAST_SYNC, timeMillis)
            .apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, 0)
    }

    fun saveUserId(userId: Int) {
        prefs.edit()
            .putInt(USER_ID, userId)
            .apply()
    }

    fun setSyncRunning(running: Boolean) {
        prefs.edit().putBoolean(KEY_SYNC_RUNNING, running).apply()
    }

    fun isSyncRunning(): Boolean {
        return prefs.getBoolean(KEY_SYNC_RUNNING, false)
    }
}
