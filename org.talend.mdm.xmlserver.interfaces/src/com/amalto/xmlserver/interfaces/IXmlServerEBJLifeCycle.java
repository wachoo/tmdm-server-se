// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.xmlserver.interfaces;


/**
 * This interface allows the Database wrapper to listen to the corresponding EJB Session Objects events
 * @author bgrieder
 *
 */
public interface IXmlServerEBJLifeCycle {

    public void doCreate() throws XmlServerException;
    public void doPostCreate() throws XmlServerException;
    public void doRemove() throws XmlServerException;
    public void doActivate() throws XmlServerException;
    public void doPassivate() throws XmlServerException;
	
}
