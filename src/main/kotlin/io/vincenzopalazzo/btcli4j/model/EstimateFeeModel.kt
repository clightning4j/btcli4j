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

/**
 * @author https://github.com/vincenzopalazzo
 */
class EstimateFeeModel {

    val mapEstimationFee = HashMap<Int, Double>()
        get() = field

    fun putValue(key: Int, value: Double){
        mapEstimationFee.put(key, value)
    }

    fun getValue(key: Int): Double{
        return mapEstimationFee.getOrDefault(key, 0.0)
    }

    fun containsKey(key: Int): Boolean{
        return mapEstimationFee.containsKey(key)
    }

    fun isEmpty(): Boolean {
        return mapEstimationFee.isEmpty()
    }

    //FIXME very stupid fee estimation!
    fun getAverageEstimateFee(): Double{
        var correctValue = 0.0
        mapEstimationFee.forEach{
            correctValue += it.value
        }
        return correctValue / mapEstimationFee.size
    }
}