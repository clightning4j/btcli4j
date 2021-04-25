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
package io.vincenzopalazzo.btcli4j.control.commands.btcprune

import io.github.clightning4j.litebtc.LiteBitcoinRPC
import io.github.clightning4j.litebtc.exceptions.BitcoinCoreException
import io.github.clightning4j.litebtc.exceptions.LiteBitcoinRPCException
import io.github.clightning4j.litebtc.model.generic.Parameters
import io.vincenzopalazzo.btcli4j.control.commands.ICommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.GetRawBlockByHeightCommand
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetRawBlockByHeightBtc(
    private val bitcoinRPC: LiteBitcoinRPC,
    private val alternative: GetRawBlockByHeightCommand = GetRawBlockByHeightCommand()
) : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        try {
            val hashBlock = bitcoinRPC.makeBitcoinRequest("getblockhash", String::class.java)
            if (hashBlock == null || hashBlock.isEmpty()) {
                plugin.log(PluginLog.ERROR, "GetRawBlockByHeightBtc: Bad getblockhash result %s".format(hashBlock))
                alternative.run(plugin, request, response)
                return
            }
            val params = Parameters("blockhash")
            params.addParameter("verbose", 0)
            val hexBlock = bitcoinRPC.makeBitcoinRequest(params, String::class.java)
            if (hexBlock == null || hexBlock.isEmpty()) {
                plugin.log(PluginLog.ERROR, "GetRawBlockByHeightBtc: Bad blockhash result %s".format(hashBlock))
                alternative.run(plugin, request, response)
                return
            }
            response.apply {
                add("blockhash", hashBlock)
                add("block", hexBlock)
            }
        } catch (bitcoinEx: BitcoinCoreException) {
            // TODO: this should be not throws, because Bitcoin core return an exception with all the propriety null, also the error.
            plugin.log(PluginLog.ERROR, "GetRawBlockByHeightBtc: terminate bitcoin core with error: %s".format(bitcoinEx.message))
            response.apply {
                add("blockhash", null)
                add("block", null)
            }
        } catch (exception: LiteBitcoinRPCException) {
            plugin.log(PluginLog.ERROR, exception.stackTraceToString())
            plugin.log(PluginLog.DEBUG, "GetRawBlockByHeightBtc: Share message to esplora")
            alternative.run(plugin, request, response)
        }
    }
}
