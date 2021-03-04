package com.example.validtest.main

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.validtest.R
import com.example.validtest.databinding.ActivityMainBinding
import com.example.validtest.modulos.container.FragmentContainer
import com.example.validtest.repository.local.room.LocalDatabase
import com.example.validtest.repository.remote.ConnectionService
import com.example.validtest.repository.remote.URLService
import com.example.validtest.utilities.Constantes

class MainActivity : AppCompatActivity() {
    //Se inicializan las variables al iniciar la actividad para reusarlas en todos los fragmentos hijos
    val constantes = Constantes()
    val conexion =
        ConnectionService().getRetrofit(constantes.URL_API).create(URLService::class.java)
    var database:RoomDatabase? = null

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    //Carga las configuraciones iniciales de la actividad y carga el fragmento contenedor.
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            viewModel =
                ViewModelProvider(this).get(MainViewModel::class.java)
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            database = LocalDatabase.getInstance(application)
            binding.toolbar.title = getString(R.string.appName)
            binding.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.White
                )
            )
            setSupportActionBar(binding.toolbar)
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.containerFragmentMain)
            if (currentFragment == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.containerFragmentMain, FragmentContainer()).commit()
            }


        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }
/**
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }**/
    //Verifica si el usuario tiene acceso a internet
    fun conectadoAInternet(context: Context): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected) {
                true
            } else {
                Log.i(constantes.TAG_GENERAL, resources.getString(R.string.errorInternet2))
                false
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
            false
        }
    }
    //Se crea un mensaje toast personalizado
    fun crearMensaje(contexto: Context, mensaje: String) {
        try {
            Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Se activa o desactiva la carga del progressbar que abarca toda la pantalla
    fun cargaActiva(activa: Boolean) {
        if (activa) {
            viewModel.vProgressBar.value = View.VISIBLE
            viewModel.vMainContainer.value = View.INVISIBLE
        } else {
            viewModel.vProgressBar.value = View.GONE
            viewModel.vMainContainer.value = View.VISIBLE
        }
    }

    //Se borra el llamado a la super clase para no volver al splash al usar el boton de atras
    override fun onBackPressed() {

    }
}