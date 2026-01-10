package com.fic.memotoriweb.ui.flashcardsScreen

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fic.memotoriweb.Globales
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.SyncStatus
import com.fic.memotoriweb.data.db.Tarjeta
import com.fic.memotoriweb.data.db.TarjetasDao
import com.fic.memotoriweb.data.imageControl.ImageManager
import com.fic.memotoriweb.data.network.ApiService
import com.fic.memotoriweb.data.network.SyncPrefs
import com.fic.memotoriweb.data.network.SyncRepository
import com.fic.memotoriweb.databinding.ActivityFlashcardsBinding
import com.fic.memotoriweb.ui.modosDeJuego.GameManager
import com.fic.memotoriweb.ui.modosDeJuego.TestActivity
import com.fic.memotoriweb.ui.smartScreen.SmartActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlashcardsActivity : AppCompatActivity() {

    lateinit var binding: ActivityFlashcardsBinding
    lateinit var tarjetasDao: TarjetasDao
    lateinit var adapter: FlashCardsAdapter
    private var currentImageView: ImageView? = null
    private var currentUri: Uri? = null

    val mediaPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                currentImageView?.setImageURI(uri)
                currentUri = uri
                Globales.currentImageView?.setImageURI(uri)
                Globales.currentUri = uri
            } else {
                currentUri = null
                Globales.currentUri = null
                currentImageView?.setImageURI(null)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        tarjetasDao = DatabaseProvider.GetDataBase(applicationContext).GetTarjetasDao()

        var tarjetasDao = DatabaseProvider.GetDataBase(applicationContext).GetTarjetasDao()

        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRV(Globales.currentCategoria!!)
        initComponents(Globales.currentCategoria!!, tarjetasDao)
    }

    private fun initRV(categoria: Categoria) {

        adapter = FlashCardsAdapter(
            emptyList(),
            onItemSelected = { tarjeta, position ->
                ModificarDialog(tarjeta)
            }
        )

        binding.rvFlashCards.layoutManager = LinearLayoutManager(this)
        binding.rvFlashCards.adapter = adapter

        lifecycleScope.launch {
            tarjetasDao.observeTarjetas(categoria.id)
                .collect { listaTarjetas ->

                    Log.i("Tarjetas", "Observer emitió ${listaTarjetas.size} tarjetas")

                    adapter.actualizarLista(listaTarjetas)

                    binding.tvTextoInicial.visibility =
                        if (listaTarjetas.isEmpty()) View.VISIBLE else View.GONE
                }
        }
    }

    private fun initComponents(categoria: Categoria, tarjetasDao: TarjetasDao) {
        binding.tvTituloCategoria.text = categoria.nombre
        binding.tvDescripcionCategoria.text = categoria.descripcion

        if (!categoria.smart) {

            binding.ibCarpeta.visibility = GONE
            val params = binding.tvTituloCategoria.layoutParams as ConstraintLayout.LayoutParams
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            binding.ibCarpeta.layoutParams = params

        }

        binding.ibOpciones.setOnClickListener {
            OpcionesDialog()
        }

        binding.ibCarpeta.setOnClickListener {
            var intent = Intent(this, SmartActivity::class.java)
            intent.putExtra("CAT_ID", categoria.id)
            startActivity(intent)
        }

        binding.fabCrear.setOnClickListener {
            CrearTarjetaDialog(tarjetasDao)
        }
    }

    private fun ModificarDialog(tarjeta: Tarjeta) {
        val dialog = Dialog(this)
        val vista = R.layout.flashcard_dialog
        dialog.setContentView(vista)

        var etConcepto = dialog.findViewById<EditText>(R.id.etConcepto)
        var etDefinicion = dialog.findViewById<EditText>(R.id.etDefinicion)
        var etDefinicionExtra = dialog.findViewById<EditText>(R.id.etDefinicionExtra)
        var ibImage = dialog.findViewById<ImageButton>(R.id.ibImage)
        var btnCrear = dialog.findViewById<AppCompatButton>(R.id.btnCrear)
        var ivImageFlashCard = dialog.findViewById<ImageView>(R.id.ivImageFlashCard)

        btnCrear.text = "Modificar"

        etConcepto.setText(tarjeta.concepto)
        etDefinicion.setText(tarjeta.definicion)
        etDefinicionExtra.setText(tarjeta.definicionExtra)

        if (tarjeta.imagen != null) {
            currentUri = tarjeta.imagen!!.toUri()
            ivImageFlashCard.setImageURI(tarjeta.imagen!!.toUri())
        } else {
            ivImageFlashCard.setImageURI(null)
            currentUri = null
        }

        ibImage.setOnClickListener {
            currentImageView = ivImageFlashCard
            mediaPicker.launch("image/*")
        }

        btnCrear.setOnClickListener {

            var img: String? = null
            if (currentUri != null) {
                if (currentUri.toString() != tarjeta.imagen.toString()){
                    img = ImageManager().imageToInternalStorage(this, currentUri!!)
                } else img = tarjeta.imagen
                currentUri = null
            } else img = null

            ModificarTarjeta(
                tarjeta.copy(
                    concepto = etConcepto.text.toString(),
                    definicion = etDefinicion.text.toString(),
                    definicionExtra = etDefinicionExtra.text.toString(),
                    imagen = img,
                    syncStatus = SyncStatus.PENDING_UPDATE
                ), tarjetasDao
            ).let {
                triggerSync()
                updateListFlashCards()
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    private fun CrearTarjetaDialog(tarjetasDao: TarjetasDao) {
        val dialog = Dialog(this)
        val vista = R.layout.flashcard_dialog
        dialog.setContentView(vista)

        var etConcepto = dialog.findViewById<EditText>(R.id.etConcepto)
        var etDefinicion = dialog.findViewById<EditText>(R.id.etDefinicion)
        var etDefinicionExtra = dialog.findViewById<EditText>(R.id.etDefinicionExtra)
        var ibImage = dialog.findViewById<ImageButton>(R.id.ibImage)
        var btnCrear = dialog.findViewById<AppCompatButton>(R.id.btnCrear)
        var ivImageFlashCard = dialog.findViewById<ImageView>(R.id.ivImageFlashCard)

        ibImage.setOnClickListener {
            currentImageView = ivImageFlashCard
            mediaPicker.launch("image/*")
        }


        btnCrear.setOnClickListener {

            var img: String? = null
            if (currentUri != null) {
                img = ImageManager().imageToInternalStorage(this, currentUri!!)
                currentUri = null
            } else img = null

            CrearTarjeta(
                Tarjeta(
                    idCategoria = Globales.currentCategoria!!.id,
                    userId = SyncPrefs(this).getUserId(),
                    concepto = etConcepto.text.toString(),
                    definicion = etDefinicion.text.toString(),
                    definicionExtra = etDefinicionExtra.text.toString(),
                    imagen = img,
                    syncStatus = SyncStatus.PENDING_CREATE
                ), tarjetasDao
            ).let {
                triggerSync()
                updateListFlashCards()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun CrearTarjeta(tarjeta: Tarjeta, tarjetasDao: TarjetasDao) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                tarjetasDao.insertTarjeta(tarjeta)
                Log.i("Tarjetas", "Tarjeta creada")
            } catch (e: Exception) {
                Log.i("Tarjetas", e.toString())
            }
        }
    }

    private fun ModificarTarjeta(tarjeta: Tarjeta, tarjetasDao: TarjetasDao) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tarjetasDao.updateFlashcard(tarjeta)
                Log.i("Tarjetas", "Tarjeta modificada")
            } catch (e: Exception) {
                Log.i("Tarjetas", e.toString())
            }
        }
    }


    private fun OpcionesDialog() {

        var dialog = Dialog(this)
        var vista = R.layout.opciones_dialog
        dialog.setContentView(vista)

        var btnRepaso = dialog.findViewById<CardView>(R.id.btnRepaso)
        var btnTestAbierto = dialog.findViewById<CardView>(R.id.btnTestAbierto)
        var btnTestOM = dialog.findViewById<CardView>(R.id.btnTestOM)
        var btnVF = dialog.findViewById<CardView>(R.id.btnVF)
        var btnCombinado = dialog.findViewById<CardView>(R.id.btnCombinado)

        btnRepaso.setOnClickListener {
            //AQUI VA EL CODIGO PARA INICIAR EL TIPO DE JUEGO
            IniciarJuego(GameManager.NORMAL)
        }

        btnTestAbierto.setOnClickListener {
            //AQUI VA EL CODIGO PARA INICIAR EL TIPO DE JUEGO
            IniciarJuego(GameManager.QUIZZ_ABIERTO)
        }

        btnTestOM.setOnClickListener {
            //AQUI VA EL CODIGO PARA INICIAR EL TIPO DE JUEGO
            IniciarJuego(GameManager.QUIZZ_OPCION)
        }

        btnVF.setOnClickListener {
            //AQUI VA EL CODIGO PARA INICIAR EL TIPO DE JUEGO
            IniciarJuego(GameManager.TRUE_OR_FALSE)
        }

        btnCombinado.setOnClickListener {
            IniciarJuego(GameManager.MIXED)
        }


        dialog.show()

    }

    private fun updateListFlashCards() {

        var listaTarjetas = listOf<Tarjeta>()

        CoroutineScope(Dispatchers.Main).launch {
            listaTarjetas = withContext(Dispatchers.IO) {
                tarjetasDao.getAllTarjetas(Globales.currentCategoria?.id!!)
            }

            adapter.actualizarLista(listaTarjetas)
            triggerSync()

        }
    }

    private fun triggerSync() {
        SyncRepository(applicationContext).enqueueSync()
    }


    private fun IniciarJuego(mode: GameManager) {
        intent = Intent(this, TestActivity::class.java)
        startActivity(intent).let {
            Globales.mode = mode
        }
    }
}