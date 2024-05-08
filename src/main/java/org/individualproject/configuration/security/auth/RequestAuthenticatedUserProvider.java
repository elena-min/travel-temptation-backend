package org.individualproject.configuration.security.auth;

import org.individualproject.configuration.security.token.AccessToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class RequestAuthenticatedUserProvider {

    @Bean
    //This method retrieve the authenticated user information from the current request
    public AccessToken getAuthenticatedUserInRequest(){
        //First it checks the security context, which holds the security info
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext == null){
            return null;
        }

        //Then it retrieves the user object info
        final Authentication authentication = securityContext.getAuthentication();
        if(authentication == null){
            return null;
        }

        //checks if the details of the authentication are an instance of AccessToken
        final Object details = authentication.getDetails();
        if(!(details instanceof AccessToken)){
            return null;
        }

        return (AccessToken) authentication.getDetails();
    }
}
