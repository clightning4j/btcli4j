package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

class StatusOnChain {

    var confirmed: Boolean? = null

    @SerializedName("block_height")
    var blockHeight: Long? = null

    @SerializedName("block_hash")
    var blockHash: String? = null

    @SerializedName("block_time")
    var blockTime: Long? = null
}