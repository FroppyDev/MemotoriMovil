package com.fic.memotoriweb.data.network

import com.fic.memotoriweb.data.db.CategoriaColor

data class CategoryUpdateModel(
    var nombre: String,
    var descripcion: String,
    var imagen: String?,
    var color: CategoriaColor = CategoriaColor.MORADO,
    var smart: Boolean = false,
    var latitud: Float?,
    var longitud: Float?,
    var radioMetros: Int?,
    val id: Long,
    val userId: Int,
)