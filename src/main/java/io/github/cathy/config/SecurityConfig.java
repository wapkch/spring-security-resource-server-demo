package io.github.cathy.config;

import io.github.cathy.oauth2.server.resource.introspection.TenantOpaqueTokenAuthenticationConverter;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain opaqueTokenSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        http.oauth2ResourceServer(resourceServer -> {
            resourceServer.opaqueToken().authenticationConverter(opaqueTokenAuthenticationConverter());
        });
        return http.build();
    }

    @Bean
    public TenantOpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter() {
        return new TenantOpaqueTokenAuthenticationConverter();
    }

    @Bean
    public MethodSecurityExpressionHandler defaultExpressionHandler(
        ObjectProvider<GrantedAuthorityDefaults> defaultsProvider, ApplicationContext context) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        defaultsProvider.ifAvailable((d) -> handler.setDefaultRolePrefix(d.getRolePrefix()));
        handler.setApplicationContext(context);
        return handler;
    }

}
