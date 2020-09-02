package io.vincenzopalazzo.btcli4j.util

import com.google.gson.GsonBuilder
import java.lang.reflect.Type

object JSONConverter {

    private const val patternFormat = "dd-MM-yyyy HH:mm:ss"

    private val gsonBuilder = GsonBuilder()

    init {
        gsonBuilder.setPrettyPrinting()
        gsonBuilder.setDateFormat(patternFormat)
    }

    fun serialize(obj: Any): String? {
        val gson = gsonBuilder.create()
        return gson.toJson(obj)
    }

    fun <T> deserialize(fromString: String, responseType: Type): T {
        require(!(fromString == null || fromString.isEmpty())) { "String parameter null or empty" }
        return try {
            val gson = gsonBuilder.create()
            gson.fromJson(fromString, responseType)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw RuntimeException(ex.cause)
        }
    }


}