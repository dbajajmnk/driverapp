package com.hbeonlabs.driversalerts.utils.network.interceptors


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class AuthInterceptorImpl @Inject constructor(
    // private val pref: SharedPreferenceManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val accessToken = " pref.getAccessToken()"

        return chain.proceed(newRequestWithAccessToken(accessToken ?: "", request))
    }

    private fun newRequestWithAccessToken(accessToken: String, request: Request): Request =
        request.newBuilder()
            .header("x-access-token", accessToken)
            .build()

}