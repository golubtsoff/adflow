package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHelper {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Gson gsonExcludeFieldsWithoutExposeAnnotation = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting().create();

    private JsonHelper(){}

    public static Gson getGson(){
        return gson;
    }

    public static Gson getGsonExcludeFieldsWithoutExposeAnnotation(){
        return gsonExcludeFieldsWithoutExposeAnnotation;
    }
}
