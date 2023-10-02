package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateTokenRequestModel(

	@field:SerializedName("participant_name")
	val particpantName: String? = null,

	@field:SerializedName("room_name")
	val roomName: String? = null,

	@field:SerializedName("role")
	val role: String? = null
)
