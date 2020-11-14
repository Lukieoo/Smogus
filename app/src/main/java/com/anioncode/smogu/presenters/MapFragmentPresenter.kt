package com.anioncode.smogu.presenters

class MapFragmentPresenter(private var view: MapFragmentPresenter.View) {

    interface View {
        //Initialize View events ,
        fun initView()
        //Initialize View events
        fun setMapFragment()
        // set Marker on Map
        fun setMarkerOnMap()
    }
}