package com.lotreetea.backend.resource;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


@RestController
public class AuthResource {
    @GetMapping("/api/whoami")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
    if (authentication instanceof OAuth2AuthenticationToken token &&
        token.getPrincipal() instanceof DefaultOAuth2User user) { //instanceof means only if this thing exists. so like it is a one line check for if i am authenticated and if it is a default2user

        Map<String, Object> attrs = user.getAttributes();

        String email = String.valueOf(attrs.getOrDefault("email", ""));
        String firstName = String.valueOf(attrs.getOrDefault("given_name", ""));
        String lastName = String.valueOf(attrs.getOrDefault("family_name", ""));


        return Map.of(
            "email", email,
            "firstName", firstName,
            "lastName", lastName,
            "roles", token.getAuthorities().stream()
                         .map(GrantedAuthority::getAuthority)
                         .toList()
        );
    }

    return Map.of(
        "email", "",
        "firstName", "",
        "lastName", "",
        "roles", List.of("ROLE_CUSTOMER")
    );
    }
}
