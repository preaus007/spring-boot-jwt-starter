package com.touhed.app.security.service;

import com.touhed.app.security.CustomUserDetails;
import com.touhed.app.user.model.User;
import com.touhed.app.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername( String email ) throws UsernameNotFoundException {
        User user = userRepository.findByEmail( email )
                .orElseThrow( () -> new UsernameNotFoundException( "User not found with email: " + email ) );

        return new CustomUserDetails( user );
    }
}
