package util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Arrays;

public class FieldsExclusionStrategy implements ExclusionStrategy {

    private String[] fields;

    public FieldsExclusionStrategy(){}

    public FieldsExclusionStrategy(String ... fields){
        this.fields = Arrays.copyOf(fields, fields.length);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        String fieldName = fieldAttributes.getName();
        for (String field : fields){
            if (fieldName.equals(field)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
