package com.fic.memotoriweb.data.network

import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder

fun createSyncWork(): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(syncConstraints)
        .build()
}