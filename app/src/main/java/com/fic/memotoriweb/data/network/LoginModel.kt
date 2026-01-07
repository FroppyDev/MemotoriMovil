package com.fic.memotoriweb.data.network

import com.fic.memotoriweb.data.db.Usuario
import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserModel
)

data class UserModel(

    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String,
)
