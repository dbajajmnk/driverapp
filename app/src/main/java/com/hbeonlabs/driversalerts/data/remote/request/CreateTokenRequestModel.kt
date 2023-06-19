package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateTokenRequestModel(

	@field:SerializedName("particpantName")
	val particpantName: String? = null,

	@field:SerializedName("roomName")
	val roomName: String? = null
)
