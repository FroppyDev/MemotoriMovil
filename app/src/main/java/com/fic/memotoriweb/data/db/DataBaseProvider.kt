package com.fic.memotoriweb.data.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    var INSTANCE: AppDatabase? = null

    fun GetDataBase(context: Context): AppDatabase{
        if(INSTANCE == null){
            synchronized(this){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bd"
                ).build()
            }
        }

        return INSTANCE!!

    }

}