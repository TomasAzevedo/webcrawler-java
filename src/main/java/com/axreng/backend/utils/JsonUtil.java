package com.axreng.backend.utils;

import com.google.gson.Gson;
import spark.ResponseTransformer;


/**
 * Utility class for converting objects to json.
 */
public class JsonUtil {


    /**
     * Transform a given object into json.
     *
     * @param object - Object to be transformed.
     * @return json
     */
    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }


    /**
     * Transforms a json String into an object.
     *
     * @param json     - json String.
     * @param classOfT - desired class.
     * @return object.
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return new Gson().fromJson(json, classOfT);
    }


    /**
     * Method that guarantees an implementation for Spark to transform the response into json.
     *
     * @return ResponseTransformer instance.
     */
    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }

}
