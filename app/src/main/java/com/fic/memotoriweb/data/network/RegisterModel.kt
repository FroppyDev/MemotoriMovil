package com.fic.memotoriweb.data.network

data class RegisterModel(

    val email: String,
    val password: String,

)

data class RegisterResponse(

    val email: String,
    val id: Int,
    val is_active: Boolean,
    val created_at: String

)