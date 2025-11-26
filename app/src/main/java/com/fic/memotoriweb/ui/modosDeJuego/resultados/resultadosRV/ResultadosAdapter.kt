package com.fic.memotoriweb.ui.modosDeJuego.resultados.resultadosRV

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R


class ResultadosAdapter(var resultadosList: List<Resultados>): RecyclerView.Adapter<ResultadosViewHolder>() {

    companion object {
        const val TIPO_ABIERTO = 1
        const val TIPO_OPCION = 2
        const val TIPO_VF = 3
        const val TIPO_NORMAL = 4
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ResultadosViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)

        val layout = when (viewType) {
            TIPO_ABIERTO -> R.layout.resultado_abierto_item
            TIPO_OPCION -> R.layout.resultado_opcion_item
            TIPO_VF -> R.layout.resultado_tof_item
            TIPO_NORMAL -> R.layout.resultado_memorizar_item
            else -> R.layout.resultado_abierto_item
        }

        return ResultadosViewHolder(layoutInflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(
        holder: ResultadosViewHolder,
        position: Int,
    ) {
        var item = resultadosList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return resultadosList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (resultadosList[position].tipo) {
            TipoResultado.TEST -> TIPO_ABIERTO
            TipoResultado.MULTIPLE_CHOICE -> TIPO_OPCION
            TipoResultado.TRUE_OR_FALSE -> TIPO_VF
            TipoResultado.NORMAL -> TIPO_NORMAL
        }
    }
}