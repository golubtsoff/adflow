package rest.admin;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class UserExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        String fieldName = fieldAttributes.getName();
        return fieldName.equals("hash")
                || fieldName.equals("person")
                || fieldName.equals("contact");
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
