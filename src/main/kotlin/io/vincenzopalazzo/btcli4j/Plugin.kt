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
package io.vincenzopalazzo.btcli4j

import io.vincenzopalazzo.btcli4j.control.MediationMethod
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import io.vincenzopalazzo.btcli4j.util.PluginManager
import jrpc.clightning.annotation.PluginOption
import jrpc.clightning.annotation.RPCMethod
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

/**
 * @author https://github.com/vincenzopalazzo
 */
class Plugin : CLightningPlugin() {

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

    @PluginOption(
        name = "btcli4j-endpoint",
        description = "If btcli4j-endpoint is specified the blockstream endpoint will be override with the custom endpoint",
        defValue = "",
        typeValue = "string"
    )
    private var personalEndPoint: String = ""

    @PluginOption(
        name = "bitcoin-rpcpassword",
        description = "Bitcoin RPC password",
        defValue = "",
        typeValue = "string"
    )
    private var bitcoinRpcPass: String = ""

    @PluginOption(
        name = "bitcoin-rpcuser",
        description = "Bitcoin RPC user",
        defValue = "",
        typeValue = "string"
    )
    private var bitcoinRpcUser: String = ""

    @PluginOption(
        name = "bitcoin-rpcurl",
        description = "Base URL Bitcoin RPC",
        defValue = "",
        typeValue = "string"
    )
    private var bitcoinBaseUrl: String = ""

    @PluginOption(
        name = "btcli4j-pruned",
        description = "Tell to BTC4J plugin to use the pruned mode",
        defValue = "false",
        typeValue = "flag"
    )
    private var prunedMode = false

    private var pluginInit = false

    @RPCMethod(name = "getchaininfo", description = "getchaininfo to fetch the data from blockstream.info")
    fun getChainInfo(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        this.configurePluginInit(plugin)
        MediationMethod.runCommand("getchaininfo", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "estimatefees", description = "estimatefees to fetch the feed estimation from blockstream.info")
    fun estimateFees(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        MediationMethod.runCommand("estimatefees", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "getrawblockbyheight", description = "getrawblockbyheight to fetch actual blockchain height from blockstream.info")
    fun getRawBlockByHeight(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        MediationMethod.runCommand("getrawblockbyheight", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "getutxout", description = "getutxout to fetch a utx with {txid} and {vout} from blockstream.info")
    fun getUtxOut(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        MediationMethod.runCommand("getutxout", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    @RPCMethod(name = "sendrawtransaction", description = "sendrawtransaction to publish a new transaction with blockstream.info")
    fun sendRawTransaction(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        MediationMethod.runCommand("sendrawtransaction", plugin, CLightningJsonObject(request["params"].asJsonObject), response)
    }

    // TODO configure the personal endpoint propriety!!
    private fun configurePluginInit(plugin: CLightningPlugin) {
        if (!pluginInit && plugin.configs.isProxyEnabled) {
            pluginInit = true
            val proxyIp = plugin.configs.proxy.address
            val proxyPort = plugin.configs.proxy.port
            this.proxyEnable = proxyIp.isNotEmpty()
            this.proxy = "%s:%d".format(proxyIp, proxyPort)
            log(PluginLog.WARNING, "proxy enable: $proxyEnable")
            if (proxyEnable) {
                HttpRequestFactory.configureProxy(this.proxy, true)
                log(PluginLog.INFO, "Tor proxy enabled on btcli4j")
            }
        }

        if (prunedMode && bitcoinRpcPass.trim().isNotEmpty() && bitcoinRpcUser.trim().isNotEmpty()) {
            PluginManager.instance.bitcoinPass = bitcoinRpcPass
            PluginManager.instance.bitcoinUser = bitcoinRpcUser
            PluginManager.instance.baseBitcoinUrl = bitcoinBaseUrl
            PluginManager.instance.prunedMode = true
        }
    }
}
