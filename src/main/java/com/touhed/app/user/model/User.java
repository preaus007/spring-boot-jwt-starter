package com.touhed.app.user.model;

import com.touhed.app.document.model.Document;
import com.touhed.app.user.BloodGroup;
import com.touhed.app.user.Gender;
import com.touhed.app.user.Role;
import com.touhed.app.user.request.AddUserRequest;
import com.touhed.app.util.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@Table( name="users" )
public class User extends AuditableEntity {
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

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "profile_pic_id", foreignKey = @ForeignKey(name = "fk_user_profile_pic_id") )
    private Document profilePic;

    public User( AddUserRequest addUserRequest ) {
        this.name = addUserRequest.getName();
        this.email = addUserRequest.getEmail();
        this.phoneNumber = addUserRequest.getPhoneNumber();
        this.role = addUserRequest.getRole();
        this.gender = addUserRequest.getGender();
        this.bloodGroup = addUserRequest.getBloodGroup();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass( this ) != Hibernate.getClass( o ) ) return false;
        User user = (User) o;
        return id != null && Objects.equals( id, user.id );
    }

    @Override
    public int hashCode() {
        if ( this.id == null )
            return System.identityHashCode( this );
        return Objects.hash( this.id );
    }
}
