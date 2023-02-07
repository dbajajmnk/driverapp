package com.hbeonlabs.driversalerts.utils.network

import okio.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "Please Check Your Internet Connection"
}