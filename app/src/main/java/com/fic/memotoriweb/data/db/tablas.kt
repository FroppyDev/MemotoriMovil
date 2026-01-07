package com.fic.memotoriweb.data.db

import android.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fic.memotoriweb.R


@Entity
data class Usuario(
    @PrimaryKey val id: Int,
    val nombre: String?,
    val email: String?,
)

@Entity(tableName = "categoria")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var nombre: String,
    val userId: Int,
    var remoteId: Long? = null,
    var descripcion: String,
    var imagen: String?,
    var color: CategoriaColor = CategoriaColor.MORADO,
    var smart: Boolean = false,
    var latitud: Float?,
    var longitud: Float?,
    var radioMetros: Int?,
    var updatedAt: Long = System.currentTimeMillis(),
    var syncStatus: SyncStatus
)

@Entity(
    tableName = "tarjeta",
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
    val userId: Int,
    var idCategoria: Long,
    var remoteId: Long? = null,
    var concepto: String?,
    var definicion: String?,
    var definicionExtra:String?,
    var imagen: String?,
    var updatedAt: Long = System.currentTimeMillis(),
    var syncStatus: SyncStatus
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
    val userId: Int,
    var horaInicio: String,
    var horaFin: String,
    var updatedAt: Long = System.currentTimeMillis(),
    var syncStatus: SyncStatus
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
    val userId: Int,
    val idDia: Int,
    var updatedAt: Long = System.currentTimeMillis(),
    var syncStatus: SyncStatus
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
    val userId: Long,
    var imagen: String,
    var infoImagen: String,
    var updatedAt: Long = System.currentTimeMillis(),
    var syncStatus: SyncStatus
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
    val userId: Long,
    var rutaFoto: String,
    var fechaHora: String,
    val idCategoria: Long,
    var latitud: Float?,
    var longitud: Float?,
    var updatedAt: Long = System.currentTimeMillis(),
    var syncStatus: SyncStatus
)

enum class CategoriaColor(val color: Int?) {
    ROSA_BAJO(R.color.bg3),
    MORADO(R.color.bg5),
    ROJO(R.color.bg4),
    NEGRO(R.color.bg7),
    ROSA(R.color.bg6),
    SECUNDARIO(R.color.secundary)
}

enum class SyncStatus {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE
}
