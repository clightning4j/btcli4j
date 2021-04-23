package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName

class EstimateFeeBitcoin {
    @SerializedName("feerate")
    var feeRate: Double? = null
    var errors: ArrayList<String>? = null
    var blocks: Long? = null

    fun convertBtcToSat() {
        this.feeRate = this.feeRate!! * 10_000_000
    }
}
