package io.vincenzopalazzo.btcli4j.control.checkchain

import io.vincenzopalazzo.btcli4j.control.Command
import io.vincenzopalazzo.btcli4j.control.checkchain.checks.BlockNotFoundCheck
import io.vincenzopalazzo.btcli4j.control.checkchain.checks.CheckHandler
import io.vincenzopalazzo.btcli4j.control.checkchain.checks.GetUtxoInvalidFormat
import io.vincenzopalazzo.btcli4j.control.checkchain.checks.TransactionCheck
import jrpc.clightning.plugins.CLightningPlugin
import okio.ByteString

/**
 * Chain of responsibility that manage all the type of error that can occurs during
 * the plugin workflow, mostly due to the Rest API fault.
 *
 * FIXME: Some checks are done inside HttpRequestFactory because it is easily make re retry logic but
 * this is a terrible performance leak.
 *
 * Some other check like checkByCommand are done inside the command logics, and this usually help to avoid
 * hack in the source code like response == "Block not found".
 *
 * @author https://github.com/vincenzopalazzo
 */
class ChainOfResponsibilityCheck {

    private val checksHandles: ArrayList<CheckHandler> = ArrayList()
    private var commandsCheck: HashMap<String, List<CheckHandler>> = HashMap()

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
        commandsCheck[Command.GetUtxOutBtc.toString()] = listOf(GetUtxoInvalidFormat())
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

    fun checkCommand(forCommand: Command, plugin: CLightningPlugin, response: ByteString): CheckResult {
        if (commandsCheck.containsKey(forCommand.toString())) {
            val handler = commandsCheck[forCommand.toString()]!!
            handler.forEach {
                val result = it.check(plugin, response)
                if (!result.next)
                    return result
            }
            return CheckResult(false, response)
        }
        throw IllegalArgumentException("No sanity check for command $forCommand")
    }
}
