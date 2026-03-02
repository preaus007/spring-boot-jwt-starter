package com.touhed.app.user.repository;

import com.touhed.app.user.model.User;
import com.touhed.app.util.dto.EntityDocumentDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail( String email );

    @Modifying
    @Query(
            "update User " +
                    "set refreshToken = ?2 " +
                    "where id = ?1"
    )
    void updateRefreshTokenById( long id, String refreshToken );

    @Cacheable( value = "userByIdCache", key = "#currentUserId", unless = "#result == null" )
    User getUserById( Long currentUserId );

    @Query( "SELECT u FROM User u WHERE u.isDeleted IS NULL OR u.isDeleted = false ORDER BY u.modifiedAt DESC" )
    List<User> findAllByNotDeleted();

    @Query( "SELECT new com.touhed.app.util.dto.EntityDocumentDTO( u.id, p ) FROM User u Left JOIN u.profilePic p " +
            "WHERE u.id IN ?1 AND ( u.isDeleted IS NULL OR u.isDeleted = false ) " )
    List<EntityDocumentDTO> getUserProfilePicByIdIn(List<Long> userIds );
}
