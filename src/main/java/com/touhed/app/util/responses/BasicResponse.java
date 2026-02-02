package com.touhed.app.util.responses;

import lombok.Data;

@Data
public class BasicResponse {
    private Long id;
    private String name;

    public BasicResponse( Long id, String name ) {
        this.id = id;
        this.name = name;
    }
}
