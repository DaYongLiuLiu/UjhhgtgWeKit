package dev.ujhhgtg.wekit.features.api.net.abc

interface WeRequestCallback {
    fun onSuccess(json: String, bytes: ByteArray?)
    fun onFailure(errType: Int, errCode: Int, errMsg: String)
}
