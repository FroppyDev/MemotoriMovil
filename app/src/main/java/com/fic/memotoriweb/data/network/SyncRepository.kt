package com.fic.memotoriweb.data.network

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager

class SyncRepository(private val context: Context) {

    fun enqueueSync() {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "sync_work",
                ExistingWorkPolicy.KEEP,
                createSyncWork()
            )
    }
}
