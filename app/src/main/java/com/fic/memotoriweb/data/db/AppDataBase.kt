package com.fic.memotoriweb.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    Categoria::class,
    Tarjeta::class,
    Horarios::class,
    Dias::class,
    Categoria_Dias::class,
    Fotos::class,
    Imagenes::class
],
    version = 1)
abstract class AppDatabase(): RoomDatabase() {

    abstract fun GetCategoryDao(): CategoryDao

    abstract fun GetTarjetasDao(): TarjetasDao

    abstract fun GetHorariosDao(): HorariosDao

    abstract fun GetDiasDao(): DiasDao

    abstract fun GetCategoriaDiasDao(): CatDiasDao

    abstract fun GetFotosDao(): FotosDao

    abstract fun GetImagenesDao(): ImagenesDao
}