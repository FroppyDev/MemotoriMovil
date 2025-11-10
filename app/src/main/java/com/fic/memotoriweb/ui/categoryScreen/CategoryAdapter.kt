package com.fic.memotoriweb.ui.categoryScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.Categoria

class CategoryAdapter(
    var categoryList: List<Categoria>? = null,
    var onItemSelected: (Categoria) -> Unit,
    var onDataChanged: (() -> Unit)? = null,
    var onImageChange: (() -> Unit)? = null,
) : RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return CategoryViewHolder(layoutInflater.inflate(R.layout.category_item, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        var item = categoryList!![position]
        holder.render(item, onItemSelected, onDataChanged, onImageChange)
    }

    override fun getItemCount(): Int {
        return categoryList!!.size
    }

    fun actualizarLista(categorys: List<Categoria>) {
        categoryList = categorys
        notifyDataSetChanged()
    }
}