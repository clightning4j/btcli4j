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

/**
 * @author https://github.com/vincenzopalazzo
 */
class EstimateFeeModel {

    val mapEstimationFee = HashMap<Int, Double>()

    fun putValue(key: Int, value: Double) {
        mapEstimationFee.put(key, value)
    }

    fun getValue(key: Int): Double {
        return mapEstimationFee.getOrDefault(key, 0.0)
    }

    fun containsKey(key: Int): Boolean {
        return mapEstimationFee.containsKey(key)
    }

    fun isEmpty(): Boolean {
        return mapEstimationFee.isEmpty()
    }

    /**
     * This method calculate the take the latest feerate return from the API in the latest block
     * with the esplora api the minimum blocks are 6, if this value is not present
     * it is calculate the minimum number of blocks in the JSON response.
     */
    fun estimateFeeRate(): Double {
        var minKey = 0
        var result = 0.0
        if (mapEstimationFee.containsKey(6)) {
            return mapEstimationFee[6]!!
        }

        mapEstimationFee.forEach {
            if (minKey > it.key) {
                minKey = it.key
                result = it.value
            }
        }
        return result
    }

    fun estimateFeeForNormalTarget(): Double {
        if (containsKey(5)) {
            return getValue(5)
        } else if (containsKey(6)) {
            return getValue(6)
        } else if (containsKey(4)) {
            return getValue(5)
        }
        return estimateFeeRate()
    }

    fun estimateFeeForUrgentTarget(): Double {
        if (containsKey(3)) {
            return getValue(3)
        } else if (containsKey(2)) {
            return getValue(2)
        } else if (containsKey(4)) {
            return getValue(4)
        }
        // TOD change this with very urgent
        return estimateFeeForVeryUrgentTarget()
    }

    fun estimateFeeForVeryUrgentTarget(): Double {
        var bigKey = 0
        var result = 0.0
        if (mapEstimationFee.containsKey(2)) {
            return mapEstimationFee[2]!!
        }
        mapEstimationFee.forEach {
            if (bigKey > it.key) {
                bigKey = it.key
                result = it.value
            }
        }
        return result
    }

    // FIXME very stupid fee estimation!
    fun getAverageEstimateFee(): Double {
        var correctValue = 0.0
        mapEstimationFee.forEach {
            correctValue += it.value
        }
        return correctValue / mapEstimationFee.size
    }
}
