package io.rverb.feedback.utility;

import com.google.gson.Gson;

import io.rverb.feedback.model.Cacheable;

public class DataUtils {
    public static <T extends Cacheable> T fromJson(String json, Class<T> type) {
        Gson gson = new Gson();
        T dataObject = gson.fromJson(json, type);

        return dataObject;
    }
}
