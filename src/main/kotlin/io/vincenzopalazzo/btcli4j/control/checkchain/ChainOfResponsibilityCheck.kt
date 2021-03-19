package io.vincenzopalazzo.btcli4j.control.checkchain

import io.vincenzopalazzo.btcli4j.control.checkchain.checks.BlockNotFoundCheck
import io.vincenzopalazzo.btcli4j.control.checkchain.checks.CheckHandler
import io.vincenzopalazzo.btcli4j.control.checkchain.checks.TransactionCheck
import jrpc.clightning.plugins.CLightningPlugin
import okio.ByteString

class ChainOfResponsibilityCheck {

    private val checksHandles: ArrayList<CheckHandler> = ArrayList()

    init {
        initChains()
    }

    private fun initChains() {
        checksHandles.addAll(
            listOf(
                BlockNotFoundCheck(),
                TransactionCheck()
            )
        )
    }

    fun check(plugin: CLightningPlugin, response: ByteString): CheckResult {
        for (check in checksHandles) {
            val result = check.check(plugin, response)
            if (!result.next)
                return result
        }
        // this should be never happen
        return CheckResult(false, ByteString.of("Check fails".toByte()))
    }
}
