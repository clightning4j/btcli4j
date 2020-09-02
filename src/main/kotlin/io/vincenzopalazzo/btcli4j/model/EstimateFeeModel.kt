package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

open class EstimateFeeModel {

    @SerializedName("1")
    val oneConfirmation: Float = 0f
        get() = field / 10000

    @SerializedName("2")
    val twoConfirmation: Float = 0f
        get() = field / 10000

    @SerializedName("3")
    val treeConfirmation: Float = 0f
        get() = field / 10000

    @SerializedName("4")
    val fourConfirmation: Float = 0f
        get() = field / 10000

    //TODO miss some properiety

    @SerializedName("144")
    val oneHundredXXConfirmation: Float = 0f
        get() = field / 10000

    @SerializedName("504")
    val fiveHundredXXConfirmation: Float = 0f
        get() = field / 10000

    @SerializedName("1008")
    val oneThousandXXConfirmation: Float = 0f
        get() = field / 10000

    fun isEmpty(): Boolean {
        val empty = oneConfirmation == 0f ||
                twoConfirmation == 0f ||
                treeConfirmation == 0f ||
                fourConfirmation == 0f ||
                oneHundredXXConfirmation == 0f ||
                fiveHundredXXConfirmation == 0f ||
                oneThousandXXConfirmation == 0f

        return empty
    }
}