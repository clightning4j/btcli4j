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
package io.vincenzopalazzo.btcli4j

import io.vincenzopalazzo.btcli4j.control.MediationMethod
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.annotation.PluginOption
import jrpc.clightning.annotation.RPCMethod
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class Plugin: CLightningPlugin(){

    @PluginOption(
            name = "btcli4j-network",
            description = "This option give information on the network",
            defValue = "testnet",
            typeValue = "string"
    )
    private var network: String = "testnet"

    @PluginOption(
            name = "btcli4j-proxy",
            description = "This option give information on proxy enabled, by default set on tor proxy",
            defValue = "127.0.0.1:9050",
            typeValue = "string"
    )
    private var proxy: String = "127.0.0.1:9050"

    @PluginOption(
            name = "btcli4j-proxy-enable",
            description = "This option give enable the proxy inside plugin. By default is true",
            defValue = "true",
            typeValue = "flag"
    )
    private var proxyEnable: Boolean = true

    private val pluginInit = false

    @RPCMethod(name = "getchaininfo", description = "getchaininfo to fetch the data from blockstream.info")
    fun getChainInfo(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        this.configurePluginInit(plugin)
        MediationMethod.runCommand("getchaininfo", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "estimatefees", description = "estimatefees to fetch the feed estimation from blockstream.info")
    fun estimateFees(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("estimatefees", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "getrawblockbyheight", description = "getrawblockbyheight to fetch actual blockchain height from blockstream.info")
    fun getRawBlockByHeight(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("getrawblockbyheight", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "getutxout", description = "getutxout to fetch a utx with {txid} and {vout} from blockstream.info")
    fun getUtxOut(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("getutxout", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "sendrawtransaction", description = "sendrawtransaction to publish a new transaction with blockstream.info")
    fun sendRawTransaction(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject){
        MediationMethod.runCommand("sendrawtransaction", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    private fun configurePluginInit(plugin: CLightningPlugin){
        if(!pluginInit){
            this.network = plugin.getParameter("btcli4j-network")
            this.proxy = plugin.getParameter("btcli4j-proxy")
            this.proxyEnable = plugin.getParameter("btcli4j-proxy-enable") ?: false
            log(PluginLog.WARNING, "proxy enable: $proxyEnable")
            if(proxyEnable){
                HttpRequestFactory.configureProxy(this.proxy, true)
                log(PluginLog.INFO, "Tor proxy enabled on btcli4j")
            }
        }
    }
}