package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName

class UTXOBitcoin {
    @SerializedName("value")
    var amount: Double? = null
    @SerializedName("scriptPubKey")
    val script: ScriptPubKey? = null

    fun convertBtcToSat() {
        this.amount = this.amount!!.div(10_000_000)
    }
}

class ScriptPubKey {
    var hex: String? = null
}
