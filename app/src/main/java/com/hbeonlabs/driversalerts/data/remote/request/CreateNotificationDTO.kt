package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateNotificationDTO(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("routeId")
	val routeId: Int? = null,

	@field:SerializedName("schoolId")
	val schoolId: Int? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: Int? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)
