package com.touhed.app.user.response;

import com.touhed.app.document.response.DocumentResponse;
import com.touhed.app.user.model.User;
import com.touhed.app.util.responses.EnumResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String employeeCode;
    private String designation;
    private String dateOfBirth;
    private String address;
    private String dateOfJoining;
    private EnumResponse department;
    private EnumResponse role;
    private EnumResponse bloodGroup;
    private EnumResponse gender;
    private EnumResponse employeeType;
    private DocumentResponse profilePic;

    public UserResponse( User user ) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole() != null ?
                new EnumResponse( user.getRole().getValue(), user.getRole().name() ) : null;
        this.bloodGroup = user.getBloodGroup() != null ?
                new EnumResponse( user.getBloodGroup().getValue() , user.getBloodGroup().name() ) : null;
        this.gender = user.getGender() != null ?
                new EnumResponse( user.getGender().getValue() , user.getGender().name() ) : null;
    }
}
