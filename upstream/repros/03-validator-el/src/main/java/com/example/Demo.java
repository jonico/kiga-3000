package com.example;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * Prints the interpolated constraint message. The message template below uses an EL
 * expression, so it only renders correctly when an EL implementation is on the
 * classpath and is compatible with the Hibernate Validator version in use.
 */
public class Demo {

    public static class Person {
        @Size(min = 2, max = 5, message = "length must be between {min} and {max}, was '${validatedValue}'")
        public String name = "abcdefghij";
    }

    public static void main(String[] args) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Person>> violations = validator.validate(new Person());
        for (ConstraintViolation<Person> v : violations) {
            System.out.println("MESSAGE: " + v.getMessage());
        }
        if (violations.isEmpty()) {
            System.out.println("MESSAGE: (no violations - unexpected)");
        }
    }
}
