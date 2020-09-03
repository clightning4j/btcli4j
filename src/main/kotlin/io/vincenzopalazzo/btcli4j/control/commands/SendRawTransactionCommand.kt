package io.vincenzopalazzo.btcli4j.control.commands

import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okhttp3.MediaType.Companion.toMediaType

class SendRawTransactionCommand : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"
        val txRaw = request["tx"].asString
        plugin.log(CLightningLevelLog.DEBUG, "Raw tx: $txRaw")

        val reqSendTx = HttpRequestFactory.createRequest("%s/tx", type = "post", body = txRaw,
                mediaType = "plain/text; charset=utf-8".toMediaType())!!
        plugin.log(CLightningLevelLog.DEBUG, reqSendTx)

        val resSendTx = HttpRequestFactory.execRequest(reqSendTx).utf8()
        plugin.log(CLightningLevelLog.DEBUG, "Result send tx $resSendTx")

        response.apply {
            add("success", resSendTx.isNotEmpty()) // TODO validate if it is a txId
            add("errmsg", resSendTx.isNotEmpty() ?: "Invalid tx") // check this code
        }
    }

}