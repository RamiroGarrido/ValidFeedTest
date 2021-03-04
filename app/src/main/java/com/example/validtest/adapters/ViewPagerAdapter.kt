package com.example.validtest.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

//Adaptador para los dos fragments principales:Tracks y Artists
class ViewPagerAdapter(
    padre:Fragment,
    private val fragmentosPagina: List<Fragment>
) : FragmentStateAdapter(padre) {
    override fun getItemCount(): Int {
        return fragmentosPagina.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentosPagina[position]
    }

}