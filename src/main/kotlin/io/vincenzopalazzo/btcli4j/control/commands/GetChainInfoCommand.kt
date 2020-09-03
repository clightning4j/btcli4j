package io.vincenzopalazzo.btcli4j.control.commands

import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

//TODO here I inject the configuration inside the request json objetc and I can know all from the configuration
//This could be a JRPCLightning feature
class GetChainInfoCommand: ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"
        val reqGenesisBlock = HttpRequestFactory.createRequest("%s/block-height/0".format(network))
        val genesisBlock: String
        if(reqGenesisBlock != null){
            genesisBlock = HttpRequestFactory.execRequest(reqGenesisBlock).utf8()
            plugin.log(CLightningLevelLog.DEBUG, "Genesis block %s".format(genesisBlock))
        }else{
            plugin.log(CLightningLevelLog.WARNING, "Request for genesis block null!!!")
            return
        }

        val reqBlockchainHeight = HttpRequestFactory.createRequest("%s/blocks/tip/height".format(network))
        val blockCount: Int
        if(reqBlockchainHeight != null){
            blockCount = HttpRequestFactory.execRequest(reqBlockchainHeight).utf8().toInt()
            plugin.log(CLightningLevelLog.DEBUG, "Block count = %s".format(blockCount.toString()))
        }else{
            plugin.log(CLightningLevelLog.WARNING, "Request to Block count null!!!")
            return
        }

        //TODO add support to elements
        var chain = ""
        when(genesisBlock){
            "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f" -> chain = "main"
            "000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943" -> chain = "test"
            "0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206" -> chain = "main"
        }

        response.apply {
            add("chain", chain)
            add("headercount", blockCount)
            add("blockcount", blockCount)
            add("ibd", false)
        }

        plugin.log(CLightningLevelLog.DEBUG, response)
    }
}