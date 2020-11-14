package com.anioncode.smogu.presenters

class ChartsFragmentPresenter(private var view: ChartsFragmentPresenter.View) {

    interface View {
        //Get data saved from SharedPreference
        //My Saved Data in SharedPreferences
       fun mySavedData()
        ///Show data on charts
        fun getDataRecyclerStation()
    }
}