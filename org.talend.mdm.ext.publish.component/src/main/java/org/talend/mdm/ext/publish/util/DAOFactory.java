/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package org.talend.mdm.ext.publish.util;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;

public class DAOFactory {

    /** unique instance */
    private static DAOFactory sInstance = null;

    private XmlServerSLWrapperLocal server;

    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * Private constuctor
     */
    private DAOFactory() {
        super();
        try {
            server = ((XmlServerSLWrapperLocalHome) new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
        } catch (Exception e) {
            String err = "Unable to access the XML Server wrapper";
            logger.error(err, e);
        }
    }

    /**
     * Get the unique instance of this class.
     */
    public static synchronized DAOFactory getUniqueInstance() {

        if (sInstance == null) {
            sInstance = new DAOFactory();
        }

        return sInstance;

    }

    public DomainObjectsDAO getDomainObjectDAO() {

        return new DomainObjectsDAOImpl(this.server);

    }

    public PicturesDAO getPicturesDAO(String hostURL) {

        return new PicturesDAOFSImpl(hostURL);

    }

}
