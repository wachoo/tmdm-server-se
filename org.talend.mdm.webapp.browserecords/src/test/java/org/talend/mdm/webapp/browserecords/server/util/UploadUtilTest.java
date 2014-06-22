// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.util;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.server.util.UploadUtil;

@SuppressWarnings("nls")
public class UploadUtilTest  extends TestCase {
    
    String header = "Id:true@Name:false:@Age:true"; //$NON-NLS-1$
    String mandatoryField = "Id@Name@Age"; //$NON-NLS-1$
    
    public void testGetVisibleMap(){
        Map<String,Boolean> headerMap = UploadUtil.getVisibleMap(header);
        Boolean visible1 = headerMap.get("Id"); //$NON-NLS-1$
        assertNotNull(visible1);
        assertTrue(visible1);
        Boolean visible2 = headerMap.get("Name"); //$NON-NLS-1$
        assertNotNull(visible2);
        assertFalse(visible2);
        Boolean visible3 = headerMap.get("Age"); //$NON-NLS-1$
        assertNotNull(visible3);
        assertTrue(visible3);
    }
    
    public void testChechMandatoryField(){
        Set<String>  mandatorySet = UploadUtil.chechMandatoryField(mandatoryField, UploadUtil.getVisibleMap(header).keySet());
        assertEquals(mandatorySet.size(), 0);
    }
    
    public void testGetFieldName(){
        ;
        assertEquals(UploadUtil.getFieldName("Name:true"), "Name"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  
    public void testGetFieldVisible(){
        assertTrue(UploadUtil.getFieldVisible("Name:true")); //$NON-NLS-1$
    }    
    
    public void testGetDefaultHeader(String header){
        UploadUtil.getDefaultHeader(header);
        String[] headerArray = UploadUtil.getDefaultHeader(header);        
        assertEquals(headerArray.length, 3);
        assertEquals(headerArray[0], "Id"); //$NON-NLS-1$
        assertEquals(headerArray[1], "Name"); //$NON-NLS-1$
        assertEquals(headerArray[2], "Age"); //$NON-NLS-1$
    }
    
    public void testGetRootCause(){
        RemoteException romoteException = new RemoteException("RemoteException Cause"); //$NON-NLS-1$
        ServletException servletException = new ServletException("ServletException Cause",romoteException); //$NON-NLS-1$
        Exception exception = new Exception("Exception Cause",servletException); //$NON-NLS-1$
        assertEquals(UploadUtil.getRootCause(exception),"RemoteException Cause"); //$NON-NLS-1$
    }
        
    public void testIsViewableXpathValid(){
        String viewableXpath = "Product/Id@Product/Name@Product/Description@Product/Price@Product/Family@ProductFamily/Name";
        String concept = "Product";
        assertFalse(UploadUtil.isViewableXpathValid(viewableXpath, concept));
        
        concept = "ProductFamily";
        assertFalse(UploadUtil.isViewableXpathValid(viewableXpath, concept));
        
        viewableXpath = "Product/Id@Product/Name@Product/Description@Product/Price@Product/Family";
        concept = "Product";
        assertTrue(UploadUtil.isViewableXpathValid(viewableXpath, concept));
    }
}
