package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName

class UTXOBitcoin {
    @SerializedName("value")
    var amount: Double? = null
    @SerializedName("scriptPubKey")
    val script: ScriptPubKey? = null
}

class ScriptPubKey {
    var hex: String? = null
}