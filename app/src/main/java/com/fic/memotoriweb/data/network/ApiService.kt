package com.fic.memotoriweb.data.network

import android.util.Log
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.Tarjeta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://memotoriapi.onrender.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val call: InterfaceApi = retrofit.create(InterfaceApi::class.java)

    suspend fun login(loginModel: LoginModel): Response<LoginResponse> {
        val response = call.login(loginModel)
        Log.i("loginMio", response.body().toString())
        return response

    }

    suspend fun getCategories(userId: Int): Response<List<Categoria>> {
        val response = call.getCategories(userId)
        return response
    }

    suspend fun getTarjetasByUser(categoryId: Int, userId: Int): Response<List<Tarjeta>> {
        val response = call.getTarjetasByUser(categoryId, userId)
        return response
    }

    suspend fun createCategory(id: Int, categoria: Categoria): Response<Categoria> {
        val response = call.createCategory(id, categoria)
        return response
    }

    suspend fun updateCategory(deckId: Int, categoria: CategoryUpdateModel): Response<Categoria> {
        val response = call.updateCategory(deckId, categoria)
        return response
    }

    suspend fun deleteCategory(deckId: Int, userId: Int): Response<Unit>{
        val response = call.deleteCategory(deckId, userId)
        return response
    }

    suspend fun createTarjeta(
        idCategoriaRemota: Int,
        userId: Int,
        tarjeta: TarjetasModel
    ): Response<TarjetasResponse> {
        val response = call.createTarjeta(idCategoriaRemota, userId, tarjeta)
        return response
    }

    suspend fun deleteTarjeta(
        categoryId: Int,
        tarjetaId: Int
    ): Response<Unit> {
        val response = call.deleteTarjeta(categoryId, tarjetaId)
        return response
    }

    suspend fun updateTarjeta(
        tarjetaId: Int,
        tarjeta: TarjetasModel
    ): Response<TarjetasResponse> {
        val response = call.updateTarjeta(tarjetaId, tarjeta)
        return response
    }

    suspend fun getUpdatedCategories(
        userId: Int,
        since: String
    ): Response<List<Categoria>> {
        return call.getUpdatedCategories(
            userId = userId,
            since = since
        )
    }

    suspend fun getUpdatedTarjetas(
        userId: Int,
        since: String
    ): Response<List<TarjetasResponse>> {
        return call.getUpdatedTarjetas(userId, since)
    }

    suspend fun createUser(
        registerModel: RegisterModel
    ): Response<RegisterResponse> {
        return call.createUser(registerModel)
    }

    suspend fun uploadImage(
        file: MultipartBody.Part
    ): Response<ImageUploadResponse> {
        return call.uploadImage(file)
    }

}