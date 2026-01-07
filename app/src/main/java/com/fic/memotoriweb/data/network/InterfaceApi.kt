package com.fic.memotoriweb.data.network

import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.Tarjeta
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface InterfaceApi {


    @POST("/login/")
    suspend fun login(@Body loginModel: LoginModel): Response<LoginResponse>

    @POST("/users/")
    suspend fun createUser(@Body user: RegisterModel): Response<RegisterResponse>

    @GET("/decks/user/{userId}")
    suspend fun getCategories(@Path("userId") userId: Int): Response<List<Categoria>>

    @GET("/sync/decks/{userId}")
    suspend fun getUpdatedCategories(
        @Path("userId") userId: Int,
        @Query("since") since: String
    ): Response<List<Categoria>>

    @GET("/cards/deck/{categoryId}/{userId}")
    suspend fun getTarjetasByUser(@Path("categoryId") categoryId: Int, @Path("userId") userId: Int): Response<List<Tarjeta>>

    @GET("/sync/cards/{userId}")
    suspend fun getUpdatedTarjetas(
        @Path("userId") userId: Int,
        @Query("since") since: String
    ): Response<List<TarjetasResponse>>

    @POST("/decks/{id}")
    suspend fun createCategory(@Path("id") id: Int, @Body categoria: Categoria): Response<Categoria>

    @POST("/cards/{idCategoria}/{userId}")
    suspend fun createTarjeta(@Path("idCategoria") idCategoria: Int, @Path("userId") userId: Int, @Body tarjeta: TarjetasModel): Response<TarjetasResponse>

    @Multipart
    @POST("/upload-image/")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

}