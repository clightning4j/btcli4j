/**
 *  C-lightning plugin to override Bitcoin backend plugin.
 *  Copyright (C) 2020 Vincenzo Palazzo vincenzopalazzodev@gmail.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package io.vincenzopalazzo.btcli4j.util

import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.*
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit


/**
 * @author https://github.com/vincenzopalazzo
 */
object HttpRequestFactory {

    private const val BASE_URL = "https://blockstream.info"
    private const val BASE_URL_TORV3 = "http://explorerzydxu5ecjrkwceayqybizmpjjznk5izmitf2modhcusuqlid.onion"
    private const val BASE_URL_TORV2 = "http://explorernuoc63nb.onion"
    private const val WAIT_TIME: Long = 60000

    private var proxyEnabled: Boolean = false
    private var client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

    fun configureProxy(proxyString: String, tor: Boolean = true) {
        val tokens = proxyString.split(":")
        val ip = tokens[0]
        val port = tokens[1]
        val proxyAddr = InetSocketAddress(ip, port.toInt())
        if (tor) {
            val proxyTor = Proxy(Proxy.Type.SOCKS, proxyAddr)
            client = OkHttpClient.Builder()
                    .proxy(proxyTor)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build()
            proxyEnabled = true
        }
    }

    fun buildQueryRL(network: String): String{
        if(network == "bitcoin"){
            return "api"
        }
        return "$network/api"
    }

    fun createRequest(url: String, type: String = "get", body: String = "",
                      mediaType: MediaType = "application/json; charset=utf-8".toMediaType(),
                      torVersion: Int = 3
    ): Request? {
        val baseUrl: String
        if (proxyEnabled) {
            if (torVersion == 3) {
                baseUrl = BASE_URL_TORV3
            } else {
                baseUrl = BASE_URL_TORV2
            }
        } else {
            baseUrl = BASE_URL
        }
        val completeUrl = "%s/%s".format(baseUrl, url)
        when (type) {
            "get" -> return buildGetRequest(completeUrl)
            "post" -> return buildPostRequest(completeUrl, body, mediaType)
        }
        return null
    }

    /**
     * This method is designed to retry the request 4 time and wait for each error 1 minute
     */
    @Throws(IOException::class)
    fun execRequest(plugin: CLightningPlugin, request: Request): ByteString {
        var response: Response = client.newCall(request).execute()
        var retryTime = 0
        var result: ByteString
        while (!isValid(response) && retryTime < 4) {
            result = response.body!!.byteString()
            response.close()
            plugin.log(PluginLog.DEBUG, "During http request to URL ${request.url}")
            plugin.log(PluginLog.DEBUG, "With error message: ${result.utf8()}")
            plugin.log(PluginLog.DEBUG, "retry time ${retryTime}")
            if(result.utf8().equals("Block not found", true)){
                //This is need because lightningd continue to require block until the backend respond with null value
                //This is one cases where the http failure is accepted
                return result
            }
            retryTime++
            Thread.sleep(WAIT_TIME)
            response = client.newCall(request).execute()
        }
        result = response.body!!.byteString()
        response.close()
        return result
    }

    private fun isValid(response: Response?): Boolean {
        return response != null && response.isSuccessful
    }

    private fun buildPostRequest(url: String, body: String, mediaType: MediaType): Request {
        val requestBody = RequestBody.create(mediaType, body)
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        return request
    }

    private fun buildGetRequest(url: String): Request {
        val request = Request.Builder()
                .url(url)
                .build()
        return request
    }
}