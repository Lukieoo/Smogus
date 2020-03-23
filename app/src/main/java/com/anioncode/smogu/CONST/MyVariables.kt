package com.anioncode.smogu.CONST

import com.anioncode.smogu.Model.ModelAll.FindAll
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.Model.ModelSensor.SensorsName
import com.anioncode.smogu.Model.ModelSensorId.SensorbyID

class MyVariables {

    companion object {
         var stationList: List<FindAll> = listOf()
        lateinit var sensorsNameList: List<SensorsName>
        lateinit var sensorbyIDList: ArrayList<SensorbyID>
        lateinit var modelIndexList: ArrayList<ModelIndex>
    }

}