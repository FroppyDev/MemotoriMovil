package com.fic.memotoriweb.ui.modosDeJuego.resultados.resultadosRV

import com.fic.memotoriweb.data.db.Tarjeta

data class Resultados(
    var tipo: TipoResultado,
    var tarjeta: Tarjeta,
    var normal_mode_response: Boolean? = false,
    var true_or_false: TOFResponse? = null,
    var multiple_choice_response: String? = null,
    var test_mode_response: String? = null
)

data class TOFResponse(
    var texto: String? = null,
    var respuesta: Boolean? = false
)


enum class TipoResultado() {
    NORMAL,
    MULTIPLE_CHOICE,
    TRUE_OR_FALSE,
    TEST
}
