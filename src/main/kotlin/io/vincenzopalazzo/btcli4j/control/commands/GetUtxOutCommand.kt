package io.vincenzopalazzo.btcli4j.control.commands

import io.vincenzopalazzo.btcli4j.model.BTCTransactionModel
import io.vincenzopalazzo.btcli4j.model.StatusUTXOModel
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

class GetUtxOutCommand: ICommand{

    private lateinit var network: String

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        network = "testnet/api"
        val txId = request["txid"].asString
        plugin.log(CLightningLevelLog.DEBUG, "TxId: $txId")
        val vOut = request["vout"].asInt
        plugin.log(CLightningLevelLog.DEBUG, "Vout: $vOut")

        if(getUTXOInformation(plugin, txId, vOut, response)){
            //The transaction wasn't spent!!
            val reqTxInformation = HttpRequestFactory.createRequest("%s/tx/%s".format(network, txId))!!
            val resTxInformation = HttpRequestFactory.execRequest(reqTxInformation).utf8()
            if(resTxInformation.isNotEmpty() && resTxInformation !== "{}"){
                val transactionInformation = JSONConverter.deserialize<BTCTransactionModel>(resTxInformation, BTCTransactionModel::class.java)
                val transactionOutput = transactionInformation.transactionsOutput?.get(vOut)!!
                response.apply {
                    add("amount", transactionOutput.value)
                    add("script", transactionOutput.scriptPubKey!!)
                }
                plugin.log(CLightningLevelLog.DEBUG, response)
            }
        }
    }

    /**
     * Function to verify if transaction have a status valid, if it is spent I will return false and the transaction will be not verify.
     * On the other hand, if it is not spent I return true to continue and get the transaction information!
     */
    private fun getUTXOInformation(plugin: CLightningPlugin, txId: String, vout: Int, response: CLightningJsonObject): Boolean{
        val requestUtxo = HttpRequestFactory.createRequest("%s/tx/%s/outspend/%s".format(network, txId, vout))!!
        plugin.log(CLightningLevelLog.DEBUG, "${this.javaClass.simpleName} request ${requestUtxo.url}")
        val resUtxo = HttpRequestFactory.execRequest(requestUtxo).utf8()
        if(resUtxo.isNotEmpty() && resUtxo !== "{}"){
            val statusUtxo = JSONConverter.deserialize<StatusUTXOModel>(resUtxo, StatusUTXOModel::class.java)
            plugin.log(CLightningLevelLog.DEBUG, "Status UTXO received ${resUtxo}")
            /* As of at least v0.15.1.0, bitcoind returns "success" but an empty
            string on a spent txout. */
            if(statusUtxo.spend){
                response.apply {
                    add("amount", null)
                    add("script", null)
                }
                plugin.log(CLightningLevelLog.DEBUG, response)
                return false
            }
        }
        return true //continue
    }

}