package com.fic.memotoriweb.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fic.memotoriweb.MainActivity
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.db.DatabaseProvider
import com.fic.memotoriweb.data.db.Fotos
import com.fic.memotoriweb.data.db.Horarios
import com.fic.memotoriweb.data.db.SyncStatus
import com.fic.memotoriweb.data.imageControl.ImageManager
import com.fic.memotoriweb.databinding.ActivityCameraBinding
import com.fic.memotoriweb.ui.categoryScreen.CategoryActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
                // Ahora sí puedes salir o volver al MainActivity si lo deseas
                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                    var horariosDao = DatabaseProvider.GetDataBase(applicationContext).GetHorariosDao()
                    val fotosDao = DatabaseProvider.GetDataBase(applicationContext).GetFotosDao()
                    var horarios: List<Horarios>? = null
                    CoroutineScope(Dispatchers.IO).launch{
                        withContext(Dispatchers.IO){
                            horarios = horariosDao.getAllHorarios()
                        }

                        if (horarios!!.isNotEmpty()){
                            for (horario in horarios){

                                if (comprobarHorario(horario)){
                                    try {
                                        val img = ImageManager().imageToInternalStorage(applicationContext, output.savedUri!!)
                                        fotosDao.insertFoto(
                                            Fotos(
                                                rutaFoto = img!!,
                                                fechaHora = LocalDateTime.now().toString(),
                                                idCategoria = horario.idCategoria,
                                                userId = 1, //cambiar id a futuro
                                                latitud = null,
                                                longitud = null,
                                                syncStatus = SyncStatus.PENDING_CREATE
                                            )
                                        )
                                        Log.i("Fotos", "foto guardada en " + horario.idCategoria.toString())
                                    }catch (e: Exception){
                                        Log.i("error", e.toString())
                                    }
                                }

                            }
                        }

                    }

                    finish()
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comprobarHorario(
        horario: Horarios,
    ): Boolean {

        var horaInicio = java.time.LocalTime.parse(horario.horaInicio)
        var horaFin = java.time.LocalTime.parse(horario.horaFin)

        val horaActual = java.time.LocalTime.now()

        val dentroDelRango = when {
            horaInicio.isBefore(horaFin) -> horaActual in horaInicio..horaFin
            horaInicio.isAfter(horaFin) -> // Ej: horario de noche 22:00 a 05:00
                horaActual >= horaInicio || horaActual <= horaFin
            else -> false
        }

        return dentroDelRango

    }

    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }

}