package com.fic.memotoriweb.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fic.memotoriweb.data.network.ApiService
import com.fic.memotoriweb.data.network.LoginModel
import com.fic.memotoriweb.data.network.LoginResponse
import com.fic.memotoriweb.data.network.SyncPrefs
import com.fic.memotoriweb.databinding.ActivityLoginBinding
import com.fic.memotoriweb.ui.categoryScreen.CategoryActivity
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    fun initComponents(){
        binding.btnLogin.setOnClickListener {

            try {
                lifecycleScope.launch {
                    val result = ApiService().login(
                        LoginModel(
                            binding.etUsuario.text.toString(),
                            binding.etPassword.text.toString()
                        )
                    )

                    if (result.body()?.message == "Login exitoso") {
                        verify(true, result)
                    } else {
                        verify(false, result)
                    }
                }
            } catch (e: Exception){
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

    }

    private fun verify(bol: Boolean, result: Response<LoginResponse>) {
        if (bol){

            SyncPrefs(this).saveUserId(result.body()?.user!!.id)
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)

        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }

}