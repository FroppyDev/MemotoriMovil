package com.fic.memotoriweb.ui.categoryScreen

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fic.memotoriweb.Globales
import com.fic.memotoriweb.R
import com.fic.memotoriweb.TimePickerFragment
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.CategoriaColor
import com.fic.memotoriweb.data.db.CategoryDao
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.Horarios
import com.fic.memotoriweb.data.db.HorariosDao
import com.fic.memotoriweb.data.imageControl.ImageManager
import com.fic.memotoriweb.databinding.ActivityCategoryBinding
import com.fic.memotoriweb.ui.CameraActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var adapter: CategoryAdapter
    private lateinit var categoriaDao: CategoryDao
    private lateinit var horariosDao: HorariosDao
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
                currentImageView?.setBackgroundResource(R.color.secundary)
                currentUri = null
                Globales.currentUri = null
                Globales.currentImageView?.setBackgroundResource(R.color.secundary)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {

        categoriaDao = DatabaseProvider.GetDataBase(applicationContext).GetCategoryDao()
        horariosDao = DatabaseProvider.GetDataBase(applicationContext).GetHorariosDao()

        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRV(categoriaDao, horariosDao)
        initComponents(categoriaDao)
    }

    private fun initComponents(categoriaDao: CategoryDao) {
        binding.fabCrear.setOnClickListener {
            DialogType(categoriaDao, horariosDao, this)
        }

        binding.fabCamara.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRV(categoriaDao: CategoryDao, horarioDao: HorariosDao) {

        val listaPrueba = listOf<Categoria>(
            Categoria(
                0, "kevin", "kevin", null,
                CategoriaColor.MORADO, false, null, null, null
            )
        )

        CoroutineScope(Dispatchers.Main).launch {
            val listaCategorias = withContext(Dispatchers.IO) {
                categoriaDao.getAllCategorys()
            }

            val listaHorarios = withContext(Dispatchers.IO){
                horarioDao.getAllHorarios()
            }.let {
                Log.i("horarios", it.toString())
                Log.i("horarios", listaCategorias.toString())
            }

            adapter = CategoryAdapter(listaCategorias, onItemSelected = { categoria ->
                Toast.makeText(this@CategoryActivity, categoria.nombre, Toast.LENGTH_SHORT).show()
            }, onDataChanged = {
                updateListCategory()
            }, onImageChange = {
                mediaPicker.launch("image/*")
            })

            binding.rvCategory.layoutManager = LinearLayoutManager(this@CategoryActivity)
            binding.rvCategory.adapter = adapter
        }
    }

    private fun DialogType(categoriaDao: CategoryDao, horariosDao: HorariosDao, context: Context) {
        Log.i("kevdev", "dialogType")
        val dialog = Dialog(context)
        val view = R.layout.type_category
        dialog.setContentView(view)
        val btnSmart = dialog.findViewById<CardView>(R.id.btnSmartCategory)
        val btnNormal = dialog.findViewById<CardView>(R.id.btnNormalCategory)

        btnNormal.setOnClickListener {
            dialog.dismiss().let {
                DialogCategory(categoriaDao)
            }
        }

        btnSmart.setOnClickListener {
            dialog.dismiss().let {
                DialogSmartCategory(categoriaDao, horariosDao)
            }
        }

        dialog.show()
    }

    private fun DialogSmartCategory(categoriaDao: CategoryDao, horariosDao: HorariosDao){
        val dialog = Dialog(this)
        val view = R.layout.smart_category_dialog
        dialog.setContentView(view)
        var diasList = mutableListOf<Int>()
        var tvHoraInicio = dialog.findViewById<TextView>(R.id.tvHoraInicio)
        var tvHoraFin = dialog.findViewById<TextView>(R.id.tvHoraFin)
        var btnLunes = dialog.findViewById<Button>(R.id.circularButtonL)
        var btnMartes = dialog.findViewById<Button>(R.id.circularButtonM)
        var btnMiercoles = dialog.findViewById<Button>(R.id.circularButtonX)
        var btnJueves = dialog.findViewById<Button>(R.id.circularButtonJ)
        var btnViernes = dialog.findViewById<Button>(R.id.circularButtonV)
        var btnSabado = dialog.findViewById<Button>(R.id.circularButtonS)
        var btnDomingo = dialog.findViewById<Button>(R.id.circularButtonD)
        var btnHorarios = dialog.findViewById<Button>(R.id.btnHorarios)
        var btnMake = dialog.findViewById<Button>(R.id.btnMake)
        var tilConcepto = dialog.findViewById<EditText>(R.id.tilConcepto)
        var tilDescripcion = dialog.findViewById<EditText>(R.id.tilDescripcion)

        var horaInicio = "00"
        var horaFin = "00"


        btnHorarios.setOnClickListener {
                var timePickerFin = TimePickerFragment({
                    //onTimeSelected(it)
                    tvHoraFin.text = "Hora de fin: $it"

                    if (horaInicio.isNotEmpty()) {
                        val partesInicio = horaInicio.split(":")
                        val partesFin = it.split(":")
                        val inicioEnMin = partesInicio[0].toInt() * 60 + partesInicio[1].toInt()
                        val finEnMin = partesFin[0].toInt() * 60 + partesFin[1].toInt()

                        if (finEnMin >= inicioEnMin) {
                            horaFin = it
                            btnMake.isEnabled = true
                        } else {
                            Toast.makeText(
                                this,
                                "La hora de fin no puede ser menor que la hora de inicio",
                                Toast.LENGTH_SHORT
                            ).show()
                            horaInicio = "00:00"
                            horaFin = "00:00"
                            tvHoraInicio.text = "Hora de inicio: 00:00"
                            tvHoraFin.text = "Hora de fin: 00:00"
                            btnMake.isEnabled = false
                        }
                    }

                }, "Hora de fin")
                timePickerFin.show(supportFragmentManager, "horaFin").let {
                    var timePickerInicio = TimePickerFragment({
                        //onTimeSelected(it)
                        tvHoraInicio.text = "Hora de inicio: $it"
                        horaInicio = it
                    }, "Hora de Inicio")
                    timePickerInicio.show(supportFragmentManager, "horaInicio")
                }
        }

        btnLunes.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(1)
            }else{
                diasList.remove(1)
            }
        }

        btnMartes.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(2)
            }else{
                diasList.remove(2)
            }
        }

        btnMiercoles.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(3)
            }else{
                diasList.remove(3)
            }
        }

        btnJueves.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(4)
            }else{
                diasList.remove(4)
            }
        }

        btnViernes.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(5)
            }else{
                diasList.remove(5)
            }
        }

        btnSabado.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(6)
            }else{
                diasList.remove(6)
            }
        }

        btnDomingo.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected){
                diasList.add(7)
            }else{
                diasList.remove(7)
            }
        }

        btnMake.setOnClickListener {
            if (horaInicio != "00" && horaFin != "00"){
                CrearCategoriaSmart(this, Categoria(
                    nombre = tilConcepto.text.toString(),
                    descripcion = tilDescripcion.text.toString(),
                    imagen = null,
                    color = CategoriaColor.SECUNDARIO,
                    smart = true,
                    latitud = null,
                    longitud = null,
                    radioMetros = null
                ), Horarios(
                    idCategoria = 0,
                    horaInicio = horaInicio,
                    horaFin = horaFin
                ), categoriaDao, horariosDao).let {
                    updateListCategory()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun CrearCategoriaSmart(context: Context, categoria: Categoria, horarios: Horarios, categoryDao: CategoryDao, horariosDao: HorariosDao){
        CoroutineScope(Dispatchers.IO).launch{

            try {
                var id = categoriaDao.insertCategory(categoria)
                horariosDao.insertHorario(horarios.copy(
                    idCategoria = id
                ))
            }catch (e: Exception){
                Log.i("errorCategoria", e.toString())
            }
        }
    }

    private fun onTimeSelected(time: String) {
        Toast.makeText(this, time, Toast.LENGTH_SHORT).show()
    }

    private fun DialogCategory(categoriaDao: CategoryDao) {

        val dialog = Dialog(this)
        val view = R.layout.category_dialog
        dialog.setContentView(view)
        val ibImage = dialog.findViewById<ImageButton>(R.id.ibImage)
        val btnMake = dialog.findViewById<Button>(R.id.btnMake)
        val ivImageCategory = dialog.findViewById<ImageView>(R.id.ivImageCategory)
        val tilConcepto = dialog.findViewById<EditText>(R.id.tilConcepto)
        val tilDescripcion = dialog.findViewById<EditText>(R.id.tilDescripcion)

        val btnM1 = dialog.findViewById<AppCompatButton>(R.id.btnM1)
        val btnM2 = dialog.findViewById<AppCompatButton>(R.id.btnM2)
        val btnM3 = dialog.findViewById<AppCompatButton>(R.id.btnM3)
        val btnM4 = dialog.findViewById<AppCompatButton>(R.id.btnM4)
        val btnM5 = dialog.findViewById<AppCompatButton>(R.id.btnM5)

        var currentColor: CategoriaColor = CategoriaColor.SECUNDARIO

        ibImage.setOnClickListener {
            currentImageView = ivImageCategory
            mediaPicker.launch("image/*")
            currentColor = CategoriaColor.SECUNDARIO
        }

        //----------------------------------------------------------------------------------

        btnM1.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            currentImageView = null
            currentColor = CategoriaColor.MORADO
            ivImageCategory.setBackgroundResource(R.color.bg5)
        }

        btnM2.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            currentImageView = null
            currentColor = CategoriaColor.ROSA_BAJO
            ivImageCategory.setBackgroundResource(R.color.bg3)
        }

        btnM3.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            currentImageView = null
            currentColor = CategoriaColor.ROJO
            ivImageCategory.setBackgroundResource(R.color.bg4)
        }

        btnM4.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            currentImageView = null
            currentColor = CategoriaColor.NEGRO
            ivImageCategory.setBackgroundResource(R.color.bg7)
        }

        btnM5.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            currentImageView = null
            currentColor = CategoriaColor.ROSA
            ivImageCategory.setBackgroundResource(R.color.bg6)
        }

        //----------------------------------------------------------------------------------

        btnMake.setOnClickListener {

            var img: String? = null
            if (currentUri != null) {
                img = ImageManager().imageToInternalStorage(this, currentUri!!)
                currentUri = null
            } else img = null

            makeCategory(
                Categoria(
                    nombre = tilConcepto.text.toString(),
                    descripcion = tilDescripcion.text.toString(),
                    imagen = img,
                    color = currentColor,
                    smart = false,
                    latitud = null,
                    longitud = null,
                    radioMetros = null
                ),
                categoriaDao
            ).let {
                updateListCategory()
                dialog.dismiss()
            }
        }


        dialog.show()

    }

    private fun makeCategory(categoria: Categoria, categoriaDao: CategoryDao) {

        CoroutineScope(Dispatchers.IO).launch {

            try {
                categoriaDao.insertCategory(categoria)
            } catch (e: Exception) {
                Log.i("errorCategoria", e.toString())
            }

        }

    }

    private fun updateListCategory() {

        var listaCategorias = listOf<Categoria>()

        CoroutineScope(Dispatchers.Main).launch {
            listaCategorias = withContext(Dispatchers.IO) {
                categoriaDao.getAllCategorys()
            }

            adapter.actualizarLista(listaCategorias)

        }
    }
}