package com.hbeonlabs.driversalerts.data.remote.request

data class DeviceConfigurationRequest(
    val deviceId:String? = null,
    val bluetoothId:String? =  null,
    val vehicleId:String? =  null,
    val licenseKey:String? =  null,

)
