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

import io.vincenzopalazzo.btcli4j.model.EstimateFeeModel
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.exceptions.CLightningPluginException
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import java.io.IOException

/**
 * @author https://github.com/vincenzopalazzo
 */
class EstimateFeeCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network: String
        if(plugin.getParameter<String>("btcli4j-network") == "bitcoin"){
            network = "api"
        }else{
            network = "${plugin.getParameter<String>("btcli4j-network")}/api"
        }

        try {
            val reqEstimateFee = HttpRequestFactory.createRequest("%s/fee-estimates".format(network))!!
            var estimateFee: EstimateFeeModel? = null

            val resEstimateFee = HttpRequestFactory.execRequest(reqEstimateFee).utf8()
            if (resEstimateFee.isNotEmpty() && !reqEstimateFee.equals("{}")) {
                plugin.log(PluginLog.DEBUG, "Estimate fee $resEstimateFee")
                estimateFee = JSONConverter.deserialize(resEstimateFee, EstimateFeeModel::class.java)
            } else {
                plugin.log(PluginLog.ERROR, "Estimate fee empty response without object !!!")
                throw CLightningPluginException(400, "Estimate fee empty from http response")
            }

            if (estimateFee != null && !estimateFee.isEmpty()) {
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
                plugin.log(PluginLog.WARNING, response)
            } else if (estimateFee != null && estimateFee.isEmpty()) {
                plugin.log(PluginLog.ERROR, "Estimate fee empty")
                throw CLightningPluginException(400, "Estimate fee empty")
            }
        }catch (ex: IOException){
            plugin.log(PluginLog.ERROR, ex.localizedMessage)
            throw CLightningPluginException(400, ex.localizedMessage)
        }
    }

}