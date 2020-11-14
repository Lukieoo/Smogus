package com.anioncode.smogu.presenters

class StatsFragmentPresenter(private var view: MainActivityPresenter.View) {

    interface View {
        //Get data saved from SharedPreference
        fun getSharedPreferenceData()
        //Check possibility connection with internet and fetch sensor data
        fun fetchSingleData()
        //Get Sensor Data From Station
        fun getDataStationSensor()
        //Init Ui events
        fun initView()
    }
}