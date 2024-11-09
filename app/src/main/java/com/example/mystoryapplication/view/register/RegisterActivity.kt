package com.example.mystoryapplication.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapplication.databinding.ActivityRegisterBinding
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isLoading.observe(this){
            showLoading(it)
        }

        binding.btnLogin.setOnClickListener{ goToLogin() }
        binding.btnRegister.setOnClickListener{ register() }
        playAnimation()
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun register() {
        val username = binding.edRegisterName.editText?.text.toString()
        val email = binding.edRegisterEmail.editText?.text.toString()
        val password = binding.edRegisterPassword.editText?.text.toString()
        if (username == "" || email == "" || password == "") {
            AlertDialog.Builder(this).apply {
                setTitle("Alert!!")
                setMessage("Username, Email Atau Password Kosong")
                setPositiveButton("Ok") { _, _ ->
                    showLoading(false)
                }
                create()
                show()
                }
            }else{
                viewModel.tryRegis(username, email, password)
                viewModel.regisResponse.observe(this){regisStatus ->
                    if (regisStatus?.error != true){
                        AlertDialog.Builder(this@RegisterActivity).apply {
                            setTitle("Success!")
                            setMessage("Selamat Akun Anda Sudah Berhasil Di Buat, Silahkan Login Dengan Akun Tersebut")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }
                viewModel.isFailure.observe(this){
                    if (it){
                        AlertDialog.Builder(this).apply {
                            setTitle("Alert!!")
                            setMessage("Registrasi Gagal")
                            setPositiveButton("Ok") { _, _ ->
                                showLoading(false)
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }

        private fun playAnimation() {
            ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title =
                ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
            val nameEditText =
                ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(200)
            val emailEditText =
                ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(200)
            val passwordEditTex =
                ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(200)
            val btnSignUp =
                ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)
            val btnSignIn =
                ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(
                    title,
                    nameEditText,
                    emailEditText,
                    passwordEditTex,
                    btnSignUp,
                    btnSignIn
                )
                startDelay = 200
            }.start()
        }
    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.INVISIBLE
        }
    }
}