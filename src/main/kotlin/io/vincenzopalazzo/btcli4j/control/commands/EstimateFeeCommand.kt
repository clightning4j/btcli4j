package io.vincenzopalazzo.btcli4j.control.commands

import com.google.gson.JsonObject
import io.vincenzopalazzo.btcli4j.model.EstimateFeeModel
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class EstimateFeeCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"

        val reqEstimateFee = HttpRequestFactory.createRequest("%s/fee-estimates".format(network))!!
        var estimateFee: EstimateFeeModel? = null

        val resEstimateFee = HttpRequestFactory.execRequest(reqEstimateFee)
        val result = String(resEstimateFee, StandardCharsets.UTF_8)
        if (resEstimateFee.isNotEmpty()) {
            plugin.log(CLightningLevelLog.DEBUG, "Estimate fee $resEstimateFee")
            estimateFee = JSONConverter.deserialize(result, EstimateFeeModel::class.java)
        } else {
            plugin.log(CLightningLevelLog.DEBUG, "Estimate fee empty response without object !!!")
        }

        if (estimateFee != null && !estimateFee.isEmpty()) {
            plugin.log(CLightningLevelLog.DEBUG, JSONConverter.serialize(estimateFee))
            response.apply {
                add("opening", estimateFee.twoConfirmation)
                add("mutual_close", estimateFee.twoConfirmation)
                add("unilateral_close", estimateFee.twoConfirmation)
                add("delayed_to_us", estimateFee.twoConfirmation)
                add("htlc_resolution", estimateFee.twoConfirmation)
                add("penalty", estimateFee.twoConfirmation)
                add("min_acceptable", estimateFee.twoConfirmation / 2)
                add("max_acceptable", estimateFee.oneConfirmation * 10)
            }
            plugin.log(CLightningLevelLog.DEBUG, response)
        } else if (estimateFee != null && estimateFee.isEmpty()) {
            plugin.log(CLightningLevelLog.DEBUG, "Estimate fee empty, I should be retry the connection")
            //TODO check if the connection is failed
        }

    }

}