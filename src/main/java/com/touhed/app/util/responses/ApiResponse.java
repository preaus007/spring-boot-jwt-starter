package com.touhed.app.util.responses;

import lombok.Data;

@Data
public class ApiResponse<T>{
    private String message;
    private int status;
    private T data;

    public ApiResponse( String message, int status, T data ) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public ApiResponse( String message, int status ) {
        this.message = message;
        this.status = status;
    }

    public static ApiResponse<?> success( String message ) {
        return new ApiResponse<>( message, 200 );
    }
}
