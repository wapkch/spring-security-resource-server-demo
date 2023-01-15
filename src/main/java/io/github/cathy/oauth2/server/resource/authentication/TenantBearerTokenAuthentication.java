package io.github.cathy.oauth2.server.resource.authentication;

import com.zhongan.multitenancy.context.DefaultTenantContext;
import com.zhongan.multitenancy.context.TenantContext;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

@Getter
@Setter
public class TenantBearerTokenAuthentication extends BearerTokenAuthentication {

    private TenantContext tenantContext;

    private Long userId;

    public TenantBearerTokenAuthentication(OAuth2AuthenticatedPrincipal principal, OAuth2AccessToken credentials,
                                           Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);

        Optional.ofNullable(principal.getAttribute("user_id"))
            .ifPresent(id -> userId = Long.valueOf(String.valueOf(id)));

        DefaultTenantContext context = new DefaultTenantContext(principal.getAttribute("tenant"));
        Object trace = principal.getAttribute("trace");
        if (trace instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> attributes = (Map<String, String>) trace;
            context.setAttributes(attributes);
        }
        tenantContext = context;
    }

}
