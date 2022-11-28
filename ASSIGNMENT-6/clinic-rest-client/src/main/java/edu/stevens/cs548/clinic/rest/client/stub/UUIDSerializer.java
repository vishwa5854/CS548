package edu.stevens.cs548.clinic.rest.client.stub;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UUIDSerializer implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    @Override
    public JsonElement serialize(UUID id, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(id.toString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String id = json.getAsJsonPrimitive().getAsString();
        return UUID.fromString(id);
    }
}