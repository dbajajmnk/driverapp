package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class BasicMessageResponse(
    @field:SerializedName("message")
    val message:String? = null
)
