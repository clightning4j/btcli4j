package io.vincenzopalazzo.btcli4j.control.commands

import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okio.ByteString.Companion.toByteString


class GetRawBlockByHeightCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"
        val heightRequest = request["params"].asJsonObject["height"]
        plugin.log(CLightningLevelLog.DEBUG, request)

        val blockWithHeight = HttpRequestFactory.createRequest("%s/block-height/%s".format(network, heightRequest))!!

        val res = HttpRequestFactory.execRequest(blockWithHeight)
        val resBlockHash = res.toByteString().utf8()
        if(resBlockHash.contains("Block not found")){
            response.apply {
                add("blockhash", null)
                add("block", null)
            }
            return
        }
        plugin.log(CLightningLevelLog.DEBUG, "$blockWithHeight Hash $resBlockHash")
        var hexBlock: String? = null
        if (resBlockHash.isNotEmpty()) {
            val blockWithHash = HttpRequestFactory.createRequest("%s/block/%s/raw".format(network, resBlockHash))!!
            val bytes = HttpRequestFactory.execRequest(blockWithHash)
            hexBlock = bytes.toByteString().hex()
        }

        response.apply {
            add("blockhash", resBlockHash)
            add("block", hexBlock)
        }
    }
}