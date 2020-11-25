package com.backend.boilerplate.security.autoconfigure;

import com.backend.boilerplate.security.AuthenticationFacade;
import com.backend.boilerplate.security.IAuthenticationFacade;
import com.backend.boilerplate.security.SpAccessDeniedHandler;
import com.backend.boilerplate.security.SpAuthenticationEntryPoint;
import com.backend.boilerplate.security.CompositeHandlerMapping;
import com.backend.boilerplate.security.filter.CorsFilter;
import com.backend.boilerplate.security.filter.AuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Objects;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration {
    @Autowired
    private Environment env;

    @Bean("corsFilter")
    public OncePerRequestFilter corsFilter() {
        log.debug("configuring CorsFilter");
        String allowedUrls = env.getProperty("cors.allowed-origins");
        Objects.requireNonNull(allowedUrls, "property {cors.allowed-origins} is not configured");
        return new CorsFilter(allowedUrls);
    }

    @Bean("AccessDeniedHandler")
    public org.springframework.security.web.access.AccessDeniedHandler accessDeniedHandler() {
        log.debug("configuring AccessDeniedHandler");
        return new SpAccessDeniedHandler();
    }

    @Bean("AuthenticationEntryPoint")
    public org.springframework.security.web.AuthenticationEntryPoint authenticationEntryPoint() {
        log.debug("configuring AuthenticationEntryPoint");
        return new SpAuthenticationEntryPoint();
    }

    @Bean("CompositeHandlerMapping")
    public HandlerMapping compositeHandlerMapping(ListableBeanFactory beanFactory) {
        log.debug("configuring CompositeHandlerMapping");
        return new CompositeHandlerMapping(beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public IAuthenticationFacade authenticationFacade() {
        log.debug("configuring AuthenticationFacade");
        return new AuthenticationFacade();
    }

    @Primary
    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter(
            @Qualifier("AccessDeniedHandler") org.springframework.security.web.access.AccessDeniedHandler accessDeniedHandler,
            @Qualifier("AuthenticationEntryPoint") org.springframework.security.web.AuthenticationEntryPoint authenticationEntryPoint,
            @Qualifier("CompositeHandlerMapping") HandlerMapping compositeHandlerMapping,
            IAuthenticationFacade authenticationFacade,
            @Qualifier("corsFilter") OncePerRequestFilter corsFilter
    ) {
        log.debug("configuring WebSecurityConfigurerAdapter");
        return new WebSecurityConfigurerAdapter() {
            @Override
            public void configure(WebSecurity web) throws Exception {
                web.ignoring()
                        .antMatchers(
                                "/api/v1/**/actuator/health"
                        );
            }

            @Override
            protected void configure(HttpSecurity httpSecurity) throws Exception {
                /*httpSecurity.csrf().disable()
                        .authorizeRequests()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .exceptionHandling()
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .and()
                        .addFilterBefore(
                                new JwtAuthorizingFilter(
                                        jwtAuthorizingFilterProperties,
                                        additionalAuthoritiesMapper
                                ),
                                BasicAuthenticationFilter.class)
                        .addFilterAt(corsFilter, org.springframework.web.filter.CorsFilter.class)
                        .addFilterAfter(
                                new AuthorizationFilter(
                                        authenticationFacade,
                                        accessDeniedHandler,
                                        authenticationEntryPoint,
                                        compositeHandlerMapping
                                ),
                                JwtAuthorizingFilter.class);*/
                httpSecurity.csrf().disable()
                        .authorizeRequests()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .exceptionHandling()
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .and()
                        .addFilterAt(corsFilter, org.springframework.web.filter.CorsFilter.class)
                        .addFilterAfter(
                                new AuthorizationFilter(
                                        authenticationFacade,
                                        accessDeniedHandler,
                                        authenticationEntryPoint,
                                        compositeHandlerMapping
                                ),
                                BasicAuthenticationFilter.class);
            }
        };
    }
}
