package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

class BTCTransactionOutputModel {

    @SerializedName("scriptpubkey")
    val scriptPubKey: String? = null
        get() = field

    @SerializedName("scriptpubkey_asm")
    val scriptpubkeyAsm: String? = null
        get() = field

    @SerializedName("scriptpubkey_type")
    val scriptPubKeyType: String? = null
        get() = field

    val value: Long = 0
        get() = field
}