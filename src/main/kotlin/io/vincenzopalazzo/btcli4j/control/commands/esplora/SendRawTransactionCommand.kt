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
import io.vincenzopalazzo.btcli4j.util.HttpRequestFactory
import jrpc.clightning.plugins.CLightningPlugin
import jrpc.clightning.plugins.log.PluginLog
import jrpc.service.converters.jsonwrapper.CLightningJsonObject
import okhttp3.MediaType.Companion.toMediaType

/**
 * @author https://github.com/vincenzopalazzo
 */
class SendRawTransactionCommand : ICommand {
    override fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject) {
        val queryUrl = HttpRequestFactory.buildQueryRL(plugin.configs.network)

        val txRaw = request["tx"].asString
        try {
            val reqSendTx = HttpRequestFactory.createRequest(
                "%s/tx".format(queryUrl), type = "post", body = txRaw,
                mediaType = "plain/text".toMediaType()
            )!!

            val resSendTx = HttpRequestFactory.execRequest(plugin, reqSendTx).utf8()
            response.apply {
                add("success", resSendTx.isNotEmpty()) // TODO validate if it is a txId
                add("errmsg", resSendTx.isNotEmpty()) // check this code
            }
        } catch (ex: Exception) {
            plugin.log(PluginLog.WARNING, ex.localizedMessage)
        }
    }
}
