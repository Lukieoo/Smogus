package com.anioncode.smogu.model.ModelSensorId

data class SensorbyID(
    var isClicked: Boolean = false,
    val key: String,
    val values: List<Value>
)