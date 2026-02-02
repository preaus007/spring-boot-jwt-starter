package com.touhed.app.user;

import lombok.Getter;

@Getter
public enum BloodGroup {
    A_POSITIVE( "A+" ),
    A_NEGATIVE( "A-" ),
    B_POSITIVE( "B+" ),
    B_NEGATIVE( "B-" ),
    O_POSITIVE( "O+" ),
    O_NEGATIVE( "O-"),
    AB_POSITIVE( "AB+" ),
    AB_NEGATIVE( "AB-" );

    private final String value;

    BloodGroup( String value ) {
        this.value = value;
    }
}
