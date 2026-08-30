package com.example;

import java.util.Properties;

public class PropertiesConfig {
    public Properties settings() {
        Properties p = new Properties();
        p.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9iDialect");
        return p;
    }
}
