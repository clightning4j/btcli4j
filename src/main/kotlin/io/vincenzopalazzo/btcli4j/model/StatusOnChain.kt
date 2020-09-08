package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

class StatusOnChain {

    var confirmed: Boolean? = null
        get() = field

    @SerializedName("block_height")
    var blockHeight: Long? = null
        get() = field

    @SerializedName("block_hash")
    var blockHash: String? = null
        get() = field

    @SerializedName("block_time")
    var blockTime: Long? = null
        get() = field
}