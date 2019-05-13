package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHelper {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private JsonHelper(){}

    public static Gson getGson(){
        return gson;
    }
}
