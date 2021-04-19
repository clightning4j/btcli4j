package io.vincenzopalazzo.btcli4j.model.bitcoin

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

class BlockchainInfoBitcoin {
    var chain: String? = null
    @SerializedName("initialblockdownload")
    var isDownloading: Boolean? = null
    @SerializedName("headers")
    var headerCount: BigInteger? = null
    @SerializedName("blocks")
    var blockCount: BigInteger? = null
}
