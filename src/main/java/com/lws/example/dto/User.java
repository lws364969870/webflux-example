package com.lws.example.dto;

import lombok.Data;

@Data
public class User {

    public User() {
    }

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private Integer id;

    private String name;
}
