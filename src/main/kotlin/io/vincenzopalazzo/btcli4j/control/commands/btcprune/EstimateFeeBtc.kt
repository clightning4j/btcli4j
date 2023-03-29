/**
 *  C-lightning plugin to override Bitcoin backend plugin.
 *  Copyright (C) 2020-2021 Vincenzo Palazzo vincenzopalazzodev@gmail.com
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
import io.vincenzopalazzo.btcli4j.util.PluginManager
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

class EstimateFeeParams(val lnmode: String, val target: Int, val mode: String)

class FeeManager {
    private val feesEstimation: HashMap<String, EstimateFeeBitcoin> = HashMap()
    private val params = listOf(
        EstimateFeeParams("FEERATE_HIGHEST", 2, "CONSERVATIVE"),
        EstimateFeeParams("FEERATE_URGENT", 6, "ECONOMICAL"),
        EstimateFeeParams("FEERATE_NORMAL", 12, "ECONOMICAL"),
        EstimateFeeParams("FEERATE_SLOW", 100, "ECONOMICAL")
    )

    private fun estimateFee(rpc: LiteBitcoinRPC, param: EstimateFeeParams): EstimateFeeBitcoin {
        // TODO: try to use this transaction getmempoolinfo
        // read this issue https://github.com/ElementsProject/lightning/issues/4473#issue-853325816
        val params = Parameters("estimatesmartfee")
        params.addParameter("conf_target", param.target)
        params.addParameter("estimate_mode", param.mode)
        return rpc.makeBitcoinRequest(params, EstimateFeeBitcoin::class.java)
    }

    fun estimate(rpc: LiteBitcoinRPC, plugin: CLightningPlugin): Boolean {
        params.forEach {
            val estimateFee = this.estimateFee(rpc, it)
            if (estimateFee.errors?.isNotEmpty() == true) {
                plugin.log(PluginLog.ERROR, "Error during the fee estimation")
                for (error in estimateFee.errors!!) {
                    plugin.log(PluginLog.ERROR, "EstimateFeeBtc: %s".format(error))
                }
                return false
            }
            estimateFee.convertBtcToSat()
            plugin.log(
                PluginLog.DEBUG,
                "EstimateFeeBtc: Estimate fee (target: %d mode: %s) calculation from bitcoin core: %d".format(
                    it.target,
                    it.target,
                    estimateFee.feeRate!!.toInt()
                )
            )
            feesEstimation[it.lnmode] = estimateFee
        }
        return true
    }

    fun urgentFee(): EstimateFeeBitcoin {
        return feesEstimation["FEERATE_URGENT"]!!
    }

    fun highestFee(): EstimateFeeBitcoin {
        return feesEstimation["FEERATE_HIGHEST"]!!
    }

    fun normalFee(): EstimateFeeBitcoin {
        return feesEstimation["FEERATE_NORMAL"]!!
    }

    fun slowFee(): EstimateFeeBitcoin {
        return feesEstimation["FEERATE_SLOW"]!!
    }
}

/**
 * @author https://github.com/vincenzopalazzo
 */
class EstimateFeeBtc(
    private val bitcoinRPC: LiteBitcoinRPC,
    private val alternative: EstimateFeeCommand = EstimateFeeCommand()
) : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        if (PluginManager.instance.isDownloading) {
            plugin.log(PluginLog.DEBUG, "EstimateFeeBtc: Share message to esplora")
            alternative.run(plugin, request, response)
            return
        }
        try {
            val estimator = FeeManager()
            if (!estimator.estimate(bitcoinRPC, plugin)) {
                // FIXME this can cause a loop? maybe
                this.returnNullFee(response)
                return
            }

            response.apply {
                add("opening", estimator.normalFee().feeRate!!.toInt())
                add("mutual_close", estimator.slowFee().feeRate!!.toInt())
                add("unilateral_close", estimator.urgentFee().feeRate!!.toInt())
                add("delayed_to_us", estimator.normalFee().feeRate!!.toInt())
                add("htlc_resolution", estimator.urgentFee().feeRate!!.toInt())
                add("penalty", estimator.normalFee().feeRate!!.toInt())
                add("min_acceptable", estimator.slowFee().feeRate!!.toInt() / 2)
                add("max_acceptable", estimator.highestFee().feeRate!!.toInt() * 10)
            }
        } catch (bitcoinEx: BitcoinCoreException) {
            plugin.log(PluginLog.ERROR, "EstimateFeeBtc: terminate bitcoin core with error: %s".format(bitcoinEx.message))
            this.returnNullFee(response)
        } catch (ex: LiteBitcoinRPCException) {
            plugin.log(PluginLog.ERROR, ex.stackTraceToString())
            plugin.log(PluginLog.DEBUG, "EstimateFeeBtc: Share message to esplora")
            alternative.run(plugin, request, response)
        }
    }

    private fun returnNullFee(response: CLightningJsonObject) {
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
