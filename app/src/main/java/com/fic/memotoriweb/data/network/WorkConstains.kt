package com.fic.memotoriweb.data.network

import androidx.work.Constraints
import androidx.work.NetworkType

val syncConstraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()