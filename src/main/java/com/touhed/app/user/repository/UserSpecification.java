package com.touhed.app.user.repository;

import com.touhed.app.user.Role;
import com.touhed.app.user.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserSpecification {

    public static Specification<User> filterByIsActive(Boolean isActive ) {
        if( isActive == null )
            return Specification.where((Specification<User>) null);

        return ( root, query, criteriaBuilder ) -> criteriaBuilder.equal( root.get( "isActive" ), isActive );
    }

    public static Specification<User> filterByIsDeletedNull() {
        return ( root, query, criteriaBuilder ) -> criteriaBuilder.isNull( root.get( "isDeleted" ) );
    }

    public static Specification<User> filterByIsDeletedFalse() {
        return ( root, query, criteriaBuilder ) -> criteriaBuilder.isFalse( root.get( "isDeleted" ) );
    }

    public static Specification<User> filterByRoleIn( List<Role> roles ) {
        if( roles == null )
            return Specification.where((Specification<User>) null);

        return (root, query, criteriaBuilder) -> root.get( "role" ).in( roles );
    }
}
