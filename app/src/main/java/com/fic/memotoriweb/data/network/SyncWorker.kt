package com.fic.memotoriweb.data.network

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fic.memotoriweb.data.db.AppDatabase
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.SyncStatus
import com.fic.memotoriweb.data.db.Tarjeta
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Instant
import java.time.ZoneId

class SyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    val db = DatabaseProvider.GetDataBase(applicationContext)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            syncLocalCategoriesToServer(db, applicationContext)
            syncLocalTarjetasToServer(db, applicationContext)
            syncServerCategoriesToLocal(db, applicationContext)
            syncServerTarjetasToLocal(db, applicationContext)
            SyncPrefs(applicationContext).saveLastSync()
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}

suspend fun syncLocalCategoriesToServer(db: AppDatabase, applicationContext: Context) {
    val categoriaDao = db.GetCategoryDao()
    val tarjetaDao = db.GetTarjetasDao()
    val pendientes = categoriaDao.getPendientes()
    val tarjetasPendientes = tarjetaDao.getPending()
    val api = ApiService()

    pendientes.forEach { categoria ->
        when (categoria.syncStatus) {

            SyncStatus.PENDING_CREATE -> {

                var imageUrl = categoria.imagen

                // 📤 Subir imagen si es local
                if (!imageUrl.isNullOrEmpty() && !imageUrl.startsWith("http")) {
                    imageUrl = uploadImageIfNeeded(
                        db.openHelper.writableDatabase.path.let { applicationContext },
                        imageUrl
                    )

                    // Si no se pudo subir, reintenta luego
                    if (imageUrl == null) return@forEach
                }

                val categoriaParaServidor =
                    categoria.copy(imagen = imageUrl)

                val categoriaResponse = api.createCategory(
                    categoria.userId,
                    categoriaParaServidor
                )

                if (!categoriaResponse.isSuccessful || categoriaResponse.body() == null) {
                    return@forEach
                }

                val remoteCategoriaId = categoriaResponse.body()!!.id

                categoria.remoteId = remoteCategoriaId
                categoria.imagen = imageUrl
                categoria.syncStatus = SyncStatus.SYNCED
                categoriaDao.updateCategory(categoria)
            }

            SyncStatus.PENDING_UPDATE -> TODO()

            SyncStatus.PENDING_DELETE -> TODO()

            SyncStatus.SYNCED -> TODO()
        }
    }
}

suspend fun syncLocalTarjetasToServer(db: AppDatabase, applicationContext: Context) {

    val tarjetaDao = db.GetTarjetasDao()
    val categoriaDao = db.GetCategoryDao()
    val api = ApiService()

    val tarjetasPendientes = tarjetaDao.getPending()

    tarjetasPendientes.forEach { tarjeta ->

        val categoria = categoriaDao.getCategoryById(tarjeta.idCategoria)

        if (categoria.remoteId == null) return@forEach

        Log.i("SYNC", "Tarjeta ${tarjeta.id} status ${tarjeta.syncStatus}")
        Log.i("SYNC", "Categoria remoteId ${categoria.remoteId}")

        when (tarjeta.syncStatus) {

            SyncStatus.PENDING_CREATE -> {

                var imageUrl = tarjeta.imagen

                // 📤 Subir imagen si es local
                if (!imageUrl.isNullOrEmpty() && !imageUrl.startsWith("http")) {
                    try {
                        imageUrl = uploadImageIfNeeded(
                            applicationContext,
                            imageUrl
                        )

                        Log.i("SYNC", imageUrl.toString())
                    } catch (e: Exception){
                        Log.i("SYNC", e.toString())
                    }

                    // Si no se pudo subir, reintenta luego
                    if (imageUrl == null) return@forEach
                }


                val response = api.createTarjeta(
                    categoria.remoteId!!.toInt(),
                    tarjeta.userId,
                    TarjetasModel(
                        concepto = tarjeta.concepto.toString(),
                        definicion = tarjeta.definicion.toString(),
                        definicionExtra = tarjeta.definicionExtra.toString(),
                        imagen = imageUrl.toString()
                    )
                )


                if (response.isSuccessful) {
                    Log.i("SYNC", "Tarjeta ${tarjeta.id} creada en el servidor")
                    tarjeta.remoteId = response.body()!!.id.toLong()
                    tarjeta.imagen = imageUrl
                    tarjeta.syncStatus = SyncStatus.SYNCED
                    tarjetaDao.updateFlashcard(tarjeta)
                }
            }

            SyncStatus.PENDING_UPDATE -> TODO()

            SyncStatus.PENDING_DELETE -> TODO()

            else -> Unit
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncServerCategoriesToLocal(db: AppDatabase, context: Context) {
    val categoriaDao = db.GetCategoryDao()
    val api = ApiService()
    val lastSync = SyncPrefs(context).getLastSync()
    val lastSyncIso = lastSync.toIso()
    val remotas = api.getUpdatedCategories(SyncPrefs(context).getUserId(), lastSyncIso)

    if (remotas.isSuccessful){
        Log.i("SYNC", "Sincronización exitosa")
        Log.i("SYNC", remotas.body().toString())

        remotas.body()?.forEach { remote ->
            val local = categoriaDao.getByRemoteId(remote.id.toInt())

            if (local == null) {
                categoriaDao.insertCategory(remote.toEntity())
            } else if (remote.updatedAt > local.updatedAt) {
                categoriaDao.updateCategory(remote)
            }
        }
    } else {
        Log.i("SYNC", "Sincronización fallida")
    }

}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncServerTarjetasToLocal(
    db: AppDatabase,
    context: Context
) {
    val tarjetaDao = db.GetTarjetasDao()
    val categoriaDao = db.GetCategoryDao()
    val api = ApiService()

    val lastSync = SyncPrefs(context).getLastSync()
    val lastSyncIso = lastSync.toIso()

    val response = api.getUpdatedTarjetas(
        userId = SyncPrefs(context).getUserId(),
        since = lastSyncIso
    )

    Log.i("SYNC", response.body().toString() + " desde syncServerTarjetasToLocal")

    if (!response.isSuccessful || response.body() == null) {
        Log.e("SYNC", "Error sincronizando tarjetas")
        return
    }

    response.body()!!.forEach { remote ->

        // ⚠ Buscar la categoría LOCAL por remoteId
        val categoriaLocal =
            categoriaDao.getByRemoteId(remote.idCategoria)

        if (categoriaLocal == null) {
            Log.w("SYNC", "Tarjeta sin categoría local, se ignora")
            return@forEach
        }

        val local =
            tarjetaDao.getByRemoteId(remote.id)

        if (local == null) {
            tarjetaDao.insertTarjeta(
                remote.toEntity(categoriaLocal.id)
            )
        } else if (remote.updatedAt > local.updatedAt) {
            tarjetaDao.updateFlashcard(
                remote.toEntity(categoriaLocal.id).copy(id = local.id)
            )
        }
    }
}

suspend fun uploadImageIfNeeded(
    context: Context,
    localPath: String
): String? {

    Log.i("UPLOAD", "Intentando subir: $localPath")

    if (localPath.startsWith("http")) return localPath

    val file = File(localPath)
    if (!file.exists()) {
        Log.e("UPLOAD", "Archivo no existe")
        return null
    }

    val mimeType = android.webkit.MimeTypeMap
        .getSingleton()
        .getMimeTypeFromExtension(file.extension.lowercase())
        ?: "image/jpeg" // fallback seguro

    val requestBody =
        file.asRequestBody(mimeType.toMediaTypeOrNull())

    val part = MultipartBody.Part.createFormData(
        "file",
        file.name,
        requestBody
    )

    val response = ApiService().uploadImage(part)

    Log.i("UPLOAD", "HTTP ${response.code()}")

    return if (response.isSuccessful) {
        response.body()?.url
    } else {
        Log.e("UPLOAD", response.errorBody()?.string() ?: "error")
        null
    }
}




fun TarjetasResponse.toEntity(idCategoriaLocal: Long) =
    Tarjeta(
        remoteId = id.toLong(),
        idCategoria = idCategoriaLocal,
        userId = userId,
        concepto = concepto,
        definicion = definicion,
        definicionExtra = definicionExtra,
        imagen = imagen,
        syncStatus = SyncStatus.SYNCED
    )


@RequiresApi(Build.VERSION_CODES.O)
fun Long.toIso(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.of("UTC"))
        .toLocalDateTime()
        .toString()
}

fun Categoria.toEntity(): Categoria {
    return Categoria(
        remoteId = id,
        userId = userId,
        nombre = nombre,
        descripcion = descripcion,
        imagen = imagen,
        color = color,
        latitud = latitud,
        longitud = longitud,
        radioMetros = radioMetros,
        smart = smart,
        updatedAt = updatedAt,
        syncStatus = SyncStatus.SYNCED
    )
}
