package com.touhed.app.security.filter;

import com.touhed.app.security.JwtUtil;
import com.touhed.app.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain ) throws ServletException, IOException {

        final String authHeader = request.getHeader( "Authorization" );

        if ( authHeader == null || !authHeader.startsWith( "Bearer " ) ) {
            chain.doFilter( request, response );
            return;
        }

        final String token = authHeader.substring( 7 );
        UserDetails userDetails = jwtUtil.extractUser( token );

        if ( userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null ) {

            if ( jwtUtil.validateToken( token ) ) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities() );

                authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
                SecurityContextHolder.getContext().setAuthentication( authenticationToken );
            }
        }
        chain.doFilter( request, response );
    }
}
