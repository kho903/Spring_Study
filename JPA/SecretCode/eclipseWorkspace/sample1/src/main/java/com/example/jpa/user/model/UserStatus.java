package com.example.jpa.user.model;

public enum UserStatus {

    None, Using, Stop;

    int value;

    public int getValue() {
        return this.value;
    }
}
