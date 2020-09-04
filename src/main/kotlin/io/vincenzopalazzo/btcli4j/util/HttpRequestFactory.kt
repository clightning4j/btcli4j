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

import jrpc.clightning.plugins.exceptions.CLightningPluginException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.ByteString
import okio.IOException
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
    private const val RETRY_TIME: Long = 60000

    private var proxyEnabled: Boolean = false
    private var client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    fun configureProxy(proxyString: String, tor: Boolean = true){
        val tokens = proxyString.split(":")
        val ip = tokens[0]
        val port = tokens[1]
        val proxyAddr = InetSocketAddress(ip, port.toInt())
        if(tor){
            val proxyTor = Proxy(Proxy.Type.SOCKS, proxyAddr)
            client = OkHttpClient.Builder()
                        .proxy(proxyTor)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
            proxyEnabled = true
        }
    }

    fun createRequest(url: String, type: String = "get", body: String = "",
                      mediaType: MediaType = "application/json; charset=utf-8".toMediaType(),
                      torVersion: Int = 3
    ): Request?{
        val baseUrl: String
        if(proxyEnabled){
            if(torVersion == 3){
                baseUrl = BASE_URL_TORV3
            }else{
                baseUrl = BASE_URL_TORV2
            }
        }else{
            baseUrl = BASE_URL
        }
        val completeUrl = "%s/%s".format(baseUrl, url)
        when(type){
            "get" -> return buildGetRequest(completeUrl)
            "post" -> return buildPostRequest(completeUrl, body, mediaType)
        }
        return null
    }

    /**
     * This method is designed to retry the request 4 time and wait for each error 1 minute
     */
    fun execRequest(request: Request): ByteString{
        var response: Response? = null
        var retryTime = 0
        val result: ByteString
        try {
            response = client.newCall(request).execute()
            while (!isValid(response) && retryTime < 4){
                retryTime++
                response?.body?.close()
                Thread.sleep(RETRY_TIME)
                response = client.newCall(request).execute()
            }
        }catch (ex: IOException){
            while (!isValid(response) && retryTime < 4){
                retryTime++
                response?.body?.close()
                Thread.sleep(RETRY_TIME)
                response = client.newCall(request).execute()
            }
            throw ex
        }finally {
            if(response != null && response.isSuccessful){
                result = response.body!!.byteString()
                response.close()
                return result
            }
            throw CLightningPluginException(400, "Request error: ${response?.message}")
        }
    }

    private fun isValid(response: Response?): Boolean{
        return response != null && (!response!!.isSuccessful || response!!.body!!.toString() != "{}")
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