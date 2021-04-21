package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

class MemPoolInfo {
    @SerializedName("loaded")
    var isReady: Boolean? = null
    var size: BigInteger? = null
}
