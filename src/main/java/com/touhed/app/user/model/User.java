package com.touhed.app.user.model;

import com.touhed.app.user.BloodGroup;
import com.touhed.app.user.Gender;
import com.touhed.app.user.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table( name="users" )
public class User {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "name", nullable = false )
    private String name;

    @Column( name = "password", nullable = false )
    private String password;

    @Column( name = "email", nullable = false, unique = true )
    private String email;

    @Column( name = "phone_number" )
    private String phoneNumber;

    @Enumerated( EnumType.STRING )
    @Column(name = "gender")
    private Gender gender;

    @Enumerated( EnumType.STRING )
    @Column( name = "blood_group" )
    private BloodGroup bloodGroup;

    @Enumerated( EnumType.STRING )
    @Column( name = "role", nullable = false )
    private Role role;

    @Column( name = "is_active", columnDefinition = "boolean default true" )
    private Boolean isActive = true;

    @Column( name = "refresh_token" )
    private String refreshToken;
}
