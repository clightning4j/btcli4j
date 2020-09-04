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

import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.exceptions.CLightningPluginException
import jrpc.clightning.plugins.log.CLightningLevelLog
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okio.IOException

/**
 * @author https://github.com/vincenzopalazzo
 */
class GetChainInfoCommand: ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val network = "testnet/api"
        try {
            val reqGenesisBlock = HttpRequestFactory.createRequest("%s/block-height/0".format(network))
            val genesisBlock: String
            if(reqGenesisBlock != null){
                genesisBlock = HttpRequestFactory.execRequest(reqGenesisBlock).utf8()
                plugin.log(PluginLog.DEBUG, "Genesis block %s".format(genesisBlock))
            }else{
                plugin.log(PluginLog.ERROR, "Request for genesis block null!!!")
                throw CLightningPluginException(400, "Request for genesis block null!!!")
            }

            val reqBlockchainHeight = HttpRequestFactory.createRequest("%s/blocks/tip/height".format(network))
            val blockCount: Int
            if(reqBlockchainHeight != null){
                blockCount = HttpRequestFactory.execRequest(reqBlockchainHeight).utf8().toInt()
                plugin.log(PluginLog.DEBUG, "Block count = %s".format(blockCount.toString()))
            }else{
                plugin.log(PluginLog.ERROR, "Request for genesis block null!!!")
                throw CLightningPluginException(400, "Request for genesis block null!!!")
            }

            //TODO add support to elements
            var chain = ""
            when(genesisBlock){
                "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f" -> chain = "main"
                "000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943" -> chain = "test"
                "0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206" -> chain = "main"
            }

            response.apply {
                add("chain", chain)
                add("headercount", blockCount)
                add("blockcount", blockCount)
                add("ibd", false)
            }
        }catch (ex: IOException){
            plugin.log(PluginLog.WARNING, ex.localizedMessage)
            throw CLightningPluginException(400, ex.localizedMessage)
        }

    }
}