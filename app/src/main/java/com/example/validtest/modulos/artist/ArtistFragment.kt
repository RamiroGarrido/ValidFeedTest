package com.example.validtest.modulos.artist

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.validtest.R
import com.example.validtest.adapters.ListViewAdapter
import com.example.validtest.adapters.RecyclerViewGeneralAdapter
import com.example.validtest.adapters.SpinnerRegionesAdapter
import com.example.validtest.databinding.FragmentGeneralBinding

import com.example.validtest.main.MainActivity
import com.example.validtest.modulos.FragmentVMFactory
import com.example.validtest.modulos.FragmentViewModel
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView
import com.squareup.picasso.Picasso

class ArtistFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentGeneralBinding
    private lateinit var viewModel: FragmentViewModel
    private lateinit var informacion:MutableList<String>

    //Se definen los datos iniciales para la creacion del fragment artist
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general, container, false)
            viewModel = ViewModelProvider(
                this,
                FragmentVMFactory(
                    (activity as MainActivity).conexion,
                    (activity as MainActivity).database!!,
                    (activity as MainActivity).constantes,
                    resources
                )
            ).get(FragmentViewModel::class.java)
            binding.viewModel = viewModel
            binding.lifecycleOwner = this
            retainInstance = true
            var sPreferences = (activity as MainActivity).getSharedPreferences(
                (activity as MainActivity).constantes.KEY_SHARED_P,
                AppCompatActivity.MODE_PRIVATE
            )
            viewModel.paisSpinner =
                sPreferences.getInt(
                    (activity as MainActivity).constantes.KEY_PAIS_A_INT,
                    (activity as MainActivity).constantes.DEFAULT_SPINNER
                )
            viewModel.paisTexto =
                (activity as MainActivity).constantes.REGIONES_C[viewModel.paisSpinner]
            setHasOptionsMenu(true)
            binding.root
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
            null
        }
    }
    //Se definen los observables y las configuraciones iniciales de las vistas
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            informacion =
                requireContext().resources.getStringArray(R.array.artist_info).toMutableList()
            binding.swipeRefreshGeneral.setOnRefreshListener(this)
            binding.recyclerViewGeneral.layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            setObservers()
            if ((activity as MainActivity).conectadoAInternet(requireContext())) {
                //Si es la primera vez que entra
                if (viewModel.artistsObtenidos.value == null) {
                    viewModel.obtenerArtists()
                }
                //Si ya existen datos (al rotar la pantalla por ej.)
                else {
                    binding.recyclerViewGeneral.adapter = RecyclerViewGeneralAdapter(
                        requireContext(),
                        this,
                        null,
                        viewModel.artistsObtenidos.value,
                        viewModel.pagina,
                        viewModel.imagenesTracksOffline,
                        viewModel.imagenesArtistsOffline
                    )
                    if (viewModel.estadoRecyclerView != null) {
                        binding.recyclerViewGeneral.layoutManager?.onRestoreInstanceState(
                            viewModel.estadoRecyclerView
                        )
                    }
                }
            } else {
                viewModel.cargarArtistsBD()
            }
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Se configura el toolbar con el spinner de regiones del mundo
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        try {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.toolbar_menu_items, menu)
            val spinnerRegiones = (menu.findItem(R.id.spinner)?.actionView as Spinner?)!!
            val adapter = SpinnerRegionesAdapter(
                requireContext(),
                (activity as MainActivity).constantes.REGIONES,
                resources.obtainTypedArray(R.array.banderas_regiones)
            )
            spinnerRegiones.adapter = adapter
            spinnerRegiones.setSelection(viewModel.paisSpinner, false)
            spinnerRegiones.onItemSelectedListener = this
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Contiene los observables que miran los cambios en el viewmodel
    private fun setObservers() {
        try {
            viewModel.artistsObtenidos.observe(viewLifecycleOwner) {
                try {
                    if (it != null) {
                        viewModel.persistirArtistBD()
                        binding.recyclerViewGeneral.adapter = RecyclerViewGeneralAdapter(
                            requireContext(),
                            this,
                            null,
                            it,
                            viewModel.pagina,
                            viewModel.imagenesTracksOffline,
                            viewModel.imagenesArtistsOffline
                        )
                        if (viewModel.estadoRecyclerView != null) {
                            binding.recyclerViewGeneral.layoutManager?.onRestoreInstanceState(
                                viewModel.estadoRecyclerView
                            )
                        }
                        viewModel.mostrarImagenSinDatos(false)
                    }else {
                        viewModel.mensajeError.value =
                            getString(R.string.errorInternet)
                        viewModel.mostrarImagenSinDatos(true)
                    }
                    (activity as MainActivity).cargaActiva(false)
                } catch (e: Exception) {
                    Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
                }
            }
            viewModel.existeArtistPagYPaisBD.observe(viewLifecycleOwner) {
                if (it != null) {
                    //Si se verifican datos de paginas o paises existentes en BD
                    if (it.isNotEmpty()) {
                        viewModel.cargarArtistsBD()
                    }
                    //Si no existen los datos en BD
                    else {
                        (activity as MainActivity).crearMensaje(
                            requireContext(),
                            resources.getString(R.string.errorOffline)
                        )
                    }
                }
            }
            viewModel.vRefrescando.observe(viewLifecycleOwner) {
                try {
                    if (it != null) {
                        binding.swipeRefreshGeneral.isRefreshing = it
                    }
                } catch (e: Exception) {
                    Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
                }
            }
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Contiene los eventos de click del paginador y del recyclerview child de la lista principal
    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.contenedorRVChild -> {
                    mostrarInformacion(v.tag as Int)
                }
                R.id.contenedorPaginaAnterior -> {
                    val pag = viewModel.pagina.minus(1)
                    if (pag > 0) {
                        if ((activity as MainActivity).conectadoAInternet(requireContext())) {
                            viewModel.estadoRecyclerView = null
                            viewModel.pagina--
                            viewModel.obtenerArtists()
                        } else {
                            viewModel.existenArtistsBD(viewModel.paisTexto, pag)
                        }
                    } else {
                        (activity as MainActivity).crearMensaje(
                            requireContext(),
                            resources.getString(R.string.errorPaginas)
                        )
                    }
                }
                R.id.contenedorPaginaSiguiente -> {
                    //Sumo las paginas anteriores al size actual para saber si es menor que el total de resultados
                    // (50 * pag-1) + pelis.size < total de resultados
                    //Entra si existen otras paginas
                    val limitePag =
                        viewModel.artistsObtenidos.value?.artists?.attr?.perPage?.toInt()
                    if ((limitePag?.times(
                            viewModel.pagina.minus(1)
                        ))!!.plus(viewModel.artistsObtenidos.value?.artists?.artist?.size!!) < viewModel.artistsObtenidos.value!!.artists.attr?.total?.toInt()!!
                    ) {
                        if ((activity as MainActivity).conectadoAInternet(requireContext())) {
                            viewModel.estadoRecyclerView = null
                            viewModel.pagina++
                            viewModel.obtenerArtists()
                        } else {
                            viewModel.existenArtistsBD(
                                viewModel.paisTexto,
                                viewModel.pagina.plus(1)
                            )
                        }
                    } else {
                        (activity as MainActivity).crearMensaje(
                            requireContext(),
                            resources.getString(R.string.errorPaginas)
                        )
                    }
                }
            }
        }catch (e:Exception){
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Muestra la informacion completa del item seleccionado
    private fun mostrarInformacion(position: Int) {
        var listaObtenida = mutableListOf<String>()
        val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        val lista = dialog.findViewById<ExpandableHeightListView>(R.id.listViewDialog)
        val imagen = dialog.findViewById<ImageView>(R.id.imagenDialog)
        val imagenes = viewModel.artistsObtenidos.value!!.artists.artist[position].image
        val nombre = viewModel.artistsObtenidos.value!!.artists.artist[position].name
        val oyentes = viewModel.artistsObtenidos.value!!.artists.artist[position].listeners
        val mbid = viewModel.artistsObtenidos.value!!.artists.artist[position].mbid
        val url = viewModel.artistsObtenidos.value!!.artists.artist[position].url
        val streamable =
            viewModel.artistsObtenidos.value!!.artists.artist[position].streamable
        if (imagenes.isNullOrEmpty()) {
            if (viewModel.imagenesArtistsOffline != null) {
                if(viewModel.imagenesArtistsOffline!![position]!=null) {
                    imagen.setImageBitmap(viewModel.imagenesArtistsOffline!![position])
                }
                else{
                    imagen.setImageResource(R.drawable.imagen_vacia)
                }
            } else {
                imagen.setImageResource(R.drawable.imagen_vacia)
            }
        } else {
            Picasso.get()
                .load(imagenes.last().text)
                .placeholder(R.drawable.imagen_vacia)
                .error(R.drawable.imagen_vacia).into(imagen)
        }
        if (nombre.isNullOrEmpty()) {
            listaObtenida.add(getString(R.string.sinDatos))
        } else {
            listaObtenida.add(nombre)
        }
        if (oyentes.isNullOrEmpty()) {
            listaObtenida.add(getString(R.string.sinDatos))
        } else {
            listaObtenida.add(oyentes)
        }
        if (mbid.isNullOrEmpty()) {
            listaObtenida.add(getString(R.string.sinDatos))
        } else {
            listaObtenida.add(mbid)
        }
        if (url.isNullOrEmpty()) {
            listaObtenida.add(getString(R.string.sinDatos))
        } else {
            listaObtenida.add(url)
        }
        if (streamable.isNullOrEmpty()) {
            listaObtenida.add(getString(R.string.sinDatos))
        } else {
            listaObtenida.add(streamable)
        }
        lista.adapter = ListViewAdapter(
            requireContext(),
            informacion.toTypedArray(),
            listaObtenida.toTypedArray()
        )
        lista.isExpanded = true
        dialog.show()
    }

    fun cambioDeRegion() {
        try {
            viewModel.estadoRecyclerView = null
            viewModel.pagina = 1
            obtenerDatosNuevos()
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Carga nuevos datos o carga datos offline dependiendo del internet
    private fun obtenerDatosNuevos() {
        try {
            (activity as MainActivity).cargaActiva(true)
            if ((activity as MainActivity).conectadoAInternet(requireContext())) {
                viewModel.obtenerArtists()
            } else {
                viewModel.cargarArtistsBD()
            }
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Carga nuevos datos o carga datos offline al actualizar utilizando el swipe refresh
    override fun onRefresh() {
        try {
            viewModel.estadoRecyclerView = null
            viewModel.vRefrescando.value = false
            obtenerDatosNuevos()
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Guarda el estado del recyclerview al entrar en onStop
    override fun onStop() {
        try {
            super.onStop()
            viewModel.estadoRecyclerView =
                binding.recyclerViewGeneral.layoutManager?.onSaveInstanceState()
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Se cambia la region de busqueda actual y refresca los datos. Por falta de tiempo queda pendiente pasar la carga del pais de shared preferences a room
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        try {
            viewModel.paisSpinner = position
            viewModel.paisTexto = (activity as MainActivity).constantes.REGIONES_C[position]
            val sPreferences = (activity as MainActivity).getSharedPreferences(
                (activity as MainActivity).constantes.KEY_SHARED_P,
                AppCompatActivity.MODE_PRIVATE
            )
            //Se guarda la region en shared preferences
            sPreferences.edit()
                .putInt((activity as MainActivity).constantes.KEY_PAIS_A_INT, position)
                .commit()
            cambioDeRegion()
        } catch (e: Exception) {
            Log.i((activity as MainActivity).constantes.TAG_GENERAL, e.message!!)
        }
    }
    //No se desea hacer nada
    override fun onNothingSelected(parent: AdapterView<*>?) {
        //No se hace nada
    }
    //Se cancelan los trabajos en segundo plano al destruir el fragment.
    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelar()
    }
}