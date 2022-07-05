package io.vincenzopalazzo.btcli4j.control.checkchain

import okio.ByteString

/**
 * This simple class manage check inside the chain, in particular each check return a CheckResult
 * and the chain manage this object to make the correct behaviors
* FIXME: add a static string for the failure, maybe we can remove the null
 */
class CheckResult(val next: Boolean, val result: ByteString? = null) {

    /**
     * Return true if the request contains a valid format, otherwise
     * false.
     */
    fun isSafe(): Boolean {
        return result != null && !result.utf8().contains("Check fails")
    }
}
