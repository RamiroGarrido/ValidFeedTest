package com.example.validtest.modulos

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import androidx.room.RoomDatabase
import com.example.validtest.R
import com.example.validtest.repository.local.entities_db.ArtistEntity
import com.example.validtest.repository.local.entities_db.TrackEntity
import com.example.validtest.repository.local.room.LocalDatabase
import com.example.validtest.repository.remote.URLService
import com.example.validtest.repository.remote.models.*
import com.example.validtest.utilities.Constantes
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FragmentViewModel(
    private val service: URLService,
    private val database: RoomDatabase,
    private val constantes: Constantes,
    private val resources: Resources
) :
    ViewModel() {
    private val trackDAO = (database as LocalDatabase).trackDAO()
    private val artistDAO = (database as LocalDatabase).artistDAO()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    var jobTracks: Job? = null
    var jobArtists: Job? = null
    var vSinDatos = MutableLiveData<Int>()
    var vRefrescando = MutableLiveData<Boolean>()
    var mensajeError = MutableLiveData<String>()
    var tracksObtenidos = MutableLiveData<TrackDTO>()
    var tracksBD = MutableLiveData<List<TrackEntity?>>()
    var existeTrackPagYPaisBD = MutableLiveData<List<TrackEntity?>>()
    var existeArtistPagYPaisBD = MutableLiveData<List<ArtistEntity?>>()
    var imagenesTracksOffline: MutableList<Bitmap?>? = null
    var imagenesArtistsOffline: MutableList<Bitmap?>? = null
    var artistsObtenidos = MutableLiveData<ArtistDTO>()
    var estadoRecyclerView: Parcelable? = null
    var paisSpinner: Int = constantes.DEFAULT_SPINNER
    var paisTexto: String = constantes.COLOMBIA_C
    var pagina: Int = 1
    //Se inicializan los datos.
    init {
        vSinDatos.value = View.GONE
        mensajeError.value = ""
    }

    //Obtiene tracks desde el webservice
    fun obtenerTracks() {
        try {
            jobTracks = viewModelScope.launch {
                try {
                    var response: Response<TrackDTO>? = null
                    response = service.getTopTracks(
                        constantes.API_KEY,
                        paisTexto,
                        pagina.toString()
                    )
                    if (response.isSuccessful) {
                        tracksObtenidos.value = response.body()
                    } else {
                        when (response?.code()) {
                            constantes.HTTP_UNAUTHORIZED -> mensajeError.value =
                                resources.getString(R.string.errorSinAutorizacion)
                            constantes.HTTP_FORBBIDEN -> mensajeError.value =
                                resources.getString(R.string.errorSinAutorizacion)
                            constantes.HTTP_NOT_FOUND -> mensajeError.value =
                                resources.getString(R.string.errorNoEncontrado)
                            else -> mensajeError.value =
                                resources.getString(
                                    R.string.errorCustom,
                                    response.errorBody().toString(),
                                    ""
                                )
                        }
                        mostrarImagenSinDatos(true)
                    }
                } catch (e: Exception) {
                    Log.i(constantes.TAG_GENERAL, e.message!!)
                }
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Obtiene artists desde el webservice
    fun obtenerArtists() {
        try {
            jobArtists = viewModelScope.launch {
                try {
                    var response: Response<ArtistDTO>? = null
                    response = service.getTopArtists(
                        constantes.API_KEY,
                        paisTexto,
                        pagina.toString()
                    )
                    if (response.isSuccessful) {
                        artistsObtenidos.value = response.body()
                    } else {
                        when (response?.code()) {
                            constantes.HTTP_UNAUTHORIZED -> mensajeError.value =
                                resources.getString(R.string.errorSinAutorizacion)
                            constantes.HTTP_FORBBIDEN -> mensajeError.value =
                                resources.getString(R.string.errorSinAutorizacion)
                            constantes.HTTP_NOT_FOUND -> mensajeError.value =
                                resources.getString(R.string.errorNoEncontrado)
                            else -> mensajeError.value =
                                resources.getString(
                                    R.string.errorCustom,
                                    response.errorBody().toString(),
                                    ""
                                )
                        }
                        mostrarImagenSinDatos(true)
                    }
                } catch (e: Exception) {
                    Log.i(constantes.TAG_GENERAL, e.message!!)
                }
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }
    //Hace visible la imagen sin datos y el mensaje asociado
    fun mostrarImagenSinDatos(sinDatos: Boolean) {
        try {
            if (sinDatos) {
                vSinDatos.value = View.VISIBLE
            } else {
                vSinDatos.value = View.GONE
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }

    //Inserta o actualiza datos de tracks dependiendo si existe ya una lista de datos por pais y pagina.
    fun persistirTrackBD() {
        ioScope.launch {
            try {
                //database.clearAllTables()
                val tracks = tracksObtenidos.value
                val tracksExistentes =
                    trackDAO.getTracksByCountryYPage(paisTexto, pagina.toString())
                //Inserta datos cuando no existen aun en BD
                if (tracksExistentes.isNullOrEmpty()) {
                    for ((index, track) in tracks!!.tracks.track!!.withIndex()) {
                        var entity = TrackEntity(
                            track.name,
                            track.duration,
                            track.listeners,
                            track.mbid,
                            track.url,
                            track.streamable?.text,
                            track.artist?.name,
                            track.artist?.mbid,
                            track.artist?.url,
                            obtenerByteArray(track.image?.last()?.text!!),
                            track.attr?.rank,
                            tracks.tracks.attr?.country,
                            tracks.tracks.attr?.page,
                            tracks.tracks.attr?.perPage,
                            tracks.tracks.attr?.totalPages,
                            tracks.tracks.attr?.total
                        )
                        trackDAO.insertTracks(entity)
                    }
                }
                //Hace un Update cuando los datos existen en BD
                else {
                    tracksExistentes!!.forEachIndexed() { index, track ->
                        track!!.name = tracks!!.tracks.track?.get(index)?.name
                        track.duration = tracks.tracks.track?.get(index)?.duration
                        track.listeners = tracks.tracks.track?.get(index)?.listeners
                        track.mbid = tracks.tracks.track?.get(index)?.mbid
                        track.url = tracks.tracks.track?.get(index)?.url
                        track.streamable = tracks.tracks.track?.get(index)?.streamable?.text
                        track.artistName = tracks.tracks.track?.get(index)?.artist?.name
                        track.artistMbid = tracks.tracks.track?.get(index)?.artist?.mbid
                        track.artistUrl = tracks.tracks.track?.get(index)?.artist?.url
                        if (tracks.tracks.track?.get(index)?.image != null) {
                            if (tracks.tracks.track?.get(index)?.image!!.last().text != null) {
                                track.image =
                                    obtenerByteArray(tracks.tracks.track?.get(index)?.image?.last()?.text!!)
                            }
                        }
                        track.rank = tracks.tracks.track?.get(index)?.attr?.rank
                        track.country = tracks.tracks.attr?.country
                        track.page = tracks.tracks.attr?.page
                        track.perPage = tracks.tracks.attr?.perPage
                        track.totalPages = tracks.tracks.attr?.totalPages
                        track.totalResults = tracks.tracks.attr?.total
                        trackDAO.updateTrack(track)
                    }
                }
                //val temp = trackDAO.getAllTracks()
                //Log.i(constantes.TAG_GENERAL, temp.toString())
            } catch (e: Exception) {
                Log.i(constantes.TAG_GENERAL, e.message!!)
            }
        }
    }

    //Inserta o actualiza datos de tracks dependiendo si existe ya una lista de datos por pais y pagina.
    fun persistirArtistBD() {
        ioScope.launch {
            try {
                val artists = artistsObtenidos.value
                val artistsExistentes =
                    artistDAO.getArtistsByCountryYPage(paisTexto, pagina.toString())
                //Inserta datos cuando no existen aun en BD
                if (artistsExistentes.isNullOrEmpty()) {
                    for ((index, artist) in artists!!.artists.artist!!.withIndex()) {
                        var entity = ArtistEntity(
                            artist.name,
                            artist.listeners,
                            artist.mbid,
                            artist.url,
                            artist.streamable,
                            obtenerByteArray(artist.image?.last()?.text!!),
                            artists.artists.attr?.country,
                            artists.artists.attr?.page,
                            artists.artists.attr?.perPage,
                            artists.artists.attr?.totalPages,
                            artists.artists.attr?.total
                        )
                        artistDAO.insertArtist(entity)
                    }
                }
                //Hace un Update cuando los datos existen en BD
                else {
                    artistsExistentes!!.forEachIndexed() { index, artist ->
                        artist!!.name = artists!!.artists.artist?.get(index)?.name
                        artist.listeners = artists!!.artists.artist?.get(index)?.listeners
                        artist.mbid = artists!!.artists.artist?.get(index)?.mbid
                        artist.url = artists!!.artists.artist?.get(index)?.url
                        artist.streamable = artists!!.artists.artist?.get(index)?.streamable
                        if (artists!!.artists.artist?.get(index)?.image != null) {
                            if (artists!!.artists.artist?.get(index)?.image!!.last().text != null) {
                                artist.image =
                                    obtenerByteArray(artists!!.artists.artist?.get(index)?.image?.last()?.text!!)
                            }
                        }
                        artist.country = artists!!.artists.attr?.country
                        artist.page = artists!!.artists.attr?.page
                        artist.perPage = artists!!.artists.attr?.perPage
                        artist.totalPages = artists!!.artists.attr?.totalPages
                        artist.totalResults = artists!!.artists.attr?.total
                        artistDAO.updateArtist(artist)
                    }
                }
            } catch (e: Exception) {
                Log.i(constantes.TAG_GENERAL, e.message!!)
            }
        }
    }

    //Verifica si existen paginas o paises en BD
    fun existenTracksBD(paisTexto: String, pagina: Int) {
        try {
            jobTracks = ioScope.launch {
                var lista = trackDAO.getTracksByCountryYPage(paisTexto, pagina.toString())
                if (lista.isNotEmpty()) {
                    this@FragmentViewModel.pagina = pagina
                }
                this@FragmentViewModel.existeTrackPagYPaisBD.postValue(lista)
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }

    //Verifica si existen paginas o paises en BD
    fun existenArtistsBD(paisTexto: String, pagina: Int) {
        try {
            jobArtists = ioScope.launch {
                var lista = artistDAO.getArtistsByCountryYPage(paisTexto, pagina.toString())
                if (lista.isNotEmpty()) {
                    this@FragmentViewModel.pagina = pagina
                }
                this@FragmentViewModel.existeArtistPagYPaisBD.postValue(lista)
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }

    //Se cargan los datos obtenidos de BD para que la interfaz los utilize o devuelve null si no hay datos
    fun cargarTracksBD() {
        try {
            jobTracks = ioScope.launch {
                var tracksObtenidos = trackDAO.getTracksByCountryYPage(paisTexto, pagina.toString())
                if (tracksObtenidos != null) {
                    if (tracksObtenidos.isNotEmpty()) {
                        imagenesTracksOffline = mutableListOf<Bitmap?>()
                        var listaSongs = mutableListOf<Song>()
                        var attr2: Attr2? = null
                        for ((index, track) in tracksObtenidos.withIndex()) {
                            listaSongs.add(
                                Song(
                                    track?.name,
                                    track?.duration,
                                    track?.listeners,
                                    track?.mbid,
                                    track?.url,
                                    Streamable(track?.streamable, track?.streamable),
                                    ArtistT(track?.artistName, track?.artistMbid, track?.artistUrl),
                                    null,
                                    Attr1(track?.rank)
                                )
                            )
                            if (track?.image != null) {
                                imagenesTracksOffline!!.add(
                                    BitmapFactory.decodeByteArray(
                                        track.image,
                                        0,
                                        track.image!!.size
                                    )
                                )
                            } else {
                                imagenesTracksOffline!!.add(null)
                            }
                            if (attr2 == null) {
                                attr2 = Attr2(
                                    track?.country,
                                    track?.page,
                                    track?.perPage,
                                    track?.totalPages,
                                    track?.totalResults
                                )
                            }
                        }
                        this@FragmentViewModel.tracksObtenidos.postValue(
                            TrackDTO(Tracks(listaSongs.toList(), attr2, null, null))
                        )
                    } else {
                        this@FragmentViewModel.tracksObtenidos.postValue(null)
                    }
                } else {
                    this@FragmentViewModel.tracksObtenidos.postValue(null)
                }

            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
            this@FragmentViewModel.tracksObtenidos.postValue(null)
        }
    }

    //Se cargan los datos obtenidos de BD para que la interfaz los utilize o devuelve null si no hay datos
    fun cargarArtistsBD() {
        try {
            jobArtists = ioScope.launch {
                var artistsObtenidos = artistDAO.getArtistsByCountryYPage(paisTexto, pagina.toString())
                if (artistsObtenidos != null) {
                    if (artistsObtenidos.isNotEmpty()) {
                        imagenesArtistsOffline = mutableListOf<Bitmap?>()
                        var listaArtistas = mutableListOf<ArtistA>()
                        var attr2: Attr2? = null
                        for ((index, artist) in artistsObtenidos.withIndex()) {
                            listaArtistas.add(
                                ArtistA(
                                    artist?.name,
                                    artist?.listeners,
                                    artist?.mbid,
                                    artist?.url,
                                    artist?.streamable,
                                    null
                                )
                            )
                            if (artist?.image != null) {
                                imagenesArtistsOffline!!.add(
                                    BitmapFactory.decodeByteArray(
                                        artist.image,
                                        0,
                                        artist.image!!.size
                                    )
                                )
                            } else {
                                imagenesArtistsOffline!!.add(null)
                            }
                            if (attr2 == null) {
                                attr2 = Attr2(
                                    artist?.country,
                                    artist?.page,
                                    artist?.perPage,
                                    artist?.totalPages,
                                    artist?.totalResults
                                )
                            }
                        }
                        this@FragmentViewModel.artistsObtenidos.postValue(
                            ArtistDTO(Artist(listaArtistas.toList(), attr2, null, null))
                        )
                    } else {
                        this@FragmentViewModel.artistsObtenidos.postValue(null)
                    }
                } else {
                    this@FragmentViewModel.artistsObtenidos.postValue(null)
                }

            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
            this@FragmentViewModel.artistsObtenidos.postValue(null)
        }
    }

    //Descarga una imagen y retorna un ByteArray para persistir en BD
    suspend fun obtenerByteArray(url: String?): ByteArray? = withContext(Dispatchers.IO) {
        return@withContext try {
            val uri = URL(url)
            val urlConnection = uri.openConnection() as HttpURLConnection
            val statusCode: Int = urlConnection.responseCode
            if (statusCode == constantes.HTTP_OK) {
                val inputStream: InputStream = urlConnection.inputStream
                Log.i(constantes.TAG_GENERAL, constantes.EXITO_CARGA)
                inputStream.readBytes()
            } else {
                Log.i(constantes.TAG_GENERAL, constantes.FALLA_CARGA)
                null
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
            null
        }
    }

    //Cancela los trabajos activos en segundo plano
    fun cancelar() {
        try {
            jobTracks?.cancel(constantes.CANCEL_CARGA_T)
            jobArtists?.cancel(constantes.CANCEL_CARGA_A)
            ioScope.cancel(constantes.CANCEL_CARGA_I)
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }

    //Cancela cualquier job pendiente en segundo plano y cierra la BD si muere el ciclo de vida
    override fun onCleared() {
        super.onCleared()
        try {
            cancelar()
            if (database.isOpen) {
                database.close()
            }
        } catch (e: Exception) {
            Log.i(constantes.TAG_GENERAL, e.message!!)
        }
    }
}