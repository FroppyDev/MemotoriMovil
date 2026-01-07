package com.fic.memotoriweb.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.fic.memotoriweb.R
import com.fic.memotoriweb.data.network.ApiService
import com.fic.memotoriweb.data.network.RegisterModel
import com.fic.memotoriweb.data.network.RegisterResponse
import com.fic.memotoriweb.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    private fun initComponents() {

        binding.btnRegister.setOnClickListener {

            verificarUsuario()

        }

    }

    private fun verificarUsuario() {
        if (binding.etPassword.text.toString() == binding.etPasswordConfirm.text.toString()) {
            if (binding.etPassword.text.toString().length >= 8) {

                lifecycleScope.launch {
                    val response = ApiService().createUser(RegisterModel(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    ))

                    if (response.isSuccessful) {
                        loginActivity()
                    } else {
                        binding.etEmail.error = "El email ya existe"
                    }
                }

            }  else {
                binding.etPasswordConfirm.error = "La contraseña debe tener al menos 8 caracteres"
            }
        } else {
            binding.etPasswordConfirm.error = "Las contraseñas no coinciden"
        }
    }

    private fun loginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
