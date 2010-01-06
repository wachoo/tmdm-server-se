/**
 * XtentisService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package urn_com_amalto_xtentis_webservice;

public interface XtentisService extends javax.xml.rpc.Service {
    public java.lang.String getXtentisPortAddress();

    public urn_com_amalto_xtentis_webservice.XtentisPort getXtentisPort() throws javax.xml.rpc.ServiceException;

    public urn_com_amalto_xtentis_webservice.XtentisPort getXtentisPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
