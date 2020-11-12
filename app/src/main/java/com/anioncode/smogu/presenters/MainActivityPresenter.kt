package com.anioncode.smogu.presenters

import android.view.View
import java.lang.Exception

class MainActivityPresenter(private var view: View) {

    interface View {
        fun initView()
        fun initNavigationController()
        fun firstTimeAction()
        fun systemPrivilegesCheck()
        fun fetchDataFromServer()
    }
}