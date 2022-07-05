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
package io.vincenzopalazzo.btcli4j.control

import io.vincenzopalazzo.btcli4j.control.commands.esplora.EstimateFeeCommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.GetChainInfoCommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.GetRawBlockByHeightCommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.GetUtxOutCommand
import io.vincenzopalazzo.btcli4j.control.commands.ICommand
import io.vincenzopalazzo.btcli4j.control.commands.esplora.SendRawTransactionCommand
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import io.github.clightning4j.litebtc.LiteBitcoinRPC
import io.vincenzopalazzo.btcli4j.control.commands.btcprune.EstimateFeeBtc
import io.vincenzopalazzo.btcli4j.control.commands.btcprune.GetChainInfoBtc
import io.vincenzopalazzo.btcli4j.control.commands.btcprune.GetRawBlockByHeightBtc
import io.vincenzopalazzo.btcli4j.control.commands.btcprune.GetUtxOutBtc
import io.vincenzopalazzo.btcli4j.control.commands.btcprune.SendRawTransactionBtc
import io.vincenzopalazzo.btcli4j.util.PluginManager

public enum class Command(private val commandName: String) {
    GetChainInfoBtc("getchaininfo"),
    EstimateFeeBtc("estimatefees"),
    GetRawBlockByHeightBtc("getrawblockbyheight"),
    GetUtxOutBtc("getutxout"),
    SendRawTransactionBtc("sendrawtransaction");

    override fun toString(): String {
        return commandName
    }
}

/**
 * @author https://github.com/vincenzopalazzo
 */
object MediationMethod {

    private val commands: HashMap<String, ICommand> = HashMap()

    init {
        // The prune mode need to be overridden with a enum
        // because it is possible use the bitcoin RPC interface
        // to talk with the bitcoin node and also with all the othe rimplementation
        // that use the Bitcoin RPC 1.0 interface (e.g Litecoin)
        if (PluginManager.instance.prunedMode) {
            // adding prune command
            val bitcoinRPC = LiteBitcoinRPC(
                PluginManager.instance.bitcoinUser,
                PluginManager.instance.bitcoinPass,
                PluginManager.instance.baseBitcoinUrl
            )

            commands.apply {
                put(Command.GetChainInfoBtc.toString(), GetChainInfoBtc(bitcoinRPC))
                put(Command.EstimateFeeBtc.toString(), EstimateFeeBtc(bitcoinRPC))
                put(Command.GetRawBlockByHeightBtc.toString(), GetRawBlockByHeightBtc(bitcoinRPC))
                put(Command.GetUtxOutBtc.toString(), GetUtxOutBtc(bitcoinRPC))
                put(Command.SendRawTransactionBtc.toString(), SendRawTransactionBtc(bitcoinRPC))
            }
        } else {
            commands.apply {
                put(Command.GetChainInfoBtc.toString(), GetChainInfoCommand())
                put(Command.EstimateFeeBtc.toString(), EstimateFeeCommand())
                put(Command.GetRawBlockByHeightBtc.toString(), GetRawBlockByHeightCommand())
                put(Command.GetUtxOutBtc.toString(), GetUtxOutCommand())
                put(Command.SendRawTransactionBtc.toString(), SendRawTransactionCommand())
            }
        }
    }

    fun runCommand(key: String, plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        if (commands.containsKey(key)) {
            commands.getValue(key).run(plugin, request, response)
        }
    }
}
