package com.touhed.app.user;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN( "Admin" ),
    USER( "User" );

    private final String value;

    Role( String value ) {
        this.value = value;
    }
}
