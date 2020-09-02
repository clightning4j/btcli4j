package io.vincenzopalazzo.btcli4j.util

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object HttpRequestFactory {

    private const val BASE_URL = "https://blockstream.info"

    private val client = OkHttpClient()

    fun createRequest(url: String, type: String = "get", body: String = "", mediaType: MediaType = "application/json; charset=utf-8".toMediaType()): Request?{
        val completeUrl = "%s/%s".format(BASE_URL, url)
        when(type){
            "get" -> return buildGetRequest(completeUrl)
            "post" -> return buildPostRequest(completeUrl, body, mediaType)
        }
        return null
    }

    fun execRequest(request: Request): ByteArray{
        return client.newCall(request).execute().body!!.bytes()
    }

    private fun buildPostRequest(url: String, body: String, mediaType: MediaType): Request {
        val requestBody = RequestBody.Companion.create(mediaType, body)
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        return request
    }

    private fun buildGetRequest(url: String): Request {
        val request = Request.Builder().url(url).build()
        return request
    }

}