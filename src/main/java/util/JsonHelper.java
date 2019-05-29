package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

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

    public static String getJsonStringExcludeFields(Object object, List<String> excludeFields){
        Class cl = object.getClass();
        JsonObject jo = getGson().toJsonTree(cl.cast(object)).getAsJsonObject();
        for (String field : excludeFields)
            jo.remove(field);
        return JsonHelper.getGson().toJson(jo);
    }
}
