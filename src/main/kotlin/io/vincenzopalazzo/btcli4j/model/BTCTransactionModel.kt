/**
 *  C-lightning plugin to override Bitcoin backend plugin.
 *  Copyright (C) 2020 Vincenzo Palazzo vincenzopalazzodev@gmail.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package io.vincenzopalazzo.btcli4j.model

import com.google.gson.annotations.SerializedName

/**
 * @author https://github.com/vincenzopalazzo
 */
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