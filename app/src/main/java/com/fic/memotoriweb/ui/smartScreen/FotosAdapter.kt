package com.fic.memotoriweb.ui.smartScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Fotos

class FotosAdapter(val fotosList: List<Fotos>): RecyclerView.Adapter<FotosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FotosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FotosViewHolder(layoutInflater.inflate(R.layout.fotos_item, parent, false))
    }

    override fun onBindViewHolder(holder: FotosViewHolder, position: Int) {
        val item = fotosList.get(position)
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return fotosList.size
    }
}