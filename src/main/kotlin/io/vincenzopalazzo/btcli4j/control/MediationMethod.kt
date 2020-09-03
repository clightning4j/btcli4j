package io.vincenzopalazzo.btcli4j.control

import io.vincenzopalazzo.btcli4j.control.commands.*
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okhttp3.Request

object MediationMethod {

    private val commands: HashMap<String, ICommand> = HashMap()

    init {
        commands.apply {
            put("getchaininfo", GetChainInfoCommand())
            put("estimatefees", EstimateFeeCommand())
            put("getrawblockbyheight", GetRawBlockByHeightCommand())
            put("getutxout", GetUtxOutCommand())
            put("sendrawtransaction", SendRawTransactionCommand())
        }
    }

    fun runCommand(key: String, plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        if(commands.containsKey(key)){
            plugin.log(CLightningLevelLog.DEBUG, "Method $key found")
            commands.getValue(key).run(plugin, request, response)
        }
    }
}