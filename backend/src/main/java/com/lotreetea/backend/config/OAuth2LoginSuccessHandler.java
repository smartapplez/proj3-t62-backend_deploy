package com.lotreetea.backend.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.lotreetea.backend.model.RegistrationSource;
import com.lotreetea.backend.model.UserRole;
import com.lotreetea.backend.service.UserService;
import com.lotreetea.backend.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();

            String email = attributes.getOrDefault("email", "").toString();
            String firstName = attributes.getOrDefault("given_name", "").toString();
            String lastName = attributes.getOrDefault("family_name", "").toString(); // May be missing

            log.debug("OAuth2 attributes: {}\n", attributes);
            log.debug("OAuth login success - email: {}, first name: {}, last name: {}", email, firstName, lastName);

            Optional<User> optionalUser = userService.findUserByEmail(email);

            User user;
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                user = new User();
                user.setRole(UserRole.ROLE_CUSTOMER);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName.isEmpty() ? null : lastName); // may be null if missing
                user.setSource(RegistrationSource.GOOGLE);
                userService.createUser(user);
            }

            DefaultOAuth2User newUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                attributes,
                "email"
            );

            log.debug("User Role: {}", user.getRole().toString());

            Authentication securityAuth = new OAuth2AuthenticationToken(
                newUser,
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
            );

            SecurityContextHolder.getContext().setAuthentication(securityAuth);
        }

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(frontendUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}