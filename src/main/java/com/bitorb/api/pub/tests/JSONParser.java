package com.bitorb.api.pub.tests;

import com.google.gson.*;

public class JSONParser {

    public <T> T fromJSONtoObj(String jsonStr, Class<T> tClass) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson g = gsonBuilder.create();
        JsonParser parser = new JsonParser();
//        Gson g = new Gson();
        JsonObject object = parser.parse(jsonStr).getAsJsonObject();
        if (jsonStr.contains("event")) {
            JsonObject event = object.get("event").getAsJsonObject();
            return g.fromJson(event, tClass);
        } else {
            return g.fromJson(object, tClass);
        }
    }

}