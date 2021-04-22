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
import io.github.clightning4j.litebtc.exceptions.LiteBitcoinRPCException
import io.github.clightning4j.litebtc.model.generic.Parameters
import io.vincenzopalazzo.btcli4j.control.commands.ICommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.SendRawTransactionCommand
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class SendRawTransactionBtc(private val bitcoinRPC: LiteBitcoinRPC, private val alternative: SendRawTransactionCommand = SendRawTransactionCommand()) : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        try {
            // TODO support allowhighfees
            val txRaw = request["tx"].asString
            val params = Parameters("sendrawtransaction")
            params.addParameter("hexstring", txRaw)
            val transactionId = bitcoinRPC.makeBitcoinRequest(params, String::class.java)
            if (transactionId == null || transactionId.isEmpty()) {
                plugin.log(PluginLog.ERROR, "The transaction id has a bad format %s".format(transactionId))
                plugin.log(PluginLog.DEBUG, "Share message to esplora")
                alternative.run(plugin, request, response)
                return
            }
            response.apply {
                add("success", transactionId.isNotEmpty()) // TODO validate if it is a txId
                // add("errmsg", transactionId.isNotEmpty()) // in case of error I will share the content to esplora.
            }
        } catch (exception: LiteBitcoinRPCException) {
            plugin.log(PluginLog.ERROR, exception.stackTraceToString())
            plugin.log(PluginLog.DEBUG, "SendRawTransactionBtc: Share message to esplora")
            alternative.run(plugin, request, response)
        }
    }
}
