package com.fic.memotoriweb.ui.categoryScreen

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fic.memotoriweb.Globales
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.CategoriaColor
import com.fic.memotoriweb.data.db.CategoryDao
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.databinding.ActivityCategoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var adapter: CategoryAdapter
    private lateinit var categoriaDao: CategoryDao
    private var currentImageView: ImageView? = null

    val mediaPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Globales.currentUri = uri
                currentImageView?.setImageURI(uri)
            } else {
                Globales.currentUri = null
                currentImageView?.setBackgroundResource(R.color.secundary)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {

        categoriaDao = DatabaseProvider.GetDataBase(applicationContext).GetCategoryDao()

        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRV(categoriaDao)
        initComponents()
    }

    private fun initComponents(){
        binding.fabCrear.setOnClickListener {
            DialogType()
        }
    }

    private fun initRV(categoriaDao: CategoryDao) {

        val listaPrueba = listOf<Categoria>(Categoria(0, "kevin", "kevin", null,
            CategoriaColor.MORADO, false, null, null, null))

        CoroutineScope(Dispatchers.Main).launch {
            val listaCategorias = withContext(Dispatchers.IO) {
                categoriaDao.getAllCategorys()
            }

            adapter = CategoryAdapter(listaCategorias) {

            }

            binding.rvCategory.layoutManager = LinearLayoutManager(this@CategoryActivity)
            binding.rvCategory.adapter = adapter
        }
    }

    private fun DialogType(){
        Log.i("kevdev", "dialogType")
        val dialog = Dialog(this)
        val view = R.layout.type_category
        dialog.setContentView(view)
        val btnSmart = dialog.findViewById<CardView>(R.id.btnSmartCategory)
        val btnNormal = dialog.findViewById<CardView>(R.id.btnNormalCategory)

        btnNormal.setOnClickListener {
            dialog.dismiss().let {
                DialogCategory()
            }
        }

        btnSmart.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun DialogCategory(){

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


        ibImage.setOnClickListener {
            currentImageView = ivImageCategory
            mediaPicker.launch("image/*")
        }

        //----------------------------------------------------------------------------------

        btnM1.setOnClickListener {

        }

        btnM2.setOnClickListener {

        }

        btnM3.setOnClickListener {

        }

        btnM4.setOnClickListener {

        }

        btnM5.setOnClickListener {

        }

        //----------------------------------------------------------------------------------

        btnMake.setOnClickListener {

        }


        dialog.show()

    }

}