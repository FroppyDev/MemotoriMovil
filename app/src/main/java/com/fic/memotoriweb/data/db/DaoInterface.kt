package com.fic.memotoriweb.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao{
    @Query("SELECT * FROM Categoria WHERE userId = :userId")
    fun getAllCategorys(userId: Int): List<Categoria>

    @Insert
    fun insertCategory(categoria: Categoria) : Long

    @Delete
    suspend fun deleteCategory(category: Categoria)

    @Update
    suspend fun updateCategory(category: Categoria)

    @Query("SELECT * FROM Categoria WHERE id = :id")
    fun getCategoryById(id: Long): Categoria

    @Query("SELECT * FROM categoria WHERE syncStatus != 'SYNCED'")
    suspend fun getPendientes(): List<Categoria>

    // 🔹 Insertar desde servidor
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<Categoria>)

    @Query("SELECT * FROM Categoria WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): Categoria?


}

@Dao
interface TarjetasDao{
    @Query("SELECT * FROM Tarjeta WHERE idCategoria = :idCategoria")
    fun getAllTarjetas(idCategoria: Long): List<Tarjeta>

    @Query("SELECT * FROM Tarjeta WHERE syncStatus != 'SYNCED'")
    fun getPending(): List<Tarjeta>

    @Query("SELECT * FROM Tarjeta WHERE idCategoria = :idCategoria")
    fun getTarjetasById(idCategoria: Long): List<Tarjeta>

    @Query("SELECT * FROM tarjeta WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): Tarjeta?

    @Insert
    fun insertTarjeta(tarjeta: Tarjeta)

    @Delete
    suspend fun deleteFlashcard(tarjeta: Tarjeta)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFlashcard(tarjeta: Tarjeta)
}


@Dao
interface HorariosDao{
    @Query("SELECT * FROM Horarios")
    fun getAllHorarios(): List<Horarios>

    @Insert
    fun insertHorario(horarios: Horarios)

    @Update
    fun updateHorario(horarios: Horarios)

    @Query("SELECT * FROM Horarios WHERE idCategoria = :idCategoria")
    fun getHorarioById(idCategoria: Long): Horarios?
}


@Dao
interface CatDiasDao{
    @Query("SELECT * FROM Categoria_Dias")
    fun getAllCatDias(): Flow<List<Categoria_Dias>>

    @Insert
    fun insertCatDias(categoriaDias: Categoria_Dias)
}


@Dao
interface DiasDao{
    @Query("SELECT * FROM Dias")
    fun getAllDias(): Flow<List<Dias>>

    @Insert
    fun insertDia(dias: Dias)
}


@Dao
interface FotosDao{
    @Query("SELECT * FROM Fotos WHERE idCategoria = :idCategoria")
    fun getAllFotos(idCategoria: Long): List<Fotos>

    @Insert
    fun insertFoto(foto: Fotos)
}

@Dao
interface ImagenesDao{
    @Query("SELECT * FROM Imagenes")
    fun getAllImagenes(): Flow<List<Imagenes>>

    @Insert
    fun insertImagen(imagenes: Imagenes)
}