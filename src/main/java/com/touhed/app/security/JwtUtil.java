package com.touhed.app.security;

import com.touhed.app.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class JwtUtil {

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor( Decoders.BASE64.decode( secretKey ) );
    }

    public String generateToken( CustomUserDetails customUserDetails, Long expirationTimeInMilliseconds ) {

        Date now = new Date();
        Date expiryDate = new Date( now.getTime() + expirationTimeInMilliseconds );

        Map<String, Object> claims = new HashMap<>();
        claims.put( "email", customUserDetails.getUser().getEmail() );
        claims.put( "role", customUserDetails.getUser().getRole() );
        claims.put( "name", customUserDetails.getUser().getName() );
        claims.put( "id", customUserDetails.getUser().getId() );

        return Jwts.builder()
                .claims( claims )
                .issuedAt( now )
                .expiration( expiryDate )
                .signWith( getSignKey(), Jwts.SIG.HS512 )
                .compact();
    }

    public String generateAccessToken( CustomUserDetails customUserDetails ) {
        return generateToken( customUserDetails, jwtExpiration );
    }

    public String generateRefreshToken( CustomUserDetails customUserDetails ) {
        return generateToken( customUserDetails, refreshExpiration );
    }

    public CustomUserDetails extractUser( String token ) {
        Claims claims = Jwts.parser()
                .verifyWith( getSignKey() )
                .build()
                .parseSignedClaims( token )
                .getPayload();

        String email = (String) claims.get( "email" );
        return userDetailsService.loadUserByUsername( email ) ;
    }

    public boolean validateToken( String token ) {
        try {
            Jwts.parser().verifyWith( getSignKey() ).build().parseSignedClaims( token );
            return true;
        } catch ( ExpiredJwtException e ) {
            System.out.println( "JWT expired at: " + e.getClaims().getExpiration() );
            return false;
        } catch (JwtException | IllegalArgumentException e ) {
            System.out.println( "Invalid JWT: " + e.getMessage() );
            return false;
        }
    }
}