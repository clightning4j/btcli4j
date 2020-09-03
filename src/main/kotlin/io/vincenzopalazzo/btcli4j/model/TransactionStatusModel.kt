package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

class TransactionStatusModel {

    val confirmed: Boolean = false
        get() = field

    @SerializedName("block_height")
    val blockHeight: Long = 0
        get() = field

    @SerializedName("block_hash")
    val blockHash: String? = null
        get() = field

    @SerializedName("block_time")
    val blockTime: Long = 0
        get() = field
}