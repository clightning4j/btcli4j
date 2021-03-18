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
package io.vincenzopalazzo.btcli4j.util

import com.google.gson.GsonBuilder
import io.vincenzopalazzo.btcli4j.model.EstimateFeeModel
import io.vincenzopalazzo.btcli4j.util.typeadapter.EstimateFeeTypeAdapter
import java.lang.reflect.Type

/**
 * @author https://github.com/vincenzopalazzo
 */
object JSONConverter {

    private val gsonBuilder = GsonBuilder()

    init {
        gsonBuilder.registerTypeAdapter(EstimateFeeModel::class.java, EstimateFeeTypeAdapter())
        gsonBuilder.setPrettyPrinting()
    }

    private val gson = gsonBuilder.create()

    fun serialize(obj: Any): String? {
        return gson.toJson(obj)
    }

    fun <T> deserialize(fromString: String, responseType: Type): T {
        try {
            return gson.fromJson(fromString, responseType)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw RuntimeException(ex.cause)
        }
    }
}
