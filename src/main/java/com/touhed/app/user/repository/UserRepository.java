package com.touhed.app.user.repository;

import com.touhed.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail( String email );

    @Modifying
    @Query(
            "update User " +
                    "set refreshToken = ?2 " +
                    "where id = ?1"
    )
    void updateRefreshTokenById( long id, String refreshToken );
}
