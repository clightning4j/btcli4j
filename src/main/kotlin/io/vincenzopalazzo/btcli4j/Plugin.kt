package io.vincenzopalazzo.btcli4j

import io.vincenzopalazzo.btcli4j.control.MediationMethod
import jrpc.clightning.annotation.RPCMethod
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

class Plugin: CLightningPlugin(){

    @RPCMethod(name = "getchaininfo", description = "getchaininfo to fecth the data from blockstream.info")
    fun getChainInfo(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("getchaininfo", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "estimatefees", description = "estimatefees to fect the feed stimation from blockstream.info")
    fun estimateFees(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("estimatefees", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "getrawblockbyheight", description = "")
    fun getRawBlockByHeight(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("getrawblockbyheight", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "getutxout", description = "")
    fun getUtxOut(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("getutxout", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "sendrawtransaction", description = "")
    fun sendRawTransaction(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("sendrawtransaction", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

}