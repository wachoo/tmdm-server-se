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

import static com.amalto.core.query.user.UserQueryBuilder.from;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String AUTHENTICATED_ROLE = "authenticated"; //$NON-NLS-1$

    @Autowired
    protected void configureMDMAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        // Create users based on information in database
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        ComplexTypeMetadata userType = systemStorage.getMetadataRepository().getComplexType("User"); //$NON-NLS-1$
        UserQueryBuilder qb = from(userType);
        qb.start(0); //TODO
        qb.limit(2);
        systemStorage.begin();
        List<UserDetails> userDetails = new ArrayList<UserDetails>();
        try {
            StorageResults users = systemStorage.fetch(qb.getSelect());
            for (DataRecord user : users) {
                String userName = String.valueOf(user.get("username")); //$NON-NLS-1$
                String password = String.valueOf(user.get("password")); //$NON-NLS-1$
                @SuppressWarnings("unchecked")
				List<String> roles = (List<String>) ((DataRecord) user.get("roles")).get("role"); //$NON-NLS-1$  //$NON-NLS-2$
                userDetails.add(new MDMUserDetails(userName, password, roles));
            }
            systemStorage.commit();
        } catch (Exception e) {
            systemStorage.rollback();
            throw new RuntimeException("Could not get list of users.", e);
        }
        // Provides user details and indicates password is MD5-encrypted
        auth.userDetailsService(new InMemoryUserDetailsManager(userDetails)).passwordEncoder(new Md5PasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Allow access on "auth/**" pages for authentication
        http.authorizeRequests().antMatchers("/auth/**") //$NON-NLS-1
                .permitAll();
        // All request must have 'authenticated' as role
        http.authorizeRequests().antMatchers("/**") //$NON-NLS-1
                .authenticated().and().formLogin().loginPage("/auth/login.jsp") //$NON-NLS-1
                .usernameParameter("j_username") //$NON-NLS-1
                .passwordParameter("j_password") //$NON-NLS-1
                .defaultSuccessUrl("/ui") //$NON-NLS-1$
                .and().logout().logoutUrl("/auth/logout") //$NON-NLS-1
                .logoutSuccessUrl("/"); //$NON-NLS-1
        // Services access requires HTTP basic authentication (if not already authenticated).
        // TODO Match "/" is applied *before* making this not used
        http.authorizeRequests().antMatchers("/secure/services/**").hasAuthority(AUTHENTICATED_ROLE).and().httpBasic()
                .realmName("Talend MDM");
        // X-Frame-Options to SAMEORIGIN.
        http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));
        // TODO For tests only: disable CSRF to make POST requests work
        http.csrf().disable();
    }
}
