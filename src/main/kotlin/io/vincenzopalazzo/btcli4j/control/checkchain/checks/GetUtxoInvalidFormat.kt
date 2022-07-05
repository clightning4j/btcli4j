package io.vincenzopalazzo.btcli4j.control.checkchain.checks

import com.google.gson.JsonSyntaxException
import io.vincenzopalazzo.btcli4j.control.checkchain.CheckResult
import io.vincenzopalazzo.btcli4j.model.esplora.BTCTransactionModel
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import okio.ByteString

class GetUtxoInvalidFormat : CheckHandler {
    override fun check(plugin: CLightningPlugin, response: ByteString): CheckResult {
        // Issue: https://github.com/clightning4j/btcli4j/issues/59
        // this is a monkey check
        try {
            if (response.utf8().isNotEmpty() && !JSONConverter.isJSONNull(response.utf8())) {
                // FIXME: The check result can be generic, and we can return the type of the decoding
                JSONConverter.deserialize<BTCTransactionModel>(
                    response.utf8(),
                    BTCTransactionModel::class.java
                )
                return CheckResult(next = true, response)
            }
        } catch (jsonException: JsonSyntaxException) {
            plugin.log(PluginLog.ERROR, "We receive a 200 code from the server, but the reponse looks invalid")
            plugin.log(PluginLog.ERROR, jsonException.message)
        }
        return CheckResult(next = false)
    }
}
