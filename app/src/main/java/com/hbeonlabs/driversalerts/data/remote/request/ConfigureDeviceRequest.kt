package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class ConfigureDeviceRequest(

	@field:SerializedName("deviceType")
	val deviceType: Int? = null,

	@field:SerializedName("expiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("licenseKey")
	val licenseKey: String? = null,

	@field:SerializedName("schoolId")
	val schoolId: Int? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: String? = null,

	@field:SerializedName("modelNo")
	val modelNo: String? = null,

	@field:SerializedName("deviceId")
	val deviceId: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("serialNo")
	val serialNo: String? = null,

	@field:SerializedName("bluetoothId")
	val bluetoothId: String? = null
)
