package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class ConfigureDeviceRequest(

	@field:SerializedName("deviceType")
	val deviceType: Int? = null,

	@field:SerializedName("licenseKey")
	val licenseKey: String? = null,

	@field:SerializedName("modelNo")
	val modelNo: String? = null,

	@field:SerializedName("deviceId")
	val deviceId: String? = null,

	@field:SerializedName("serialNo")
	val serialNo: String? = null,

	@field:SerializedName("bluetoothDeviceId")
	val bluetoothDeviceId: String? = null
)
