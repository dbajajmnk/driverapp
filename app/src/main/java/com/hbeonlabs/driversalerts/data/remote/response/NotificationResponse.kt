package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName


data class NotificationResponse(
	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val list: List<NotificationResponseItem>? = null,
)


data class NotificationResponseItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("latitude")
	val latitude: Float? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("routeId")
	val routeId: String? = null,

	@field:SerializedName("schoolId")
	val schoolId: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("longitude")
	val longitude: Float? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
