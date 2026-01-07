package com.fic.memotoriweb.ui.smartScreen

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.Fotos
import com.fic.memotoriweb.data.db.FotosDao
import com.fic.memotoriweb.databinding.ActivitySmartBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SmartActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmartBinding
    private lateinit var fotosDao: FotosDao
    private lateinit var adapter: FotosAdapter
    private var idCat: Long = 6

    override fun onCreate(savedInstanceState: Bundle?) {

        fotosDao = DatabaseProvider.GetDataBase(applicationContext).GetFotosDao()
        super.onCreate(savedInstanceState)
        binding = ActivitySmartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRV()
    }

    private fun initRV() {

        CoroutineScope(Dispatchers.IO).launch {

            val listaFotos = fotosDao.getAllFotos(idCat)

            withContext(Dispatchers.Main) {
                Log.i("Fotos", listaFotos.toString())

                adapter = FotosAdapter(listaFotos)
                binding.rvFotos.layoutManager = LinearLayoutManager(this@SmartActivity)
                binding.rvFotos.adapter = adapter
            }
        }
    }

}