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
package io.vincenzopalazzo.btcli4j.control.commands.btcprune

import io.github.clightning4j.litebtc.LiteBitcoinRPC
import io.github.clightning4j.litebtc.exceptions.BitcoinCoreException
import io.github.clightning4j.litebtc.exceptions.LiteBitcoinRPCException
import io.github.clightning4j.litebtc.model.generic.Parameters
import io.vincenzopalazzo.btcli4j.control.commands.ICommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.EstimateFeeCommand
import io.vincenzopalazzo.btcli4j.model.bitcoin.EstimateFeeBitcoin
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class EstimateFeeBtc(
    private val bitcoinRPC: LiteBitcoinRPC,
    private val alternative: EstimateFeeCommand = EstimateFeeCommand()
) : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        try {
            // TODO: try to use this transaction getmempoolinfo
            // read this issue https://github.com/ElementsProject/lightning/issues/4473#issue-853325816
            val params = Parameters("estimatesmartfee")
            params.addParameter("conf_target", 6)
            // params.addParameter("estimate_mode", "CONSERVATIVE")

            val estimateFee = bitcoinRPC.makeBitcoinRequest(params, EstimateFeeBitcoin::class.java)

            if (estimateFee.errors?.isNotEmpty() == true) {
                // TODO this can cause a loop? maybe
                plugin.log(PluginLog.ERROR, "Error during the fee estimation")
                for (error in estimateFee.errors!!) {
                    plugin.log(PluginLog.ERROR, "EstimateFeeBtc: %s".format(error))
                }
                this.returnNullFeee(response)
                return
            }

            estimateFee.convertBtcToSat()
            plugin.log(PluginLog.DEBUG, "EstimateFeeBtc: Estimate fee calculation from bitcoin core: %d".format(estimateFee.feeRate!!.toInt()))
            response.apply {
                add("opening", estimateFee.feeRate!!.toInt())
                add("mutual_close", estimateFee.feeRate!!.toInt())
                add("unilateral_close", estimateFee.feeRate!!.toInt())
                add("delayed_to_us", estimateFee.feeRate!!.toInt())
                add("htlc_resolution", estimateFee.feeRate!!.toInt())
                add("penalty", estimateFee.feeRate!!.toInt())
                add("min_acceptable", estimateFee.feeRate!!.toInt() / 2)
                add("max_acceptable", estimateFee.feeRate!!.toInt() * 10)
            }
        } catch (bitcoinEx: BitcoinCoreException) {
            plugin.log(PluginLog.ERROR, "EstimateFeeBtc: terminate bitcoin core with error: %s".format(bitcoinEx.message))
            this.returnNullFeee(response)
        } catch (ex: LiteBitcoinRPCException) {
            plugin.log(PluginLog.ERROR, ex.stackTraceToString())
            plugin.log(PluginLog.DEBUG, "EstimateFeeBtc: Share message to esplora")
            alternative.run(plugin, request, response)
        }
    }

    private fun returnNullFeee(response: CLightningJsonObject) {
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
}
