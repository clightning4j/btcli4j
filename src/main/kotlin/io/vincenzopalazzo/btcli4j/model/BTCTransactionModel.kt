package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

class BTCTransactionModel {

    @SerializedName("txid")
    val txId: String? = null
        get() = field

    val version: Int = 0
        get() = field

    @SerializedName("locktime")
    val lockTime: Long = 0
        get() = field

    @SerializedName("vin")
    val transactionsInputs: List<BTCTransactionInputModel>? = null
        get() = field

    @SerializedName("vout")
    val transactionsOutput: List<BTCTransactionOutputModel>? = null
        get() = field

    val size: Long = 0
        get() = field

    val weight: Long = 0
        get() = field

    val fee: Long = 0
        get() = field

    val status: TransactionStatusModel? = null
        get() = field
}