package com.fic.memotoriweb

import android.net.Uri
import android.widget.ImageView
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.ui.modosDeJuego.GameManager
import com.fic.memotoriweb.ui.modosDeJuego.resultados.resultadosRV.Resultados

object Globales {

    var currentUri: Uri? = null
    var currentImageView: ImageView? = null
    var currentCategoria: Categoria? = null
    var mode: GameManager = GameManager.NORMAL
    var resultadosList = mutableListOf<Resultados>()

}