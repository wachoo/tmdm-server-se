/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server.security;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class);

    private static final String PRIVATE_INTERNAL_USER = "MDMInternalUser"; //$NON-NLS-1$

    public static final String AUTHENTICATED_ROLE = "authenticated"; //$NON-NLS-1$

    public static final String ADMINISTRATION_ROLE = "administration"; //$NON-NLS-1$

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Allow access on "auth/**" pages for authentication
        http.authorizeRequests().antMatchers("/auth/**") //$NON-NLS-1$
                .permitAll();
        // All request must have 'authenticated' as role
        http.authorizeRequests().antMatchers("/**") //$NON-NLS-1$
                .authenticated().and().formLogin().loginPage("/auth/login.jsp") //$NON-NLS-1$
                .usernameParameter("j_username") //$NON-NLS-1$
                .passwordParameter("j_password") //$NON-NLS-1$
                .defaultSuccessUrl("/ui") //$NON-NLS-1$
                .and().logout().logoutUrl("/auth/logout") //$NON-NLS-1$
                .logoutSuccessUrl("/"); //$NON-NLS-1$
        // Services access requires HTTP basic authentication (if not already
        // authenticated).
        // TODO Match "/" is applied *before* making this not used
        http.authorizeRequests().antMatchers("/secure/services/**").hasAuthority(AUTHENTICATED_ROLE).and().httpBasic() //$NON-NLS-1$
                .realmName("Talend MDM"); //$NON-NLS-1$
        // X-Frame-Options to SAMEORIGIN.
        http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));
        // TODO For tests only: disable CSRF to make POST requests work
        http.csrf().disable();
    }

    public static void invokeSynchronousPrivateInternal(Runnable runnable) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null) {
            LOGGER.warn("Private internal invocation with non-null authentication"); //$NON-NLS-1$
            runnable.run();
        } else {
            context = SecurityContextHolder.createEmptyContext();
            List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList(ADMINISTRATION_ROLE, AUTHENTICATED_ROLE);
            authentication = new UsernamePasswordAuthenticationToken(PRIVATE_INTERNAL_USER, "", roles); //$NON-NLS-1$
            context.setAuthentication(authentication);
            SyncTaskExecutor delegateExecutor = new SyncTaskExecutor();
            DelegatingSecurityContextExecutor executor = new DelegatingSecurityContextExecutor(delegateExecutor, context);
            executor.execute(runnable);
        }
    }
}
