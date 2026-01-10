package com.fic.memotoriweb.ui.flashcardsScreen

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.SyncStatus
import com.fic.memotoriweb.data.db.Tarjeta
import com.fic.memotoriweb.data.network.SyncRepository
import com.fic.memotoriweb.databinding.FlashcardItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FlashCardsViewHolder(view: View):RecyclerView.ViewHolder(view) {

    var binding = FlashcardItemBinding.bind(view)
    val tarjetasDao = DatabaseProvider.GetDataBase(binding.root.context).GetTarjetasDao()


    fun render(
        item: Tarjeta,
        onItemSelected: (Tarjeta, position: Int) -> Unit,
        position: Int
    ) {
        binding.tvConcepto.text = item.concepto
        binding.tvDefinicion.text = item.definicion
        binding.tvAuxiliar.text = item.definicionExtra
        binding.cvImage.setBackgroundColor(Color.argb(50,255,0,0)) // rojo semitransparente
        binding.tvAuxiliar.setBackgroundColor(Color.argb(50,0,255,0)) // verde

        binding.ivFlashCard.setImageDrawable(null)
        binding.ivFlashCard.setImageBitmap(null)

        if (item.imagen != null){
            if (item.imagen?.startsWith("http") == true) {
                Glide.with(binding.root.context)
                    .load(item.imagen)
                    .into(binding.ivFlashCard)
            } else {
                binding.ivFlashCard.setImageURI(Uri.fromFile(File(item.imagen)))
            }

        } else {
            binding.ivFlashCard.visibility = View.GONE
            binding.cvImage.visibility = View.GONE
        }

        if (item.definicionExtra != "" ){
            binding.tvAuxiliar.visibility = View.VISIBLE
        } else {
            binding.tvAuxiliar.visibility = View.GONE
        }

        if (item.definicionExtra == "" && item.imagen == null){
            binding.llAuxiliares.visibility = View.GONE
        }

        binding.cvPadre.setOnLongClickListener {
            OpcionesFlashcardDialog(binding.root.context, item)
            true
        }

        binding.cvPadre.setOnClickListener {
            onItemSelected(item, position)
        }
    }

    private fun OpcionesFlashcardDialog(context: Context, tarjeta: Tarjeta){

        var dialog = Dialog(context)
        var vista = R.layout.category_options_dialog
        dialog.setContentView(vista)

        var btnCompartir = dialog.findViewById<AppCompatButton>(R.id.btnCompartir)
        var btnEliminar = dialog.findViewById<AppCompatButton>(R.id.btnEliminar)

        btnEliminar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                tarjetasDao.updateFlashcard(
                    tarjeta.copy(
                        syncStatus = SyncStatus.PENDING_DELETE
                    )
                )

                SyncRepository(context.applicationContext).enqueueSync()

                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                }
            }
        }


        btnCompartir.setOnClickListener {

        }

        dialog.show()

    }
}