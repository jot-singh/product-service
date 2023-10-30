package com.dag.productservice.validators.schema;

public interface Validator<T extends Object> {
    boolean isValid(T value);
}
