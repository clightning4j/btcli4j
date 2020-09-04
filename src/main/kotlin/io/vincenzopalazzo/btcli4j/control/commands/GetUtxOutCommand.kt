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

import io.vincenzopalazzo.btcli4j.model.BTCTransactionModel
import io.vincenzopalazzo.btcli4j.model.StatusUTXOModel
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.exceptions.CLightningPluginException
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okio.IOException

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetUtxOutCommand : ICommand {

    private lateinit var network: String

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network: String
        if(plugin.getParameter<String>("btcli4j-network") == "bitcoin"){
            network = "api"
        }else{
            network = "${plugin.getParameter<String>("btcli4j-network")}/api"
        }
        val txId = request["txid"].asString
        plugin.log(PluginLog.DEBUG, "TxId: $txId")
        val vOut = request["vout"].asInt
        plugin.log(PluginLog.DEBUG, "Vout: $vOut")
        try {

            if (getUTXOInformation(plugin, txId, vOut, response)) {
                //The transaction wasn't spent!!
                val reqTxInformation = HttpRequestFactory.createRequest("%s/tx/%s".format(network, txId))!!
                val resTxInformation = HttpRequestFactory.execRequest(reqTxInformation).utf8()
                if (resTxInformation.isNotEmpty() && resTxInformation !== "{}") {
                    val transactionInformation = JSONConverter.deserialize<BTCTransactionModel>(resTxInformation, BTCTransactionModel::class.java)
                    val transactionOutput = transactionInformation.transactionsOutput?.get(vOut)!!
                    response.apply {
                        add("amount", transactionOutput.value)
                        add("script", transactionOutput.scriptPubKey!!)
                    }
                }
            }
        } catch (ex: IOException) {
            plugin.log(PluginLog.WARNING, ex.localizedMessage)
            throw CLightningPluginException(400, ex.localizedMessage)
        }
    }

    /**
     * Function to verify if transaction have a status valid, if it is spent I will return false and the transaction will be not verify.
     * On the other hand, if it is not spent I return true to continue and get the transaction information!
     */
    private fun getUTXOInformation(plugin: CLightningPlugin, txId: String, vout: Int, response: CLightningJsonObject): Boolean {
        val requestUtxo = HttpRequestFactory.createRequest("%s/tx/%s/outspend/%s".format(network, txId, vout))!!
        val resUtxo = HttpRequestFactory.execRequest(requestUtxo).utf8()
        if (resUtxo.isNotEmpty() && resUtxo !== "{}") {
            val statusUtxo = JSONConverter.deserialize<StatusUTXOModel>(resUtxo, StatusUTXOModel::class.java)
            /* As of at least v0.15.1.0, bitcoind returns "success" but an empty
            string on a spent txout. */
            if (statusUtxo.spend) {
                response.apply {
                    add("amount", null)
                    add("script", null)
                }
                return false
            }
        }
        return true //continue
    }

}