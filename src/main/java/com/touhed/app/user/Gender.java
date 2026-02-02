package com.touhed.app.user;

import lombok.Getter;

@Getter
public enum Gender {
    MALE( "Male" ),
    FEMALE( "Female" );

    private final String gender;

    public String getValue(){
        return gender;
    }

    Gender( String gender ) {
        this.gender = gender;
    }
}
