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
class EstimateFeeModel {

    @SerializedName("1")
    val oneConfirmation: Double = 0.0
        get() = field / 10000

    @SerializedName("2")
    val twoConfirmation: Double = 0.0
        get() = field / 10000

    @SerializedName("3")
    val treeConfirmation: Double = 0.0
        get() = field / 10000

    @SerializedName("4")
    val fourConfirmation: Double = 0.0
        get() = field / 10000

    //TODO miss some properiety

    @SerializedName("144")
    val oneHundredXXConfirmation: Double = 0.0
        get() = field / 10000

    @SerializedName("504")
    val fiveHundredXXConfirmation: Double = 0.0
        get() = field / 10000

    @SerializedName("1008")
    val oneThousandXXConfirmation: Double = 0.0
        get() = field / 10000

    fun isEmpty(): Boolean {
        val empty = oneConfirmation == 0.0 &&
                twoConfirmation == 0.0  &&
                treeConfirmation == 0.0  &&
                fourConfirmation == 0.0  &&
                oneHundredXXConfirmation == 0.0  &&
                fiveHundredXXConfirmation == 0.0  &&
                oneThousandXXConfirmation == 0.0

        return empty
    }
}