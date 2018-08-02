package com.khmelenko.lab.varis.network.retrofit

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

import java.io.IOException

/**
 * Types adapter for parsing JSON structures
 *
 * @author Dmytro Khmelenko
 */
class ItemTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {

        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<T>() {

            override fun write(output: JsonWriter, value: T) {
                delegate.write(output, value)
            }

            override fun read(input: JsonReader): T {

                var jsonElement = elementAdapter.read(input)
                if (jsonElement.isJsonObject) {
                    val jsonObject = jsonElement.asJsonObject
                    // fetch repositories
                    if (jsonObject.has("repos") && jsonObject.get("repos").isJsonArray) {
                        jsonElement = jsonObject.get("repos")
                    }

                    // fetch user
                    if (jsonObject.has("user") && jsonObject.get("user").isJsonObject) {
                        jsonElement = jsonObject.get("user")
                    }

                    // NOTE Add more objects here, if needed
                }

                return delegate.fromJsonTree(jsonElement)
            }
        }.nullSafe()
    }
}
