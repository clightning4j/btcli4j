package io.vincenzopalazzo.btcli4j.control.checkchain.checks

import io.vincenzopalazzo.btcli4j.control.checkchain.CheckResult
import jrpc.clightning.plugins.CLightningPlugin
import okio.ByteString

interface CheckHandler {
    fun check(plugin: CLightningPlugin, response: ByteString): CheckResult
}