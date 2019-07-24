package rest.customer.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CampaignExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        String fieldName = fieldAttributes.getName();
        return fieldName.equals("customer")
//                || fieldName.equals("id")
                || fieldName.equals("removedDate");
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
