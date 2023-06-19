package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class RoomCreationResponseModel(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class EnabledCodecsItem(

	@field:SerializedName("mime")
	val mime: String? = null,

	@field:SerializedName("fmtpLine")
	val fmtpLine: String? = null
)

data class Data(

	@field:SerializedName("metadata")
	val metadata: String? = null,

	@field:SerializedName("emptyTimeout")
	val emptyTimeout: Int? = null,

	@field:SerializedName("creationTime")
	val creationTime: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("activeRecording")
	val activeRecording: Boolean? = null,

	@field:SerializedName("turnPassword")
	val turnPassword: String? = null,

	@field:SerializedName("enabledCodecs")
	val enabledCodecs: List<EnabledCodecsItem?>? = null,

	@field:SerializedName("sid")
	val sid: String? = null,

	@field:SerializedName("maxParticipants")
	val maxParticipants: Int? = null,

	@field:SerializedName("numParticipants")
	val numParticipants: Int? = null
)
