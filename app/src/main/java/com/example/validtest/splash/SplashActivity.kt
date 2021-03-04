package com.example.validtest.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.validtest.R
import com.example.validtest.main.MainActivity
//Clase para mostrar una animacion principal y una animacion. Redirecciona al finalizar a la actividad principal (MainActivity)
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.supportActionBar?.hide()
        iniciarAnimacion()
    }

    fun iniciarAnimacion() {
        val contenedor = findViewById<LinearLayout>(R.id.splashContainer)
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        })
        contenedor.startAnimation(animation)
    }
}