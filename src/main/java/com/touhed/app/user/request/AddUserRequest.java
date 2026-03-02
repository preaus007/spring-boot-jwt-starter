package com.touhed.app.user.request;

import com.touhed.app.document.DocumentDTO;
import com.touhed.app.user.BloodGroup;
import com.touhed.app.user.Gender;
import com.touhed.app.user.Role;
import lombok.Data;

@Data
public class AddUserRequest {
    private String name;
    private String password;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private String designation;
    private String employeeCode;
    private Gender gender;
    private BloodGroup bloodGroup;
    private String address;
    private String dateOfJoining;
    private Role role;
    private DocumentDTO profilePic;
}
