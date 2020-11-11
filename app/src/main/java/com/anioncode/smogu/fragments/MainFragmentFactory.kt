package com.anioncode.smogu.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject

class MainFragmentFactory
@Inject
constructor(
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {

            AboutFragment::class.java.name -> {
                val fragment = AboutFragment()
                fragment
            }
            ChartFragment::class.java.name -> {
                val fragment = ChartFragment()
                fragment
            }
            InfoFragment::class.java.name -> {
                val fragment = InfoFragment()
                fragment
            }
            MapFragment::class.java.name -> {
                val fragment = MapFragment()
                fragment
            }
            StatsFragment::class.java.name -> {
                val fragment = StatsFragment()
                fragment
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}