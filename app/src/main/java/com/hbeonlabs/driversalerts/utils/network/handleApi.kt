package com.hbeonlabs.driversalerts.utils.network

import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

fun <T : Any> handleApi(
    execute: () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {

            /*     Timber.d("Error Body : ${response.errorBody().toString()}")
                 Timber.d("Api Error: ${response.message()} with code: ${response.code()} ")
                 return if (response.errorBody() != null) {
                     val type = object : TypeToken<BasicApiResponse<Unit>>() {}.type
                     val errorResponse: BasicApiResponse<Unit> =
                         GsonSingleton().fromJson(response.errorBody()!!.string(), type)
                     NetworkResult.Error(code = response.code(), message = errorResponse.message)
                 } else {
                     NetworkResult.Error(code = response.code(), message = response.message())
                 }*/
            NetworkResult.Error(code = response.code(), message = response.message())

        }
    } catch (e: HttpException) {
        Timber.d("Http Exception : $e with message ${e.message}")
        NetworkResult.Error(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        Timber.d("Exception : $e with message ${e.message}")
        NetworkResult.Exception(e)
    } catch (e: NoConnectivityException) {
        Timber.d("Exception : $e with message ${e.message}")
        NetworkResult.Exception(e)
    }

}