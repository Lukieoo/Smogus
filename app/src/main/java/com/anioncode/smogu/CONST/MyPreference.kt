package com.anioncode.smogu.CONST

import android.content.Context

class MyPreference(context:Context){
    val PRERFERENCES_NAME="SHAREDPREFRENCES"
    val PRERFERENCES_ID="ID"
    val PRERFERENCES_STATION_NAME="STATION_NAME"
    val PRERFERENCES_STATION_STREET="STATION_STREET"
    val PRERFERENCES_STATION_PROVINCE="STATION_PROVINCE"

    val preference=context.getSharedPreferences(PRERFERENCES_NAME,Context.MODE_PRIVATE)

    fun getID():String{
        return preference.getString(PRERFERENCES_ID,"").toString()
    }
    fun setID(id:String){
        val editor=preference.edit()
        editor.putString(PRERFERENCES_ID,id)
        editor.apply()
    }


    fun getSTATION_NAME():String{
        return preference.getString(PRERFERENCES_STATION_NAME,"").toString()
    }
    fun setSTATION_NAME(name:String){
        val editor=preference.edit()
        editor.putString(PRERFERENCES_STATION_NAME,name)
        editor.apply()
    }

    fun getSTATION_STREET():String{
        return preference.getString(PRERFERENCES_STATION_STREET,"").toString()
    }
    fun setSTATION_STREET(name:String){
        val editor=preference.edit()
        editor.putString(PRERFERENCES_STATION_STREET,name)
        editor.apply()
    }
    fun getSTATION_PROVINCE():String{
        return preference.getString(PRERFERENCES_STATION_PROVINCE,"").toString()
    }
    fun setSTATION_PROVINCE(name:String){
        val editor=preference.edit()
        editor.putString(PRERFERENCES_STATION_PROVINCE,name)
        editor.apply()
    }


}