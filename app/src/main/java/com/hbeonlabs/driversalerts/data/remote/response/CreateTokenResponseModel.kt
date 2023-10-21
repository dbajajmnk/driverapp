package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class CreateTokenResponseModel(

	@field:SerializedName("data")
	val data: TokenData? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	val message: String? = null,

	var roomName: String? = null
)

data class TokenData(

	@field:SerializedName("token")
	val token: String? = null
)
