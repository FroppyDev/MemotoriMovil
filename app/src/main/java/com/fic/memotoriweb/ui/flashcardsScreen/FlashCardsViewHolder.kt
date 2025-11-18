package com.fic.memotoriweb.ui.flashcardsScreen

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.data.db.Tarjeta
import com.fic.memotoriweb.databinding.FlashcardItemBinding

class FlashCardsViewHolder(view: View):RecyclerView.ViewHolder(view) {

    var binding = FlashcardItemBinding.bind(view)


    fun render(item: Tarjeta, onItemSelected: (Tarjeta) -> Unit) {
        binding.tvConcepto.text = item.concepto
        binding.tvDefinicion.text = item.definicion
        binding.tvAuxiliar.text = item.definicionExtra
        binding.cvImage.setBackgroundColor(Color.argb(50,255,0,0)) // rojo semitransparente
        binding.tvAuxiliar.setBackgroundColor(Color.argb(50,0,255,0)) // verde

        if (item.imagen != null){
            binding.ivFlashCard.setImageURI(item.imagen!!.toUri())
            binding.cvImage.visibility = View.VISIBLE
            binding.ivFlashCard.visibility = View.VISIBLE

        } else {
            binding.ivFlashCard.visibility = View.GONE
            binding.cvImage.visibility = View.GONE
        }

        if (item.definicionExtra != ""){
            binding.tvAuxiliar.visibility = View.VISIBLE
        } else {
            binding.tvAuxiliar.visibility = View.GONE
        }

        if (item.definicionExtra == "" && item.imagen == null){
            binding.llAuxiliares.visibility = View.GONE
        }
    }
}