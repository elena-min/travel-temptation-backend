package org.individualproject.configuration.security.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode
@Getter
public class AccessToken {
    private final String subject;
    private final Long userID;
    private final Set<String> roles;

    public AccessToken(String subject, Long id, Collection<String> roles){
        this.subject = subject;
        this.userID = id;
        this.roles = roles != null? Set.copyOf(roles) : Collections.emptySet();
    }

    public boolean hasRole(String roleName)
    {
        return  this.roles.contains(roleName);
    }


}
