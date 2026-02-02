package com.touhed.app.util.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException( String entityName, Long entityId ) {
        super( "Request for " + entityName + " with id " + entityId + " not found" );
    }

    public NotFoundException( String entityName, String entityProperty, String entityPropertyValue ) {
        super( "Request for " + entityName + " with " + entityProperty + " " + entityPropertyValue + " not found" );
    }
}
