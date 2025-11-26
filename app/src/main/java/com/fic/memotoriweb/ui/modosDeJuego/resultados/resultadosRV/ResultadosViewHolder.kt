package com.fic.memotoriweb.ui.modosDeJuego.resultados.resultadosRV

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fic.memotoriweb.R
import com.fic.memotoriweb.databinding.ActivityResultadosBinding
import com.fic.memotoriweb.databinding.ResultadoAbiertoItemBinding
import com.fic.memotoriweb.databinding.ResultadoMemorizarItemBinding
import com.fic.memotoriweb.databinding.ResultadoOpcionItemBinding
import com.fic.memotoriweb.databinding.ResultadoTofItemBinding

class ResultadosViewHolder(view: View): RecyclerView.ViewHolder(view) {

    var bindingTof = ResultadoTofItemBinding.bind(view)
    var bindingNormal = ResultadoMemorizarItemBinding.bind(view)
    var bindingMultiple = ResultadoOpcionItemBinding.bind(view)
    var bindingAbierto = ResultadoAbiertoItemBinding.bind(view)

    fun render(item: Resultados){
        when(item.tipo) {
            TipoResultado.NORMAL -> renderNormal(item)
            TipoResultado.MULTIPLE_CHOICE -> renderMultiple(item)
            TipoResultado.TRUE_OR_FALSE -> renderTof(item)
            TipoResultado.TEST -> renderAbierto(item)

        }
    }

    fun renderNormal(item: Resultados){

        if (item.normal_mode_response == true){
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.correct)
        } else {
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.incorrect)
        }

        bindingNormal.tvConcepto.text = item.tarjeta.concepto
        bindingNormal.tvDefinicion.text = item.tarjeta.definicion
        bindingNormal.tvRespuesta.text = if (item.normal_mode_response == true) "Correcto" else "Incorrecto"
    }

    fun renderMultiple(item: Resultados){

        if (item.multiple_choice_response == item.tarjeta.concepto){
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.correct)
        } else {
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.incorrect)
        }

        bindingMultiple.tvConcepto.text = item.tarjeta.concepto
        bindingMultiple.tvDefinicion.text = item.tarjeta.definicion
        bindingMultiple.tvRespuesta.text = item.multiple_choice_response
    }

    fun renderTof(item: Resultados){

        if (item.true_or_false?.texto == item.tarjeta.concepto){
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.correct)
        } else {
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.incorrect)
        }

        bindingTof.tvConcepto.text = item.tarjeta.concepto
        bindingTof.tvDefinicion.text = item.true_or_false?.texto
        bindingTof.tvRespuesta.text = if (item.true_or_false?.respuesta == true) "Verdadero" else "Falso"
    }

    fun renderAbierto(item: Resultados){

        if (item.test_mode_response == item.tarjeta.concepto){
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.correct)
        } else {
            bindingMultiple.llRespuesta.setBackgroundResource(R.color.incorrect)
        }

        bindingAbierto.tvConcepto.text = item.tarjeta.concepto
        bindingAbierto.tvDefinicion.text = item.tarjeta.definicion
        bindingAbierto.tvRespuesta.text = item.test_mode_response
    }


}