package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateAttendanceRequest(

	@field:SerializedName("studentId")
	val studentId: Int? = null,

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("inTime")
	val inTime: String? = null,

	@field:SerializedName("schoolId")
	val schoolId: Int? = null,

	@field:SerializedName("isPresent")
	val isPresent: Int? = null,

	@field:SerializedName("outTime")
	val outTime: String? = null
)
