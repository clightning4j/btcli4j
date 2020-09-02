package io.vincenzopalazzo.btcli4j.util

import com.google.gson.GsonBuilder
import java.lang.reflect.Type

object JSONConverter {

    private val gsonBuilder = GsonBuilder()
    private val gson = gsonBuilder.create()

    init {
        gsonBuilder.setPrettyPrinting()
    }

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