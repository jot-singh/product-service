package com.dag.productservice.validators.schema;

 import java.util.ArrayList;
 import java.util.List;

 public class ValidatorPipeline<T> implements Validator<T> {
     private List<Validator<T>> validators;

     public ValidatorPipeline() {
         this.validators = new ArrayList<>();
     }

     public ValidatorPipeline<T> addValidator(Validator<T> validator) {
         this.validators.add(validator);
         return this;
     }

     @Override
     public boolean isValid(T value) {
         for (Validator<T> validator : validators) {
             if (!validator.isValid(value)) {
                 return false;
             }
         }
         return true;
     }
 }