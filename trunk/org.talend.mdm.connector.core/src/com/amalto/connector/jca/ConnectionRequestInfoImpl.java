/*
 * Created on 27 nov. 2004
 *
 */
package com.amalto.connector.jca;

import javax.resource.spi.ConnectionRequestInfo;

/**
 * ConnectionRequestInfoImpl.java
 * 
 * @author bgrieder
 */
public class ConnectionRequestInfoImpl implements ConnectionRequestInfo {
    
    private String username = null;
    private String password = null;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String userName) {
        this.username = userName;
    }

}
