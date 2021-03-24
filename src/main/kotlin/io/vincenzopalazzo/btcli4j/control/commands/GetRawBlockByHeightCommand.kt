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
package io.vincenzopalazzo.btcli4j.control.commands

import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.exceptions.CLightningPluginException
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okhttp3.MediaType.Companion.toMediaType

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetRawBlockByHeightCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val queryUrl = HttpRequestFactory.buildQueryRL(plugin.configs.network)
        val heightRequest = request["height"].asLong
        try {
            val blockWithHeight = HttpRequestFactory.createRequest(
                "%s/block-height/%s".format(queryUrl, heightRequest),
                mediaType = "text/plain".toMediaType()
            )!!
            // get block reference by height
            val resBlockHash = HttpRequestFactory.execRequest(plugin, blockWithHeight).utf8()
            plugin.log(PluginLog.DEBUG, "$blockWithHeight Hash $resBlockHash")
            val hexBlock: String
            if (resBlockHash.isNotEmpty() && resBlockHash != "Block not found") {
                // get the raw block by block hash
                val blockWithHash = HttpRequestFactory.createRequest(
                    "%s/block/%s/raw".format(queryUrl, resBlockHash),
                    mediaType = "text/plain".toMediaType()
                )!!
                blockWithHash.header("Content-Encoding: gzip")
                hexBlock = HttpRequestFactory.execRequest(plugin, blockWithHash).hex()
                if (hexBlock.length < 150) {
                    //150 is a random number there is any real motivation.
                    // sanity check, esplora return a wrong value as block hex;
                    // the problem is also described here
                    returnResponse(response)
                } else {
                    returnResponse(response, resBlockHash, hexBlock)
                }
            } else {
                // Lightningd continue to require bitcoin block and it know that the block is the last
                // only if it receive the object with null proprieties
                returnResponse(response)
            }
        } catch (ex: Exception) {
            plugin.log(PluginLog.WARNING, ex.message)
            throw CLightningPluginException(ex.cause)
        }
    }

    private fun returnResponse(response: CLightningJsonObject, blockHash: String? = null, blockHex: String? = null) {
        response.apply {
            add("blockhash", blockHash)
            add("block", blockHex)
        }
    }
}
