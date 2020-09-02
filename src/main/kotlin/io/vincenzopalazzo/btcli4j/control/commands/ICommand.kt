package io.vincenzopalazzo.btcli4j.control.commands

import jrpc.clightning.plugins.CLightningPlugin
import jrpc.service.converters.jsonwrapper.CLightningJsonObject

interface ICommand {

    fun run(plugin: CLightningPlugin, request: CLightningJsonObject, response: CLightningJsonObject)
}