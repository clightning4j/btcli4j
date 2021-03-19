package io.vincenzopalazzo.btcli4j.control.checkchain.checks

import io.vincenzopalazzo.btcli4j.control.checkchain.CheckResult
import jrpc.clightning.plugins.CLightningPlugin
import okio.ByteString

class TransactionCheck : CheckHandler {
    override fun check(plugin: CLightningPlugin, response: ByteString): CheckResult {
        val responseString = response.utf8()
        // What this error means from esplora API?
        if (responseString.contains("bad-txns-inputs-missingorspent", true)) {
            return CheckResult(false, ByteString.EMPTY)
        } else if (responseString.contains("Transaction already in block chain", true)) {
            return CheckResult(false, ByteString.EMPTY)
        }
        return CheckResult(true)
    }
}
