package com.example.driversalerts.utils.network

import com.example.driversalerts.utils.network.NetworkResult
import com.example.driversalerts.utils.network.NetworkResultCall
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResultCallAdapter(
  private val resultType: Type
) : CallAdapter<Type, Call<NetworkResult<Type>>> {

  override fun responseType(): Type = resultType

  override fun adapt(call: Call<Type>): Call<NetworkResult<Type>> {
      return NetworkResultCall(call)
  }
}