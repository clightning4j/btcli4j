package io.vincenzopalazzo.btcli4j.util.typeadapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import io.vincenzopalazzo.btcli4j.model.EstimateFeeModel
import java.math.BigDecimal

class EstimateFeeTypeAdapter : TypeAdapter<EstimateFeeModel>() {

    override fun write(jsonWriter: JsonWriter, obj: EstimateFeeModel) {
        jsonWriter.beginObject()

        obj.mapEstimationFee.forEach{
            jsonWriter.name(it.key.toString())
            jsonWriter.value(BigDecimal.valueOf(it.value))
        }
        jsonWriter.endObject()
    }

    override fun read(jsonReader: JsonReader): EstimateFeeModel {
        val feerate = EstimateFeeModel()
        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            var token = jsonReader.peek()
            val key: Int
            if(token == JsonToken.NAME){
                key = jsonReader.nextName().toInt()
                //move to next token
                token = jsonReader.peek()
                feerate.putValue(key, BigDecimal.valueOf(jsonReader.nextDouble()).toDouble())
            }
        }
        jsonReader.endObject()
        return feerate
    }
}