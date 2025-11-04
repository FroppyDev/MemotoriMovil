package com.fic.memotoriweb.ui.categoryScreen

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Categoria
import com.fic.memotoriweb.databinding.CategoryItemBinding

class CategoryViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val binding = CategoryItemBinding.bind(view)

    fun render(item: Categoria, onItemSelected: (Categoria) -> Unit){
        try{
            binding.ivCategoryImage.setBackgroundResource(R.color.secundary)
        }catch (e:Exception){

        }
        binding.clDescripcion.visibility = GONE
        binding.vLine.visibility = GONE

        binding.cvParent.setBackgroundResource(R.color.primary)
        binding.tvCategory.text = item.nombre
        binding.tvDescription.text = item.descripcion

        binding.cvParent.setOnClickListener {
            onItemSelected(item)
        }

    }

    private fun share(tabla:String){

    }

}