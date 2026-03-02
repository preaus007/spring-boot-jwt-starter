package com.touhed.app.user.request;

import com.touhed.app.user.Role;
import lombok.Data;

@Data
public class GetUserRequest {

    private Boolean isActive;
    private Role role;
}
