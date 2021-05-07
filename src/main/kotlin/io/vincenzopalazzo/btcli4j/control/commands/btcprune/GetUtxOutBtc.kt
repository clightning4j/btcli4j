/**
 *  C-lightning plugin to override Bitcoin backend plugin.
 *  Copyright (C) 2020-2021 Vincenzo Palazzo vincenzopalazzodev@gmail.com
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
import io.vincenzopalazzo.btcli4j.control.commands.esplora.GetUtxOutCommand
import io.vincenzopalazzo.btcli4j.model.bitcoin.UTXOBitcoin
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import io.vincenzopalazzo.btcli4j.util.PluginManager
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetUtxOutBtc(private val bitcoinRPC: LiteBitcoinRPC, private val alternative: GetUtxOutCommand = GetUtxOutCommand()) : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        if (PluginManager.instance.isDownloading) {
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: Share message to esplora")
            alternative.run(plugin, request, response)
            return
        }
        try {
            val txId = request["txid"].asString
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: TxId -> $txId")
            val vOut = request["vout"].asInt
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: Vout -> $vOut")
            val params = Parameters("gettxout")
            params.addParameter("txid", txId)
            params.addParameter("n", vOut)
            // params.addParameter("include_mempool", true) TODO: double check
            val getUtxo: UTXOBitcoin? = bitcoinRPC.makeBitcoinRequest(params, UTXOBitcoin::class.java)
            plugin.log(
                PluginLog.DEBUG,
                "GetUtxOutBtc: Bitcoin getutxo response is %s".format(
                    JSONConverter.serialize(
                        getUtxo ?: "Is null"
                    )
                )
            )
            // TODO: bitcoin core should return an error?
            if (getUtxo?.amount == null || getUtxo.script == null || getUtxo.script.hex == null || getUtxo.script.hex!!.isEmpty()) {
                plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: Received undefined response, return a null response")
                response.apply {
                    add("amount", null)
                    add("script", null)
                }
                return
            }
            getUtxo.convertBtcToSat()
            // Check if the data are valid, otherwise put the message to esplora
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: answer from gettxout: " + JSONConverter.serialize(getUtxo))
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: Amount tx (not conv to integer): " + getUtxo.amount!!)
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: Script hex: %s".format(getUtxo.script!!.hex!!))
            response.apply {
                add("amount", getUtxo.amount!!.toInt())
                add("script", getUtxo.script.hex!!)
            }
        } catch (bitcoinEx: BitcoinCoreException) {
            plugin.log(PluginLog.ERROR, "GetUtxOutBtc: terminate bitcoin core with error: %s".format(bitcoinEx.message))
            response.apply {
                add("amount", null)
                add("script", null)
            }
        } catch (exception: LiteBitcoinRPCException) {
            plugin.log(PluginLog.ERROR, exception.stackTraceToString())
            plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: Share message to esplora")
            alternative.run(plugin, request, response)
        }
        plugin.log(PluginLog.DEBUG, "GetUtxOutBtc: answer is: " + JSONConverter.serialize(response))
    }
}
