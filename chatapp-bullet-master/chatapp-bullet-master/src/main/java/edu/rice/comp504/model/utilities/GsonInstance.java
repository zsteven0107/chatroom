package edu.rice.comp504.model.utilities;

import com.google.gson.Gson;

public class GsonInstance {
    private static Gson gson;

    /**
     * Get the singleton object.
     */
    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
