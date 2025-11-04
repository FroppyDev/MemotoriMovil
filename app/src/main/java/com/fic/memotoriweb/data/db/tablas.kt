package com.fic.memotoriweb.data.db

import android.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fic.memotoriweb.R

@Entity
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var nombre: String,
    var descripcion: String,
    var imagen: String?,
    var color: CategoriaColor = CategoriaColor.MORADO,
    var smart: Boolean = false,
    var latitud: Float?,
    var longitud: Float?,
    var radioMetros: Int?
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["idCategoria"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class Tarjeta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var idCategoria: Long,
    var concepto: String?,
    var definicion: String?,
    var definicionExtra:String?,
    var imagen: String?
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["idCategoria"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Horarios(
    @PrimaryKey(autoGenerate = true)
    val idHorario: Long = 0,
    val idCategoria: Long,
    var horaInicio: String,
    var horaFin: String
)

@Entity
data class Dias(
    @PrimaryKey(autoGenerate = false)
    val idDia: Int,
    val nombreDia: String
)

@Entity(
    primaryKeys = ["idCategoria", "idDia"],
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["idCategoria"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Dias::class,
            parentColumns = ["idDia"],
            childColumns = ["idDia"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Categoria_Dias(
    val idCategoria: Long,
    val idDia: Int
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["idCategoria"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Imagenes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val idCategoria: Long,
    var imagen: String,
    var infoImagen: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["idCategoria"],
        onDelete = ForeignKey.CASCADE
    )]
)

//tabla de fotos tomadas para las carpetas inteligentes
data class Fotos(
    @PrimaryKey(autoGenerate = true)
    val idFoto: Long = 0,
    var rutaFoto: String,
    var fechaHora: String,
    val idCategoria: Long,
    var latitud: Float?,
    var longitud: Float?
)

enum class CategoriaColor(val color: Int?) {
    ROJO(R.color.bg),
    AZUL(R.color.bg2),
    VERDE(R.color.bg3),
    AMARILLO(R.color.bg4),
    MORADO(R.color.bg5)
}
