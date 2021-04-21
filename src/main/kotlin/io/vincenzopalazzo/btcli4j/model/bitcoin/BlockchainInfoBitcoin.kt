package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName

class BlockchainInfoBitcoin {
    var chain: String? = null
    @SerializedName("initialblockdownload")
    var isDownloading: Boolean? = null
    @SerializedName("headers")
    var headerCount: Long? = null
    @SerializedName("blocks")
    var blockCount: Long? = null
}
