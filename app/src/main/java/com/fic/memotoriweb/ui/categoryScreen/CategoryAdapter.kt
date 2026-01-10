package com.fic.memotoriweb.ui.categoryScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Categoria

class CategoryAdapter(
    private val onItemSelected: (Categoria) -> Unit,
    private val onDataChanged: () -> Unit,
    private val onImageChange: () -> Unit
) : androidx.recyclerview.widget.ListAdapter<Categoria, CategoryViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return CategoryViewHolder(layoutInflater.inflate(R.layout.category_item, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        var item = getItem(position)
        holder.render(item, onItemSelected, onDataChanged, onImageChange)
    }

    class DiffCallback : DiffUtil.ItemCallback<Categoria>() {
        override fun areItemsTheSame(old: Categoria, new: Categoria): Boolean {
            return old.id == new.id
        }

        override fun areContentsTheSame(old: Categoria, new: Categoria): Boolean {
            return old == new
        }
    }

    /*fun actualizarLista(categorys: List<Categoria>) {
        categoryList = categorys
        notifyDataSetChanged()
    }*/
}