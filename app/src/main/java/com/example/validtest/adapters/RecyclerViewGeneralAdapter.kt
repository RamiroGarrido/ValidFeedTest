package com.example.validtest.adapters

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.validtest.R
import com.example.validtest.repository.remote.models.ArtistDTO
import com.example.validtest.repository.remote.models.TrackDTO
import com.squareup.picasso.Picasso

//ADAPTADOR UTILIZADO PARA MOSTRAR LA LISTA PRINCIPAL DE TRACKS Y ARTISTAS
class RecyclerViewGeneralAdapter(
    val context: Context,
    private val handler: View.OnClickListener,
    private var tracks: TrackDTO?,
    var artists: ArtistDTO?,
    val pagina: Int,
    private val imagenesTracksOff:MutableList<Bitmap?>?,
    private val imagenesArtistsOff:MutableList<Bitmap?>?
) : RecyclerView.Adapter<RecyclerViewGeneralAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_child, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (tracks != null) {
            tracks!!.tracks.track?.size!!
        } else {
            artists!!.artists.artist.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            //CARGA LA LISTA DE ITEMS DE TRACKS
            if (tracks != null) {
                val imagenes = tracks!!.tracks.track?.get(position)?.image
                //Si no existe la imagen, coloco una cargada de BD o una por defecto
                if (imagenes.isNullOrEmpty()) {
                    if(imagenesTracksOff!=null){
                        if(imagenesTracksOff[position] != null) {
                            holder.imagen.setImageBitmap(imagenesTracksOff[position])
                        }else{
                            holder.imagen.setImageResource(R.drawable.imagen_vacia)
                        }
                    }else {
                        holder.imagen.setImageResource(R.drawable.imagen_vacia)
                    }
                }
                //Carga la URL de la ultima imagen (es decir, la de mejor tamaño)
                else {
                    Picasso
                        .get()
                        .load(imagenes[imagenes.lastIndex].text)
                        .placeholder(R.drawable.imagen_vacia)
                        .error(R.drawable.imagen_vacia)
                        .into(holder.imagen)
                }
                if (tracks!!.tracks.track?.get(position)?.attr?.rank.isNullOrEmpty()) {
                    holder.texto1.text = holder.itemView.context.getString(R.string.sinDatos)
                } else {
                    val rank = tracks!!.tracks.track?.get(position)?.attr?.rank
                    holder.texto1.text = holder.itemView.context.getString(
                        R.string.rank,
                        rank
                    )
                }
                if (tracks!!.tracks.track?.get(position)?.name.isNullOrEmpty()) {
                    holder.texto2.text = holder.itemView.context.getString(R.string.sinDatos)
                } else {
                    holder.texto2.text = tracks!!.tracks.track?.get(position)?.name
                }
                if (tracks!!.tracks.track?.get(position)?.artist?.name.isNullOrEmpty()) {
                    holder.texto3.text = holder.itemView.context.getString(R.string.sinDatos)
                } else {
                    holder.texto3.text = tracks!!.tracks.track?.get(position)?.artist?.name
                }
                //Si la posicion es la ultima de la pagina de resultados
                if (position == itemCount - 1) {
                    //Si no existen más resultados en otras páginas no muestra el paginador
                    //Si existen menos de 50 resultados en la pagina actual, no existen paginas siguientes
                    if (tracks?.tracks?.track?.size!! < tracks!!.tracks.attr?.perPage?.toInt()!!) {
                        holder.contenedorPaginas.visibility = View.GONE
                    } else {
                        holder.contenedorPaginas.visibility = View.VISIBLE
                        holder.numeroPagina.text = pagina.toString()
                        holder.pAnterior.tag = position
                        holder.pSiguiente.tag = position
                        holder.pAnterior.setOnClickListener(handler)
                        holder.pSiguiente.setOnClickListener(handler)
                    }
                } else {
                    holder.contenedorPaginas.visibility = View.GONE
                }
                holder.contenedorMain.tag = position
                holder.contenedorMain.setOnClickListener(handler)
            }
            //CARGA LA LISTA DE ITEMS PARA ARTISTAS
            else {
                val imagenes = artists!!.artists.artist[position].image
                //Si no existe la imagen, coloco una cargada de BD o una por defecto
                if (imagenes.isNullOrEmpty()) {
                    if(imagenesArtistsOff!=null){
                        if(imagenesArtistsOff[position] != null) {
                            holder.imagen.setImageBitmap(imagenesArtistsOff[position])
                        }else{
                            holder.imagen.setImageResource(R.drawable.imagen_vacia)
                        }
                    }else {
                        holder.imagen.setImageResource(R.drawable.imagen_vacia)
                    }
                }
                //Carga la URL de la ultima imagen (es decir, la de mejor tamaño)
                else {
                    Picasso
                        .get()
                        .load(imagenes[imagenes.lastIndex].text)
                        .placeholder(R.drawable.imagen_vacia)
                        .error(R.drawable.imagen_vacia)
                        .into(holder.imagen)
                }
                if (artists!!.artists.artist[position].name.isNullOrEmpty()) {
                    holder.texto1.text = holder.itemView.context.getString(R.string.sinDatos)
                } else {
                    holder.texto1.text = artists!!.artists.artist[position].name
                }
                if (artists!!.artists.artist[position].listeners.isNullOrEmpty()) {
                    holder.texto2.text = holder.itemView.context.getString(R.string.sinDatos)
                } else {
                    holder.texto2.text = holder.itemView.context.getString(
                        R.string.oyentes,
                        artists!!.artists.artist[position].listeners
                    )
                }
                //Si la posicion es la ultima de la pagina de resultados muestra el paginador
                if (position == itemCount - 1) {
                    //Si no existen más resultados en otras páginas no muestra el paginador
                    //Si existen menos de 50 resultados en la pagina actual, no existen paginas siguientes
                    if (artists?.artists?.artist?.size!! < artists!!.artists.attr?.perPage?.toInt()!!) {
                        holder.contenedorPaginas.visibility = View.GONE
                    } else {
                        holder.contenedorPaginas.visibility = View.VISIBLE
                        holder.numeroPagina.text = pagina.toString()
                        holder.pAnterior.tag = position
                        holder.pSiguiente.tag = position
                        holder.pAnterior.setOnClickListener(handler)
                        holder.pSiguiente.setOnClickListener(handler)
                    }
                } else {
                    holder.contenedorPaginas.visibility = View.GONE
                }
                holder.contenedorMain.tag = position
                holder.contenedorMain.setOnClickListener(handler)
            }
        } catch (e: Exception) {
            Log.i(
                "RGM",
                e.message!!
            )
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val contenedorMain: LinearLayout =
            v.findViewById<View>(R.id.contenedorRVChild) as LinearLayout
        val contenedorPaginas: LinearLayout =
            v.findViewById<View>(R.id.contenedorPaginas) as LinearLayout
        val pAnterior: LinearLayout =
            v.findViewById<View>(R.id.contenedorPaginaAnterior) as LinearLayout
        val numeroPagina: TextView =
            v.findViewById<TextView>(R.id.numeroPaginaRecyclerViewChild) as TextView
        val pSiguiente: LinearLayout =
            v.findViewById<View>(R.id.contenedorPaginaSiguiente) as LinearLayout
        var imagen: ImageView = v.findViewById<View>(R.id.imagenRVChild) as ImageView
        var texto1: TextView = v.findViewById<View>(R.id.texto1RVChild) as TextView
        var texto2: TextView = v.findViewById<View>(R.id.texto2RVChild) as TextView
        var texto3: TextView = v.findViewById<View>(R.id.texto3RVChild) as TextView

    }
}