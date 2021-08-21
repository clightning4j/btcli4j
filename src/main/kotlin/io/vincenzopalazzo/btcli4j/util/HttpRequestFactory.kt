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

import io.vincenzopalazzo.btcli4j.control.checkchain.ChainOfResponsibilityCheck
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.ByteString

/**
 * @author https://github.com/vincenzopalazzo
 */
object HttpRequestFactory {

    private lateinit var optionsManager: OptionsManager
    private var retryTime = 4
    private var checkChains = ChainOfResponsibilityCheck()
    private lateinit var baseUrl: String
    private var client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    fun initHttpClient(optionsManager: OptionsManager) {
        this.optionsManager = optionsManager
        if (optionsManager.proxyEnabled) {
            this.configureProxy(optionsManager.proxyUrl, optionsManager.torVersion != null)
        }
        baseUrl = optionsManager.getEndPointUrl()
    }

    private fun configureProxy(proxyString: String, tor: Boolean = true) {
        val tokens = proxyString.split(":")
        val ip = tokens[0]
        val port = tokens[1]
        val proxyAddr = InetSocketAddress(ip, port.toInt())
        if (tor) {
            val proxyTor = Proxy(Proxy.Type.SOCKS, proxyAddr)
            client = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .proxy(proxyTor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build()
        }
    }

    fun buildQueryRL(network: String): String {
        if (network == "bitcoin") {
            return "api"
        }
        return "$network/api"
    }

    fun createRequest(
        url: String,
        type: String = "get",
        body: String = "",
        mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
    ): Request? {
        val completeUrl = "%s/%s".format(baseUrl, url)
        when (type) {
            "get" -> return buildGetRequest(completeUrl)
            "post" -> return buildPostRequest(completeUrl, body, mediaType)
        }
        return null
    }

    /**
     * This method is designed to retry the request 4 time and wait an exponential time
     * this, the wait time is set to 1 minutes by default and the wait time is exponential,
     * So this mean that the wait time is set to
     */
    @Throws(Exception::class)
    fun execRequest(plugin: CLightningPlugin, request: Request): ByteString {
        for (time in 0..retryTime) {
            try {
                val response = makeRequest(request)
                if (isValid(response)) {
                    val result = response.body!!.byteString()
                    plugin.log(PluginLog.DEBUG, "Request to ${request.url} with the following result $result")
                    return result
                }
                // there are some cases where the invalid answer can be accepted from lightnind
                val result = response.body!!.byteString()
                val checkResult = checkChains.check(plugin, result)
                // This is a error with the a 200 code
                // this can happen often with the client-server communication
                if (checkResult.result!!.utf8() == "Check fails") {
                    plugin.log(PluginLog.DEBUG, "An wrong status happen in retry time $time")
                    plugin.log(PluginLog.WARNING, "Response invalid, all the plugin check on the request failed")
                    waitingToRetry(plugin, retryTime)
                    continue
                }
                return result
            } catch (ex: Exception) {
                plugin.log(PluginLog.DEBUG, "An wrong status happen in retry time $time")
                plugin.log(PluginLog.ERROR, "Error during the request method %s".format(ex.localizedMessage))
                waitingToRetry(plugin, retryTime)
            }
        }
        // never found a valid answer and I'm exit with an error.
        plugin.log(PluginLog.ERROR, "After all retry time ($retryTime) all request failed")
        throw Exception("Error generate from Bitcoin backend more than 4 time")
    }

    private fun waitingToRetry(plugin: CLightningPlugin, retryTime: Int) {
        val exponentialRetryTime = optionsManager.waitingTime * (retryTime + 1)
        plugin.log(
            PluginLog.WARNING,
            "Error occurs %d time: and the waiting time is set to %d".format(retryTime, exponentialRetryTime)
        )
        Thread.sleep(exponentialRetryTime.toLong())
    }

    private fun makeRequest(request: Request): Response {
        return client.newCall(request).execute()
    }

    private fun isValid(response: Response?): Boolean {
        return response != null && response.isSuccessful
    }

    private fun buildPostRequest(url: String, body: String, mediaType: MediaType): Request {
        val requestBody = body.toRequestBody(mediaType)
        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }

    private fun buildGetRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .build()
    }
}
