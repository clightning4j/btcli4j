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
import io.github.clightning4j.litebtc.exceptions.LiteBitcoinRPCException
import io.vincenzopalazzo.btcli4j.control.commands.ICommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.GetChainInfoCommand
import io.vincenzopalazzo.btcli4j.model.bitcoin.BlockchainInfoBitcoin
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.exceptions.CLightningPluginException
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetChainInfoBtc(
    private val bitcoinRPC: LiteBitcoinRPC,
    private val alternative: GetChainInfoCommand = GetChainInfoCommand()
) : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        try {
            var network = plugin.configs.network
            network = convertNameToBitcoinCore(network)
            val blockchainInfo = bitcoinRPC.makeBitcoinRequest("getblockchaininfo", BlockchainInfoBitcoin::class.java)
            plugin.log(PluginLog.ERROR, blockchainInfo.chain!!)
            if (blockchainInfo.chain!! != network) {
                throw CLightningPluginException(400, "Bitcoin Core and C-lightning are running over different chain.")
            }

            if (blockchainInfo.isDownloading!!) {
                alternative.run(plugin, request, response)
                return
            }
            response.apply {
                add("chain", blockchainInfo.chain!!)
                add("headercount", blockchainInfo.headerCount!!)
                add("blockcount", blockchainInfo.blockCount!!)
                add("ibd", blockchainInfo.isDownloading!!)
            }
        } catch (exception: CLightningPluginException) {
            plugin.log(PluginLog.ERROR, "Wrong chain")
            throw exception
        } catch (exception: LiteBitcoinRPCException) {
            plugin.log(PluginLog.ERROR, exception.stackTraceToString())
            alternative.run(plugin, request, response)
        }
    }

    private fun convertNameToBitcoinCore(chainName: String): String {
        when (chainName) {
            "bitcoin" -> return "main"
            "testnet" -> return "testnet"
            "regtest" -> return "regtest"
        }
        throw CLightningPluginException(400, "Unknown name chain %s".format(chainName))
    }
}
