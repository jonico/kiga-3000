package com.example;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book {
    @Id
    private Long id;

    public Long getId() {
        return id;
    }
}
