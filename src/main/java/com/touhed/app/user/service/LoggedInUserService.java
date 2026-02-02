package com.touhed.app.user.service;

import com.touhed.app.security.CustomUserDetails;
import com.touhed.app.user.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class LoggedInUserService {

    @PersistenceContext
    private EntityManager entityManager;

    private static Long extractId( Authentication authentication ) {
        if ( authentication == null ) {
            return null;
        }
        else if ( authentication.getPrincipal() instanceof UserDetails ) {
            CustomUserDetails springSecurityUser = ( CustomUserDetails ) authentication.getPrincipal();
            return springSecurityUser.getId();
        }
        return null;
    }

    public Authentication getAuthentication(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication();
    }

    public Long getCurrentUserId(){
        return extractId( getAuthentication() );
    }

    @Transactional( propagation = Propagation.REQUIRED )
    public User getLoginUser() {
        return entityManager.getReference( User.class, getCurrentUserId() );
    }

    @Transactional( propagation = Propagation.REQUIRED, readOnly = true )
    public User getLoginUserForReadOnly() {
        return entityManager.getReference( User.class, getCurrentUserId() );
    }
}


