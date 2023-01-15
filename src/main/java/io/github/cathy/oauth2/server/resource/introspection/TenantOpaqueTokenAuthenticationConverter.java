package io.github.cathy.oauth2.server.resource.introspection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongan.multitenancy.security.core.userdetails.AttributeGrantedAuthority;

import io.github.cathy.oauth2.server.resource.authentication.TenantBearerTokenAuthentication;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

public class TenantOpaqueTokenAuthenticationConverter implements OpaqueTokenAuthenticationConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    @SneakyThrows
    public Authentication convert(String introspectedToken, OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
        Instant iat = authenticatedPrincipal.getAttribute(OAuth2TokenIntrospectionClaimNames.IAT);
        Instant exp = authenticatedPrincipal.getAttribute(OAuth2TokenIntrospectionClaimNames.EXP);
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, introspectedToken,
            iat, exp);

        Set<GrantedAuthority> userAuthorities = new HashSet<>();
        Object authorities = authenticatedPrincipal.getAttribute("authorities");
        if (authorities instanceof Collection) {
            for (Map<String, Object> authority : (Collection<Map<String, Object>>) authorities) {
                String permission = String.valueOf(authority.get("authority"));
                Map<String, Object> attributes = MAPPER.readValue(MAPPER.writeValueAsString(authority.get("attributes")),
                    new TypeReference<Map<String, Object>>() {
                    });
                userAuthorities.add(new AttributeGrantedAuthority(permission, attributes));
            }
        }

        return new TenantBearerTokenAuthentication(authenticatedPrincipal, accessToken,
            CollectionUtils.isEmpty(userAuthorities) ? authenticatedPrincipal.getAuthorities() : userAuthorities);
    }

}
