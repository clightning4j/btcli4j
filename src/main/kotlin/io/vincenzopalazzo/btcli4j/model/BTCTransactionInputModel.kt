package io.vincenzopalazzo.btcli4j.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class BTCTransactionInputModel{

    @SerializedName("txid")
    val txId: String? = null
        get() = field

    val vout: Long = 0
        get() = field

    @SerializedName("prevout")
    val preVout: JsonObject? = null // What is the return type here?
        get() = field

    @SerializedName("scriptsig")
    val scriptSig: String? = null
        get() = field

    @SerializedName("scriptsig_asm")
    val scriptsigAsm: String? = null
        get() = field
}