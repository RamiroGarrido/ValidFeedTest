package com.example.validtest.modulos.container

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.validtest.R
import com.example.validtest.adapters.ViewPagerAdapter
import com.example.validtest.databinding.FragmentContainerBinding
import com.example.validtest.main.MainActivity
import com.example.validtest.modulos.artist.ArtistFragment
import com.example.validtest.modulos.track.TrackFragment
import com.google.android.material.tabs.TabLayoutMediator

class FragmentContainer : Fragment() {

    private lateinit var binding: FragmentContainerBinding
    //Se definen los datos iniciales para la creacion del fragment contenedor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_container, container, false)

            binding.lifecycleOwner = this
            retainInstance = true
            binding.root
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
            null
        }

    }
    //Se configura el viewpager2 y los tabs para que actuen juntos.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            binding.viewPagerMain.adapter =
                ViewPagerAdapter(
                    this, listOf(
                        TrackFragment(),
                        ArtistFragment()
                    )
                )
            val iconos = resources.obtainTypedArray(R.array.tab_pictures)
            TabLayoutMediator(
                (activity as MainActivity).binding.tabLayoutMain,
                binding.viewPagerMain
            ) { tab, position ->
                tab.text = resources.getStringArray(R.array.tab_names)[position]
                tab.setIcon(
                    iconos.getResourceId(
                        position,
                        (activity as MainActivity).constantes.DEFAULT
                    )
                )
            }.attach()
            iconos.recycle()
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
}