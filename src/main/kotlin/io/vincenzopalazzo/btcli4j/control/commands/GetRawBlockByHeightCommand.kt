package io.vincenzopalazzo.btcli4j.control.commands

import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject


class GetRawBlockByHeightCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"
        val heightRequest = request["height"].asLong
        plugin.log(CLightningLevelLog.DEBUG, request)

        val blockWithHeight = HttpRequestFactory.createRequest("%s/block-height/%s".format(network, heightRequest))!!

        val resBlockHash = HttpRequestFactory.execRequest(blockWithHeight).utf8()

        if(resBlockHash.contains("Block not found")){
            //Lightningd continue to require bitcoin block and it know that the block is the last
            //only if it receive the object with null proprieties
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
            hexBlock = HttpRequestFactory.execRequest(blockWithHash).hex()
        }

        response.apply {
            add("blockhash", resBlockHash)
            add("block", hexBlock)
        }
    }
}