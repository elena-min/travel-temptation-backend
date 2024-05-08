package org.individualproject.configuration.security.token.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.configuration.security.token.AccessTokenDecoder;
import org.individualproject.configuration.security.token.AccessTokenEncoder;
import org.individualproject.configuration.security.token.exception.InvalidAccessTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AccessTokenEncoderDecoderImpl implements AccessTokenDecoder, AccessTokenEncoder {

    private final Key key;

    public AccessTokenEncoderDecoderImpl(@Value("") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try{
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessTokenEncoded);
            Claims claims = jwt.getBody();

            List<String> roles = claims.get("roles", List.class);
            Long userID = claims.get("userID", Long.class);
            return  new AccessToken(claims.getSubject(), userID, roles);
        }
        catch (JwtException e){
            throw  new InvalidAccessTokenException(e.getMessage());
        }
    }

    @Override
    public String encode(AccessToken accessToken) {
        Map<String, Object> claimsMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(accessToken.getRoles())){
            claimsMap.put("roles", accessToken.getRoles());
        }
        if(accessToken.getUserID() != null){
            claimsMap.put("userID", accessToken.getUserID());
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(accessToken.getSubject())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.MINUTES)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }
}
