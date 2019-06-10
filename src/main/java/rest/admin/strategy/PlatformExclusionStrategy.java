package rest.admin.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class PlatformExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        String fieldName = fieldAttributes.getName();
        return fieldName.equals("partner");
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
