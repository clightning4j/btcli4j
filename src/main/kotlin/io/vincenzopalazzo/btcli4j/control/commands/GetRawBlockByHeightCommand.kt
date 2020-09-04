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
import okio.IOException

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetRawBlockByHeightCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"
        val heightRequest = request["height"].asLong
        try {

            val blockWithHeight = HttpRequestFactory.createRequest("%s/block-height/%s".format(network, heightRequest))!!

            val resBlockHash = HttpRequestFactory.execRequest(blockWithHeight).utf8()

            if (resBlockHash.contains("Block not found")) {
                //Lightningd continue to require bitcoin block and it know that the block is the last
                //only if it receive the object with null proprieties
                response.apply {
                    add("blockhash", null)
                    add("block", null)
                }
                return
            }

            plugin.log(PluginLog.DEBUG, "$blockWithHeight Hash $resBlockHash")
            var hexBlock: String? = null
            if (resBlockHash.isNotEmpty()) {
                val blockWithHash = HttpRequestFactory.createRequest("%s/block/%s/raw".format(network, resBlockHash))!!
                hexBlock = HttpRequestFactory.execRequest(blockWithHash).hex()
            }

            response.apply {
                add("blockhash", resBlockHash)
                add("block", hexBlock)
            }
        } catch (ex: IOException) {
            plugin.log(PluginLog.WARNING, ex.localizedMessage)
            throw CLightningPluginException(400, ex.localizedMessage)
        }
    }
}