package com.fic.memotoriweb.data.network

data class TarjetasModel(

    val concepto: String?,
    val definicion: String?,
    val definicionExtra: String?,
    val imagen: String?

)

data class TarjetasResponse(

    val concepto: String?,
    val definicion: String?,
    val definicionExtra: String?,
    val imagen: String?,
    val id: Int,
    val userId: Int,
    val idCategoria: Int,
    val updatedAt: Long

)