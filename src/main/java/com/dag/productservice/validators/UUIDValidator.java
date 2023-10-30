package com.dag.productservice.validators;

import com.dag.productservice.validators.schema.Validator;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class UUIDValidator implements Validator<String> {
    public boolean isValid(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        return uuid.matches("[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}");
    }
}