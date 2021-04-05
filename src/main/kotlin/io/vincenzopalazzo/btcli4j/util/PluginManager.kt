package io.vincenzopalazzo.btcli4j.util

class PluginManager {

    companion object {
        val instance = PluginManager()
    }

    var prunedMode: Boolean = false
    var bitcoinPass = ""
    var bitcoinUser = ""
    var baseBitcoinUrl = ""
}
