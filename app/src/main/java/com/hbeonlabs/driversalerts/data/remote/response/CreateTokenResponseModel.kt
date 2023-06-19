package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class CreateTokenResponseModel(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
