package com.example.validtest.adapters

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.validtest.R

//Adaptador para cargar los paises del spinner del toolbar
class SpinnerRegionesAdapter(
    val context: Context,
    val codigosRegiones: ArrayList<String>,
    val banderasRegiones: TypedArray
) : BaseAdapter() {

    override fun getCount(): Int {
        return codigosRegiones.size
    }

    override fun getItem(position: Int): String {
        return codigosRegiones[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.menu_region_spinner, null)
            val icon = view.findViewById<ImageView>(R.id.imageViewRegion)
            icon.setImageResource(banderasRegiones.getResourceId(position,-1))
            view.findViewById<TextView>(R.id.textoRegion).text = codigosRegiones[position]
            view
        } catch (e: Exception) {
            View(context)
        }
    }
}