package com.example.validtest.modulos

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.RoomDatabase
import com.example.validtest.repository.remote.URLService
import com.example.validtest.utilities.Constantes
//ViewModel Factory que carga las variables necesarias para el uso del viewmodel.
//Se utilizan las instancias que no cambian de la actividad main (MainActivity)
class FragmentVMFactory(
    val servicio: URLService,
    val database: RoomDatabase,
    val constantes: Constantes,
    val resources: Resources
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FragmentViewModel::class.java)) {
            return FragmentViewModel(servicio, database, constantes, resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}