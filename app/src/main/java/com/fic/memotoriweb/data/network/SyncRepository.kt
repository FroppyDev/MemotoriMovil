package com.fic.memotoriweb.data.network

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import kotlinx.coroutines.sync.Mutex

class SyncRepository(private val context: Context) {

    object SyncLock {
        val mutex = Mutex()
    }

    fun enqueueSync() {

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "sync_work",
                ExistingWorkPolicy.KEEP,
                createSyncWork()
            )
    }
}
