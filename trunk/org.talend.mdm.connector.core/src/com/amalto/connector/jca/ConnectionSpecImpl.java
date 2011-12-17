/*
 * Created on 27 nov. 2004
 *
 */
package com.amalto.connector.jca;

import javax.resource.cci.ConnectionSpec;

/**
 * comx.connector.bridge
 * ConnectionSpecImpl.java
 * 
 * @author bgrieder
 */
public class ConnectionSpecImpl implements ConnectionSpec {
    
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
