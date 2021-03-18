package io.vincenzopalazzo.btcli4j.control.checkchain.checks

import io.vincenzopalazzo.btcli4j.control.checkchain.CheckResult
import jrpc.clightning.plugins.CLightningPlugin
import okio.ByteString

/**
 * When the c-lightning will ask the next block and this block
 * it is not available, the request will be not available and for this
 * reason we need to make a check of the message inside this request.
 */
class BlockNotFoundCheck: CheckHandler {
    override fun check(plugin: CLightningPlugin, response: ByteString): CheckResult {
        if (response.utf8().contentEquals("Block not found")) {
           return CheckResult(false, response)
        }
        return CheckResult(true)
    }
}