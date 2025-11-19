package com.fic.memotoriweb.ui.modosDeJuego

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fic.memotoriweb.Globales
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.Tarjeta
import com.fic.memotoriweb.data.db.TarjetasDao
import com.fic.memotoriweb.databinding.ActivityTestBinding
import com.fic.memotoriweb.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestActivity : AppCompatActivity() {

    lateinit var binding: ActivityTestBinding
    lateinit var tarjetasDao: TarjetasDao
    private var tarjetasList = listOf<Tarjeta>()
    var index = 0
    private var mode: GameManager = Globales.mode
    var listaResultados = mutableListOf<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {

        tarjetasDao = DatabaseProvider.GetDataBase(applicationContext).GetTarjetasDao()
        binding = ActivityTestBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initList()
    }

    private fun initGames(mode: GameManager) {

        if (tarjetasList.isNotEmpty()){
            when(mode) {
                GameManager.NORMAL -> NormalMode(tarjetasList[index])
                GameManager.QUIZZ_OPCION -> TODO()
                GameManager.TRUE_OR_FALSE -> TODO()
                GameManager.QUIZZ_ABIERTO -> TODO()
                GameManager.MIXED -> TODO()
            }
        }
    }

    private fun initList() {
        CoroutineScope(Dispatchers.Main).launch {
            tarjetasList = withContext(Dispatchers.IO) {
                tarjetasDao.getAllTarjetas(Globales.currentCategoria!!.id)
            }

            initGames(mode)
        }
    }

    fun SiguienteTarjeta(){
        if (index < tarjetasList.lastIndex) {
            index++
            initGames(mode)
        } else {

        }
    }

    private fun NormalMode(card: Tarjeta) {
        val vista = layoutInflater.inflate(R.layout.layout_normal, binding.contenedorModos, false)
        binding.contenedorModos.removeAllViews()
        binding.contenedorModos.addView(vista)

        val cardView = vista.findViewById<CardView>(R.id.cvFlashcard)
        val btnC = vista.findViewById<Button>(R.id.btnConocida)
        val btnD = vista.findViewById<Button>(R.id.btnDesconocida)
        var tvConcepto = vista.findViewById<TextView>(R.id.tvConcepto)
        var front = true

        tvConcepto.text = card.concepto

        cardView.setOnClickListener {
            if (front){
                RotationYFlipAnimator.flip(cardView, onHalf = {tvConcepto.text = card.definicion})
            } else {
                RotationYFlipAnimator.flip(cardView, onHalf = {tvConcepto.text = card.concepto})
            }
            front = !front
        }

        btnC.setOnClickListener {
            SiguienteTarjeta()
        }

        btnD.setOnClickListener {
            SiguienteTarjeta()
        }
    }
}

object RotationYFlipAnimator {

    fun flip(view: View, onHalf: (() -> Unit)? = null) {

        // Animación 1: reducir hasta 0 (desaparecer)
        val shrink = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        shrink.duration = 200

        // Animación 2: volver a crecer (y aquí es donde "cambia el contenido")
        val expand = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        expand.duration = 200

        shrink.addListener(object : Animator.AnimatorListener {

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                // Aquí puedes cambiar el texto, imagen, fondo, lo que sea



                onHalf?.invoke()

                // Cuando termina la primera animación, empieza la segunda
                expand.start()
            }

            override fun onAnimationRepeat(p0: Animator) {

            }

            override fun onAnimationStart(p0: Animator) {

            }
        })
        shrink.start()
    }
}
