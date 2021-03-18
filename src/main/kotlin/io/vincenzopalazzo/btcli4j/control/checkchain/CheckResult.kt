package io.vincenzopalazzo.btcli4j.control.checkchain

import okio.ByteString

/**
 * This simple class manage check inside the chain, in particular each check return a CheckResult
 * and the chain manage this object to make the correct behaviors
 */
class CheckResult(val next: Boolean, val result: ByteString? = null)