package com.example;

import org.hibernate.dialect.MySQL8Dialect;

public class TypeReferenceConfig {
    public String dialectClassName() {
        return MySQL8Dialect.class.getName();
    }
}
