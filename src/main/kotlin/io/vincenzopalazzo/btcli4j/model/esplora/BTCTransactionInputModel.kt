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
package io.vincenzopalazzo.btcli4j.model.esplora

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

/**
 * @author https://github.com/vincenzopalazzo
 */
class BTCTransactionInputModel {

    @SerializedName("txid")
    val txId: String? = null

    val vout: Long = 0

    @SerializedName("prevout")
    val preVout: JsonObject? = null // What is the return type here?

    @SerializedName("scriptsig")
    val scriptSig: String? = null

    @SerializedName("scriptsig_asm")
    val scriptsigAsm: String? = null
}
