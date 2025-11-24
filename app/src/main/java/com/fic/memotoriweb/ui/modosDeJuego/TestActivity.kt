package com.fic.memotoriweb.ui.modosDeJuego

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
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
import kotlin.random.Random

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
                GameManager.QUIZZ_OPCION -> Quizz_Opcion(tarjetasList[index], tarjetasList)
                GameManager.TRUE_OR_FALSE -> True_Or_False(tarjetasList[index], tarjetasList)
                GameManager.QUIZZ_ABIERTO -> Quizz_Abierto(tarjetasList[index])
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

    private fun Quizz_Abierto(tarjetaActual: Tarjeta){
        val vista = layoutInflater.inflate(R.layout.layout_abierto, binding.contenedorModos, false)
        binding.contenedorModos.removeAllViews()
        binding.contenedorModos.addView(vista)

        var btnAceptar = vista.findViewById<AppCompatButton>(R.id.btnAceptar)
        var btnDesconocido = vista.findViewById<AppCompatButton>(R.id.btnDesconocida)
        var etRespuesta = vista.findViewById<EditText>(R.id.etRespuesta)
        var btnAuxiliar = vista.findViewById<ImageButton>(R.id.btnAuxiliar)
        var btnImagen = vista.findViewById<ImageButton>(R.id.btnImagen)
        var tvPregunta = vista.findViewById<TextView>(R.id.tvPregunta)

        tvPregunta.text = tarjetaActual.definicion

        btnImagen.setOnClickListener{
            imageDialog(tarjetaActual)
        }

        btnAuxiliar.setOnClickListener {
            textDialog(tarjetaActual)
        }

        btnAceptar.setOnClickListener {
            //etRespuesta.text.toString()
            SiguienteTarjeta()
        }

        btnDesconocido.setOnClickListener {
            SiguienteTarjeta()
        }

    }

    private fun True_Or_False(tarjetaActual: Tarjeta, listaTarjetas: List<Tarjeta>){

        val vista = layoutInflater.inflate(R.layout.layout_tof, binding.contenedorModos, false)
        binding.contenedorModos.removeAllViews()
        binding.contenedorModos.addView(vista)

        var btnVerdadero = vista.findViewById<AppCompatButton>(R.id.btnVerdadero)
        var btnFalso = vista.findViewById<AppCompatButton>(R.id.btnFalso)
        var tvRespuesta = vista.findViewById<TextView>(R.id.tvRespuesta)
        var tvPregunta = vista.findViewById<TextView>(R.id.tvPregunta)

        var respuesta:String?
        var opcion = Random.nextInt(1, 3)
        if (opcion == 1){
            respuesta = tarjetaActual.concepto
        } else {
            respuesta = listaTarjetas.get(Random.nextInt(0, listaTarjetas.size - 1)).concepto
        }

        tvPregunta.text = tarjetaActual.definicion
        tvRespuesta.text = respuesta

        btnVerdadero.setOnClickListener {
            SiguienteTarjeta()
        }

        btnFalso.setOnClickListener {
            SiguienteTarjeta()
        }

    }

    private fun Quizz_Opcion(tarjetaActual: Tarjeta, listaTarjetas: List<Tarjeta>){

        val vista = layoutInflater.inflate(R.layout.layout_multiple, binding.contenedorModos, false)
        binding.contenedorModos.removeAllViews()
        binding.contenedorModos.addView(vista)

        val tvPregunta = vista.findViewById<TextView>(R.id.tvPregunta)
        val btnOpcion1 = vista.findViewById<AppCompatButton>(R.id.btnRespuesta1)
        val btnOpcion2 = vista.findViewById<AppCompatButton>(R.id.btnRespuesta2)
        val btnOpcion3 = vista.findViewById<AppCompatButton>(R.id.btnRespuesta3)
        val btnOpcion4 = vista.findViewById<AppCompatButton>(R.id.btnRespuesta4)

        if(listaTarjetas.size >= 4){
            val index = listaTarjetas.indexOf(tarjetaActual)
            val opciones = mutableListOf<Tarjeta>()

            when (index) {
                0 -> {
                    // primera tarjeta -> agarra 3 por delante
                    opciones.addAll(tarjetasList.subList(0, minOf(4, tarjetasList.size)))
                }

                tarjetasList.lastIndex -> {
                    // última tarjeta -> agarra 3 por detrás
                    val start = maxOf(tarjetasList.size - 4, 0)
                    opciones.addAll(tarjetasList.subList(start, tarjetasList.size))
                }

                else -> {
                    // caso normal -> intenta 1 atrás y 2 adelante
                    val start = maxOf(index - 1, 0)
                    val end = minOf(index + 3, tarjetasList.size) // index+3 porque es exclusivo
                    opciones.addAll(tarjetasList.subList(start, end))

                    // si no llegaste a 4 (por estar cerca de límites), completa por el otro lado
                    while (opciones.size < 4) {
                        if (start > 0) opciones.add(0, tarjetasList[start - 1])
                        else if (end < tarjetasList.size) opciones.add(tarjetasList[end])
                    }
                }
            }

            tvPregunta.text = tarjetaActual.definicion
            btnOpcion1.text = opciones[0].concepto
            btnOpcion2.text = opciones[1].concepto
            btnOpcion3.text = opciones[2].concepto
            btnOpcion4.text = opciones[3].concepto

            btnOpcion1.setOnClickListener {
                SiguienteTarjeta()
            }

            btnOpcion2.setOnClickListener {
                SiguienteTarjeta()
            }

            btnOpcion3.setOnClickListener {
                SiguienteTarjeta()
            }

            btnOpcion4.setOnClickListener {
                SiguienteTarjeta()
            }

        } else {
            Toast.makeText(this, "Se necesitan al menos 4 tarjetas", Toast.LENGTH_SHORT).show()
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

    private fun imageDialog(tarjeta: Tarjeta){

        var dialog = Dialog(this)
        val vista = R.layout.image_dialog
        dialog.setContentView(vista)

        var img = dialog.findViewById<ImageView>(R.id.ivImagenTarjeta)

        if(tarjeta.imagen != null){
            img.setImageURI(tarjeta.imagen!!.toUri())

            dialog.show()
        } else {
            Toast.makeText(this, "No hay imagen", Toast.LENGTH_SHORT).show()
        }

    }

    private fun textDialog(tarjeta: Tarjeta){

        var dialog = Dialog(this)
        val vista = R.layout.text_dialog
        dialog.setContentView(vista)

        var text = dialog.findViewById<TextView>(R.id.tvAuxiliar)

        if(tarjeta.definicionExtra != null){

            text.text = tarjeta.definicionExtra

            dialog.show()
        } else {

            Toast.makeText(this, "No hay imagen", Toast.LENGTH_SHORT).show()

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
