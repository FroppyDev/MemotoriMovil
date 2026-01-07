package com.fic.memotoriweb.ui.categoryScreen

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fic.memotoriweb.Globales
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.data.db.CategoriaColor
import com.fic.memotoriweb.data.db.CategoryDao
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.imageControl.ImageManager
import com.fic.memotoriweb.databinding.CategoryItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CategoryViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val binding = CategoryItemBinding.bind(view)
    val categoryDao = DatabaseProvider.GetDataBase(binding.root.context).GetCategoryDao()

    fun render(
        item: Categoria,
        onItemSelected: (Categoria) -> Unit,
        onDataChanged: (() -> Unit)?,
        onImageChange: (() -> Unit)?
    ){

        try {
            if (item.imagen != null){
                if (item.imagen?.startsWith("http") == true) {
                    Glide.with(binding.root.context)
                        .load(item.imagen)
                        .into(binding.ivCategoryImage)
                } else {
                    binding.ivCategoryImage.setImageURI(Uri.fromFile(File(item.imagen)))
                }
            } else {
                binding.ivCategoryImage.setBackgroundResource(item.color.color!!)
            }
        } catch (e: Exception){
            Log.i("errorCategoryViewHolder", e.toString())
        }

        binding.tvCategory.text = item.nombre
        binding.tvDescription.text = item.descripcion

        binding.cvParent.setOnClickListener {
            onItemSelected(item)
        }

        binding.cvParent.setOnLongClickListener {
            OptionsDialog(binding.root.context, categoryDao, item, onDataChanged, onImageChange)
            true
        }

    }

    private fun OptionsDialog(
        context: Context,
        categoryDao: CategoryDao,
        categoria: Categoria,
        onDataChanged: (() -> Unit)?,
        onImageChange: (() -> Unit)?
    ){
        val dialog = Dialog(context)
        val view = R.layout.category_options_dialog
        dialog.setContentView(view)

        val btnModificar = dialog.findViewById<Button>(R.id.btnModificar)
        val btnEliminar = dialog.findViewById<Button>(R.id.btnEliminar)
        val btnCompartir = dialog.findViewById<Button>(R.id.btnCompartir)

        btnModificar.setOnClickListener {
            DialogModificar(categoria, onImageChange, categoryDao, onDataChanged)
            dialog.dismiss()
        }

        btnEliminar.setOnClickListener {

            EliminarCategoria(categoria, categoryDao).let {
                onDataChanged?.invoke()
            }

            dialog.dismiss()
        }

        btnCompartir.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun EliminarCategoria(categoria: Categoria, categoryDao: CategoryDao){

        CoroutineScope(Dispatchers.IO).launch {

            categoryDao.deleteCategory(categoria)

        }


    }

    private fun share(tabla:String){

    }

    private fun DialogModificar(
        categoria: Categoria,
        onImageChange: (() -> Unit)?,
        categoryDao: CategoryDao,
        onDataChanged: (() -> Unit)?
    ){
        var dialog = Dialog(binding.root.context)
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

        tilConcepto.setText(categoria.nombre)
        tilDescripcion.setText(categoria.descripcion)

        if (categoria.imagen != null){
            ivImageCategory.setImageURI(categoria.imagen!!.toUri())
        } else {
            ivImageCategory.setBackgroundResource(categoria.color.color!!)
        }

        btnMake.setText("Modificar")

        ibImage.setOnClickListener {
            Globales.currentImageView = ivImageCategory
            onImageChange?.invoke()
            currentColor = CategoriaColor.SECUNDARIO
        }

        //----------------------------------------------------------------------------------

        btnM1.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            Globales.currentImageView = null
            currentColor = CategoriaColor.MORADO
            Globales.currentUri = null
            ivImageCategory.setBackgroundResource(R.color.bg5)
        }

        btnM2.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            Globales.currentImageView = null
            currentColor = CategoriaColor.ROSA_BAJO
            Globales.currentUri = null
            ivImageCategory.setBackgroundResource(R.color.bg3)
        }

        btnM3.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            Globales.currentImageView = null
            currentColor = CategoriaColor.ROJO
            Globales.currentUri = null
            ivImageCategory.setBackgroundResource(R.color.bg4)
        }

        btnM4.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            Globales.currentImageView = null
            currentColor = CategoriaColor.NEGRO
            Globales.currentUri = null
            ivImageCategory.setBackgroundResource(R.color.bg7)
        }

        btnM5.setOnClickListener {
            ivImageCategory.setImageDrawable(null)
            Globales.currentImageView = null
            currentColor = CategoriaColor.ROSA
            Globales.currentUri = null
            ivImageCategory.setBackgroundResource(R.color.bg6)
        }

        //----------------------------------------------------------------------------------

        btnMake.setOnClickListener {

            var img: String? = null
            if (Globales.currentUri != null) {
                img = ImageManager().imageToInternalStorage(binding.root.context, Globales.currentUri!!)
                Globales.currentUri = null
            } else img = null

            UpdateCategory(categoryDao, categoria.copy(
                nombre = tilConcepto.text.toString(),
                descripcion = tilDescripcion.text.toString(),
                color = currentColor,
                imagen = img

            ), onDataChanged).let {
                onDataChanged?.invoke()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun UpdateCategory(
        categoryDao: CategoryDao,
        categoria: Categoria,
        onDataChanged: (() -> Unit)?
    ){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                categoryDao.updateCategory(categoria).let {
                    onDataChanged?.invoke()
                }
                Log.i("kevindev", "updateCategory")

            } catch (e: Exception){
                Log.i("errorCategoryViewHolder", e.toString())
            }
        }
    }

}