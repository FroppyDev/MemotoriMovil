package com.fic.memotoriweb.ui.smartScreen

import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fic.memotoriweb.data.db.Fotos
import com.fic.memotoriweb.databinding.FotosItemBinding

class FotosViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = FotosItemBinding.bind(view)

    fun render(item: Fotos) {
        Glide.with(binding.ivFoto.context)
            .load(item.rutaFoto)
            .into(binding.ivFoto)
    }
}
