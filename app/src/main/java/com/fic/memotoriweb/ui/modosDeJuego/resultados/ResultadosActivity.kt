package com.fic.memotoriweb.ui.modosDeJuego.resultados

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fic.memotoriweb.Globales
import com.fic.memotoriweb.R
import com.fic.memotoriweb.databinding.ActivityResultadosBinding
import com.fic.memotoriweb.ui.modosDeJuego.resultados.resultadosRV.ResultadosAdapter

class ResultadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadosBinding
    private lateinit var adapter: ResultadosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRV()
    }


    private fun initRV(){
        Log.i("Globales", "initRV: ${Globales.resultadosList}")
        adapter = ResultadosAdapter(Globales.resultadosList)
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }


}