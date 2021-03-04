package com.example.validtest.adapters

import android.content.Context
import android.text.util.Linkify
import android.text.util.Linkify.WEB_URLS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.validtest.R
//Adaptador para listview el cual cargara los detalles de los tracks o artistas seleccionados
class ListViewAdapter(
    val context: Context,
    val textoEstatico: Array<String>,
    val peliObtenida: Array<String?>
) : BaseAdapter() {
    override fun getCount(): Int {
        return peliObtenida.size
    }

    override fun getItem(position: Int): String? {
        return peliObtenida[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    //Devuelve la vista a utilizar por los listview que contienen los detalles de los tracks y artistas
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.listview_child, null, false)
            val tEstatico = view.findViewById<TextView>(R.id.textoEstaticoLVC)
            val tObtenido = view.findViewById<TextView>(R.id.textoObtenidoLVC)
            tEstatico.text = textoEstatico[position]
            if (peliObtenida[position].isNullOrEmpty()) {
                tObtenido.text = context.resources.getString(R.string.sinDatos)
            } else {
                tObtenido.text = peliObtenida[position]
            }
            //Se setea la pagina web o el video como hipervinculo
            if (textoEstatico[position] == context.getString(R.string.url) || textoEstatico[position] == context.getString(
                    R.string.artistaURL
                )
            ) {
                Linkify.addLinks(tObtenido, WEB_URLS)
                tObtenido.linksClickable = true
            }
            view
        } catch (e: Exception) {
            View(context)
        }
    }
}