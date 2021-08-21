package io.vincenzopalazzo.btcli4j.util

class OptionsManager(private val url: String, val waitingTime: Int, var torVersion: Int?, var proxyEnabled: Boolean, var proxyUrl: String) {
    companion object {
        // Default values
        private const val BASE_URL = "https://blockstream.info"
        private const val BASE_URL_TORV3 = "http://explorerzydxu5ecjrkwceayqybizmpjjznk5izmitf2modhcusuqlid.onion"
        private const val BASE_URL_TORV2 = "http://explorernuoc63nb.onion" // TODO: This is deprecated in the lightningd from august 2021
    }

    private fun customEndPoint(): Boolean {
        when (url) {
            BASE_URL -> return true
            BASE_URL_TORV2 -> return true
            BASE_URL_TORV3 -> return true
        }
        return false
    }

    fun getEndPointUrl(): String {
        if (customEndPoint())
            return url
        return when (proxyEnabled) {
            true -> if (torVersion == 3) BASE_URL_TORV3 else if (torVersion == 2) BASE_URL_TORV2 else BASE_URL
            false -> BASE_URL
        }
    }
}
