package com.gamezone.persistence;

import com.gamezone.model.DigitalVideoGame;
import com.gamezone.model.PhysicalVideoGame;
import com.gamezone.model.VideoGame;
import com.google.gson.*;

import java.lang.reflect.Type;

public class VideoGameTypeAdapter implements JsonSerializer<VideoGame>, JsonDeserializer<VideoGame> {

    private static final String TYPE_FIELD = "type";
    private static final String DIGITAL = "DIGITAL";
    private static final String PHYSICAL = "FISICO";

    @Override
    public JsonElement serialize(VideoGame src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject;
        if (src instanceof DigitalVideoGame) {
            jsonObject = (JsonObject) context.serialize(src, DigitalVideoGame.class);
            jsonObject.addProperty(TYPE_FIELD, DIGITAL);
        } else if (src instanceof PhysicalVideoGame) {
            jsonObject = (JsonObject) context.serialize(src, PhysicalVideoGame.class);
            jsonObject.addProperty(TYPE_FIELD, PHYSICAL);
        } else {
            throw new JsonParseException("Tipo de videojuego desconocido: " + src.getClass());
        }
        return jsonObject;
    }

    @Override
    public VideoGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.has(TYPE_FIELD) ? jsonObject.get(TYPE_FIELD).getAsString() : "";

        if (DIGITAL.equalsIgnoreCase(type)) {
            return context.deserialize(json, DigitalVideoGame.class);
        } else if (PHYSICAL.equalsIgnoreCase(type)) {
            return context.deserialize(json, PhysicalVideoGame.class);
        }
        throw new JsonParseException("No se reconoce el tipo de videojuego: " + type);
    }
}
