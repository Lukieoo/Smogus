package com.anioncode.smogu.CONST

import com.anioncode.smogu.model.ModelAll.FindAll
import com.anioncode.smogu.model.ModelIndex.ModelIndex
import com.anioncode.smogu.model.ModelSensor.SensorsName
import com.anioncode.smogu.model.ModelSensorId.SensorbyID

class MyVariables {

    companion object {
        var stationList: List<FindAll> = listOf()

        lateinit var sensorsNameList: List<SensorsName>
        var sizedApplication: Int = 0

        lateinit var sensorbyIDList: ArrayList<SensorbyID>
        lateinit var modelIndexList: ArrayList<ModelIndex>
        lateinit var SELECTED: String
        var SENSORNAME: String = "ST"
        lateinit var sensorIDList: ArrayList<String>
    }

}