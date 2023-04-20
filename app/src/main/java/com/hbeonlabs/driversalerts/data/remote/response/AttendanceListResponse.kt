package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class AttendanceListResponse(

	@field:SerializedName("AttendanceListResponse")
	val attendanceListResponse: List<AttendanceListResponseItem?>? = null
)

data class AttendanceListResponseItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

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
	val longitude: Any? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
