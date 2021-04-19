package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

class EstimateFeeBitcoin {
    @SerializedName("feerate")
    var feeRate: BigInteger? = null
    var errors: ArrayList<String>? = null
    var blocks: BigInteger? = null
}