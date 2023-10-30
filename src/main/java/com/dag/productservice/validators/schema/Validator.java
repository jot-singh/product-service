package com.dag.productservice.validators.schema;

public interface Validator<T> {
    boolean isValid(T value);
}
