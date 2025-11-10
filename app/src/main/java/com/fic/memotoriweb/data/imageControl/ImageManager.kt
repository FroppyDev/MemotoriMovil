package com.fic.memotoriweb.data.imageControl

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class ImageManager {

    //funcion para crear respaldo de las imagenes manejadas por la aplicacion
    fun imageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath // <- Guarda esto en tu base de datos
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}