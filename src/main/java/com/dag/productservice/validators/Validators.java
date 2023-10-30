package com.dag.productservice.validators;

import com.dag.productservice.validators.schema.ValidatorPipeline;
public enum Validators {
    UUID_VALIDATOR(new ValidatorPipeline<String>()
            .addValidator(MaxLengthValidator.builder().maxLength(36).build())
            .addValidator(MinLengthValidator.builder().minLength(36).build())
            .addValidator(UUIDValidator.builder().build())),
    MIN_LENGTH_VALIDATOR(new ValidatorPipeline<String>()
            .addValidator(MinLengthValidator.builder().minLength(0).build())),
    MAX_LENGTH_VALIDATOR(new ValidatorPipeline<String>()
            .addValidator(MaxLengthValidator.builder().maxLength(0).build())),
    NON_NULL_CHECK(new ValidatorPipeline<String>()
            .addValidator(NonNullCheck.builder().build()));

    private final ValidatorPipeline<String> validatorPipeline;

    Validators(ValidatorPipeline<String> stringValidatorPipeline) {
        this.validatorPipeline = stringValidatorPipeline;
    }

    public ValidatorPipeline<String> get() {
        return validatorPipeline;
    }
}


