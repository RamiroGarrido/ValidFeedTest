package com.example.validtest.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.validtest.modulos.container.FragmentContainer
import com.example.validtest.utilities.Constantes

class MainViewModel:ViewModel() {
    //Variables observables para las visibilidades de la interfaz
    var vProgressBar = MutableLiveData<Int>()
    var vMainContainer = MutableLiveData<Int>()

    init {
        vProgressBar.value = View.GONE
        vMainContainer.value = View.VISIBLE
    }
}