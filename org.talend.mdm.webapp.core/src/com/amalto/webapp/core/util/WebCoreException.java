// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

/**
 * DOC talend2 class global comment. Detailled comment
 */
public class WebCoreException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static int INFO = 1;

    public static int Error = 2;

    private String title;

    private boolean client;

    private int level = 2;

    public WebCoreException(String title, Throwable cause) {
        super(cause);
        this.title = title;
    }

    public WebCoreException(String title, String message, int level) {
        super(message);
        this.title = title;
        this.level = level;
    }

    /**
     * Getter for title.
     * 
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for client.
     * 
     * @return the client
     */
    public boolean isClient() {
        return this.client;
    }

    /**
     * Sets the client.
     * 
     * @param client the client to set
     */
    public void setClient(boolean client) {
        this.client = client;
    }

    /**
     * Getter for level.
     * 
     * @return the level
     */
    public int getLevel() {
        return this.level;
    }

}
