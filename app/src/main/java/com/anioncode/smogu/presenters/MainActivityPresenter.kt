package com.anioncode.smogu.presenters

import android.view.View
import java.lang.Exception

class MainActivityPresenter(private var view: View) {

    fun setProgress(counter: Int,size :Int){
        var tmpProgress:Int = ((counter.toFloat()/size.toFloat())*100).toInt()
       view.setProgressBarProgress(tmpProgress)
    }

    interface View {
        fun initView()
        fun initNavigationController()
        fun firstTimeAction()
        fun systemPrivilegesCheck()
        fun fetchDataFromServer()
        fun showProgressBar()
        fun hideProgressBar()
        fun setProgressBarProgress(int: Int)
    }
}