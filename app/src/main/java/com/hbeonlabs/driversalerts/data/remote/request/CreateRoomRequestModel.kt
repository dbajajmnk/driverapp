package com.hbeonlabs.driversalerts.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateRoomRequestModel(

	@field:SerializedName("roomName")
	val roomName: String? = null
)
