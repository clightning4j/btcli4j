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
package io.vincenzopalazzo.btcli4j.control.commands.esplora

import io.vincenzopalazzo.btcli4j.control.commands.ICommand
import io.vincenzopalazzo.btcli4j.model.esplora.EstimateFeeModel
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import io.vincenzopalazzo.btcli4j.util.JSONConverter
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.exceptions.CLightningPluginException
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class EstimateFeeCommand : ICommand {

    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val queryUrl = HttpRequestFactory.buildQueryRL(plugin.configs.network)

        try {
            val reqEstimateFee = HttpRequestFactory.createRequest("%s/fee-estimates".format(queryUrl))!!
            val estimateFee: EstimateFeeModel

            val resEstimateFee = HttpRequestFactory.execRequest(plugin, reqEstimateFee).utf8()
            if (resEstimateFee.isNotEmpty() && !reqEstimateFee.equals("{}")) {
                plugin.log(PluginLog.DEBUG, "EstimateFeeCommand: Estimate fee $resEstimateFee")
                estimateFee = JSONConverter.deserialize(resEstimateFee, EstimateFeeModel::class.java)
            } else {
                plugin.log(PluginLog.ERROR, "EstimateFeeCommand: Estimate fee empty response without object !!!")
                throw CLightningPluginException(400, "EstimateFeeCommand: Estimate fee empty from http response")
            }

            if (!estimateFee.isEmpty()) {
                response.apply {
                    add("opening", estimateFee.estimateFeeForNormalTarget().toInt())
                    add("mutual_close", estimateFee.estimateFeeRate().toInt())
                    add("unilateral_close", estimateFee.estimateFeeForUrgentTarget().toInt())
                    add("delayed_to_us", estimateFee.estimateFeeRate().toInt())
                    add("htlc_resolution", estimateFee.estimateFeeForNormalTarget().toInt())
                    add("penalty", estimateFee.getAverageEstimateFee().toInt())
                    add("min_acceptable", estimateFee.estimateFeeRate().toInt() / 2)
                    add("max_acceptable", estimateFee.estimateFeeForVeryUrgentTarget().toInt() * 10)
                }
            } else if (estimateFee.isEmpty()) {
                plugin.log(PluginLog.DEBUG, "Estimate fee empty")
                response.apply {
                    add("opening", null)
                    add("mutual_close", null)
                    add("unilateral_close", null)
                    add("delayed_to_us", null)
                    add("htlc_resolution", null)
                    add("penalty", null)
                    add("min_acceptable", null)
                    add("max_acceptable", null)
                }
            }
        } catch (ex: Exception) {
            plugin.log(PluginLog.ERROR, ex.localizedMessage)
            throw CLightningPluginException(400, ex.localizedMessage)
        }
    }
}
