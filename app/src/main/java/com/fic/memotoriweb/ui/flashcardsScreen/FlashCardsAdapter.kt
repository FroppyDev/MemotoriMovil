package com.fic.memotoriweb.ui.flashcardsScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Tarjeta

class FlashCardsAdapter(var flashCardList: List<Tarjeta>? = null, var onItemSelected: (Tarjeta) -> Unit ):RecyclerView.Adapter<FlashCardsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashCardsViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return FlashCardsViewHolder(layoutInflater.inflate(R.layout.flashcard_item, parent, false))
    }

    override fun getItemCount(): Int {
        return flashCardList!!.size
    }

    override fun onBindViewHolder(holder: FlashCardsViewHolder, position: Int) {
        var item = flashCardList!![position]
        holder.render(item, onItemSelected)
    }

    fun actualizarLista(flashCardList: List<Tarjeta>) {
        this.flashCardList = flashCardList
        notifyDataSetChanged()
    }
}