// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jboss.security.Base64Encoder;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.DroppedItemPOJOPK;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.TransformerCtrlBean;
import com.amalto.core.ejb.TransformerPOJO;
import com.amalto.core.ejb.TransformerPOJOPK;
import com.amalto.core.ejb.UpdateReportItemPOJO;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlUtil;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJOPK;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJOPK;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.UpdateReportItem;
import com.amalto.core.util.Util;
import com.amalto.core.util.Version;
import com.amalto.core.util.WhereConditionForcePivotFilter;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.util.webservices.*;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.sun.org.apache.xpath.internal.XPathAPI;

public abstract class IXtentisRMIPort implements XtentisPort {

    private static Logger LOG = Logger.getLogger(IXtentisRMIPort.class);

    /***************************************************************************
     * 
     * S E R V I C E S
     * 
     * **************************************************************************/

    /***************************************************************************
     * Components Management
     * **************************************************************************/

    public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException {
        try {
            if (WSComponent.DataManager.equals(wsGetComponentVersion.getComponent())) {
                Version version = Version.getVersion(this.getClass());
                return new WSVersion(version.getMajor(), version.getMinor(), version.getRevision(), version.getBuild(),
                        version.getDescription(), version.getDate());
            }
            throw new RemoteException("Version information is not available yet for "
                    + wsGetComponentVersion.getComponent().getValue() + " components");
        } catch (RemoteException e) {
            throw (e);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Initialize
     * **************************************************************************/

    public WSInt initMDM(WSInitData initData) throws RemoteException {
        throw new RemoteException("initMDM not implemented as RMI call");
    }

    /***************************************************************************
     * Logout
     * **************************************************************************/

    public WSString logout(WSLogout wsLogout) throws RemoteException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("logout() ");
        String msg = "OK";
        try {
            ILocalUser user = LocalUser.getLocalUser();
            user.logout();
        } catch (Exception e) {
            String err = "Error trying to logout";
            org.apache.log4j.Logger.getLogger(this.getClass()).warn(err, e);
            msg = e.getMessage();
        }
        return new WSString(msg);
    }

    /***************************************************************************
     * Data Model
     * **************************************************************************/

    public WSDataModel getDataModel(WSGetDataModel wsDataModelget) throws RemoteException {
        try {
            return XConverter.VO2WS(com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
                    new DataModelPOJOPK(wsDataModelget.getWsDataModelPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp) throws RemoteException {
        try {

            WSDataModelPKArray array = new WSDataModelPKArray();
            Collection<DataModelPOJOPK> pks = com.amalto.core.util.Util.getDataModelCtrlLocal()
                    .getDataModelPKs(regexp.getRegex());
            ArrayList<WSDataModelPK> list = new ArrayList<WSDataModelPK>();
            for (Iterator iter = pks.iterator(); iter.hasNext();) {
                DataModelPOJOPK pk = (DataModelPOJOPK) iter.next();
                WSDataModelPK dmpk = new WSDataModelPK(pk.getUniqueId());
                list.add(dmpk);
            }
            array.setWsDataModelPKs(list.toArray(new WSDataModelPK[list.size()]));
            return array;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel) throws RemoteException {
        try {
            return new WSDataModelPK(com.amalto.core.util.Util.getDataModelCtrlLocal()
                    .removeDataModel(new DataModelPOJOPK(wsDeleteDataModel.getWsDataModelPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel) throws RemoteException {
        try {
            return new WSBoolean((Util.getDataModelCtrlLocal().existsDataModel(
                    new DataModelPOJOPK(wsExistsDataModel.getWsDataModelPK().getPk())) != null));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel) throws RemoteException {
        try {
            return new WSDataModelPK(com.amalto.core.util.Util.getDataModelCtrlLocal()
                    .putDataModel(XConverter.WS2VO(wsDataModel.getWsDataModel())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
        try {
            return new WSString(com.amalto.core.util.Util.getDataModelCtrlLocal().checkSchema(wsSchema.getSchema()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSCheckServiceConfigResponse checkServiceConfig(WSCheckServiceConfigRequest request) throws RemoteException {
        WSCheckServiceConfigResponse serviceConfigResponse = new WSCheckServiceConfigResponse();
        try {
            Object service = Util.retrieveComponent(null, request.getJndiName());
            Boolean checkResult = (Boolean) Util.getMethod(service, "checkConfigure").invoke(service, new Object[] {});
            serviceConfigResponse.setCheckResult(checkResult);

        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

        return serviceConfigResponse;
    }

    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept) throws RemoteException {
        WSBusinessConcept bc = wsPutBusinessConcept.getBusinessConcept();
        try {
            String s = "<xsd:element name=" + bc.getName() + " type=" + bc.getBusinessTemplate() + ">" + "	<xsd:annotation>";
            WSI18NString[] labels = bc.getWsLabel();
            for (int i = 0; i < labels.length; i++) {
                s += "<xsd:appinfo source=\"" + labels[i].getLanguage().getValue() + "\">" + labels[i].getLabel()
                        + "</xsd:appinfo>";
            }
            WSI18NString[] docs = bc.getWsDescription();
            for (int i = 0; i < docs.length; i++) {
                s += "<xsd:documentation xml:lang=\"" + docs[i].getLanguage().getValue() + "\">" + docs[i].getLabel()
                        + "</xsd:documentation>";
            }
            s += "	</xsd:annotation>" + "	<xsd:unique name=\"" + bc.getName() + "\">" + "		<xsd:selector xpath=\""
                    + bc.getWsUniqueKey().getSelectorpath() + "\"/>";
            for (int i = 0; i < bc.getWsUniqueKey().getFieldpath().length; i++) {
                s += "<xsd:field xpath=\"" + bc.getWsUniqueKey().getFieldpath()[i] + "\"/>";
            }
            s += "	</xsd:unique>" + "</xsd:element>";
            return new WSString(Util.getDataModelCtrlLocal().putBusinessConceptSchema(
                    new DataModelPOJOPK(wsPutBusinessConcept.getWsDataModelPK().getPk()), s));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws RemoteException {
        try {
            return new WSString(Util.getDataModelCtrlLocal().putBusinessConceptSchema(
                    new DataModelPOJOPK(wsPutBusinessConceptSchema.getWsDataModelPK().getPk()),
                    wsPutBusinessConceptSchema.getBusinessConceptSchema()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString deleteBusinessConcept(WSDeleteBusinessConcept wsDeleteBusinessConcept) throws RemoteException {
        try {
            return new WSString(com.amalto.core.util.Util.getDataModelCtrlLocal().deleteBusinessConcept(
                    new DataModelPOJOPK(wsDeleteBusinessConcept.getWsDataModelPK().getPk()),
                    wsDeleteBusinessConcept.getBusinessConceptName()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray getBusinessConcepts(WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
        try {
            return new WSStringArray(com.amalto.core.util.Util.getDataModelCtrlLocal().getAllBusinessConceptsNames(
                    new DataModelPOJOPK(wsGetBusinessConcepts.getWsDataModelPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
        try {
            String schema = Util.getDataModelCtrlLocal()
                    .getDataModel(new DataModelPOJOPK(wsGetBusinessConceptKey.getWsDataModelPK().getPk())).getSchema();

            XSDKey xsdKey = Util.getBusinessConceptKey(Util.parse(schema), wsGetBusinessConceptKey.getConcept());
            return new WSConceptKey(xsdKey.getSelector(), xsdKey.getFields());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * DataCluster
     * **************************************************************************/

    public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet) throws RemoteException {
        try {
            return XConverter.VO2WS(com.amalto.core.util.Util.getDataClusterCtrlLocal().getDataCluster(
                    new DataClusterPOJOPK(wsDataClusterGet.getWsDataClusterPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster) throws RemoteException {
        try {
            return new WSBoolean(com.amalto.core.util.Util.getDataClusterCtrlLocal().existsDataCluster(
                    new DataClusterPOJOPK(wsExistsDataCluster.getWsDataClusterPK().getPk())) != null);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDataCluster) throws RemoteException {
        try {
            String revisionId = wsExistsDataCluster.getRevisionID();
            String clusterName = wsExistsDataCluster.getName();
            boolean exist = Util.getXmlServerCtrlLocal().existCluster(revisionId, clusterName);
            return new WSBoolean(exist);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp) throws RemoteException {
        try {
            WSDataClusterPKArray array = new WSDataClusterPKArray();
            Collection<DataClusterPOJOPK> pks = com.amalto.core.util.Util.getDataClusterCtrlLocal().getDataClusterPKs(
                    regexp.getRegex());
            ArrayList<WSDataClusterPK> list = new ArrayList<WSDataClusterPK>();
            for (Iterator iter = pks.iterator(); iter.hasNext();) {
                DataClusterPOJOPK pk = (DataClusterPOJOPK) iter.next();
                list.add(new WSDataClusterPK(pk.getUniqueId()));
            }
            array.setWsDataClusterPKs(list.toArray(new WSDataClusterPK[list.size()]));
            return array;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
        try {
            return new WSDataClusterPK(com.amalto.core.util.Util.getDataClusterCtrlLocal()
                    .removeDataCluster(new DataClusterPOJOPK(wsDeleteDataCluster.getWsDataClusterPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster) throws RemoteException {
        try {
            return new WSDataClusterPK(com.amalto.core.util.Util.getDataClusterCtrlLocal()
                    .putDataCluster(XConverter.WS2VO(wsDataCluster.getWsDataCluster())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster) throws RemoteException {
        try {
            Util.getXmlServerCtrlLocal().createCluster(wsDataCluster.getRevisionID(), wsDataCluster.getName());
            DataClusterPOJO pojo = new DataClusterPOJO(wsDataCluster.getName(), "", "");
            ObjectPOJOPK pk = pojo.store(wsDataCluster.getRevisionID());
            return new WSBoolean(true);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
        try {
            Collection<String> results = com.amalto.core.util.Util.getItemCtrl2Local()
                    .getConceptsInDataCluster(new DataClusterPOJOPK(wsGetConceptsInDataCluster.getWsDataClusterPK().getPk()))
                    .keySet();

            return new WSStringArray(results.toArray(new String[results.size()]));

        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * View
     * **************************************************************************/

    public WSView getView(WSGetView wsViewGet) throws RemoteException {
        try {
            return XConverter.VO2WS(com.amalto.core.util.Util.getViewCtrlLocalHome().create()
                    .getView(new ViewPOJOPK(wsViewGet.getWsViewPK().getPk())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean existsView(WSExistsView wsExistsView) throws RemoteException {
        try {
            return new WSBoolean(com.amalto.core.util.Util.getViewCtrlLocalHome().create()
                    .existsView(new ViewPOJOPK(wsExistsView.getWsViewPK().getPk())) != null);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
        try {
            ArrayList l;
            String regex = regexp.getRegex() != null && !"".equals(regexp.getRegex()) && !"*".equals(regexp.getRegex()) ? regexp
                    .getRegex() : ".*";
            Collection list = com.amalto.core.util.Util.getViewCtrlLocalHome().create().getViewPKs(regex);
            l = new ArrayList();
            ViewPOJOPK pk;
            for (Iterator iter = list.iterator(); iter.hasNext(); l.add(new WSViewPK(pk.getIds()[0])))
                pk = (ViewPOJOPK) iter.next();
            return new WSViewPKArray((WSViewPK[]) l.toArray(new WSViewPK[l.size()]));

        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSViewPK deleteView(WSDeleteView wsDeleteView) throws RemoteException {
        try {
            return new WSViewPK(com.amalto.core.util.Util.getViewCtrlLocalHome().create()
                    .removeView(new ViewPOJOPK(wsDeleteView.getWsViewPK().getPk())).getIds()[0]);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSViewPK putView(WSPutView wsView) throws RemoteException {
        try {
            return new WSViewPK(com.amalto.core.util.Util.getViewCtrlLocalHome().create()
                    .putView(XConverter.WS2VO(wsView.getWsView())).getIds()[0]);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Search
     * **************************************************************************/

    public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
        try {

            Collection res = Util.getItemCtrl2Local().viewSearch(
                    new DataClusterPOJOPK(wsViewSearch.getWsDataClusterPK().getPk()),
                    new ViewPOJOPK(wsViewSearch.getWsViewPK().getPk()), XConverter.WS2VO(wsViewSearch.getWhereItem()),
                    wsViewSearch.getSpellTreshold(), wsViewSearch.getOrderBy(), wsViewSearch.getDirection(),
                    wsViewSearch.getSkip(), wsViewSearch.getMaxItems());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));

        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
        try {
            Collection res = com.amalto.core.util.Util.getItemCtrl2Local().xPathsSearch(
                    new DataClusterPOJOPK(wsXPathsSearch.getWsDataClusterPK().getPk()), wsXPathsSearch.getPivotPath(),
                    new ArrayList<String>(Arrays.asList(wsXPathsSearch.getViewablePaths().getStrings())),
                    XConverter.WS2VO(wsXPathsSearch.getWhereItem()), wsXPathsSearch.getSpellTreshold(),
                    wsXPathsSearch.getOrderBy(), wsXPathsSearch.getDirection(), wsXPathsSearch.getSkip(),
                    wsXPathsSearch.getMaxItems());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
            // } catch (com.amalto.webapp.util.XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
        try {
            Collection res = Util.getItemCtrl2Local().getItemsPivotIndex(
                    wsGetItemsPivotIndex.getClusterName(),
                    wsGetItemsPivotIndex.getMainPivotName(),
                    XConverter.WS2VO(wsGetItemsPivotIndex.getPivotWithKeys()),
                    wsGetItemsPivotIndex.getIndexPaths().getStrings(),
                    XConverter.WS2VO(wsGetItemsPivotIndex.getWhereItem()),
                    wsGetItemsPivotIndex.getPivotDirections() == null ? null : wsGetItemsPivotIndex.getPivotDirections()
                            .getStrings(),
                    wsGetItemsPivotIndex.getIndexDirections() == null ? null : wsGetItemsPivotIndex.getIndexDirections()
                            .getStrings(), wsGetItemsPivotIndex.getStart(), wsGetItemsPivotIndex.getLimit());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
        try {
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, wsGetItems.getConceptName());

            Collection res = com.amalto.core.util.Util.getItemCtrl2Local().getItems(
                    new DataClusterPOJOPK(wsGetItems.getWsDataClusterPK().getPk()), wsGetItems.getConceptName(),
                    XConverter.WS2VO(wsGetItems.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsGetItems.getSpellTreshold(), wsGetItems.getSkip(), wsGetItems.getMaxItems());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
            // } catch (com.amalto.webapp.util.XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
        try {

            String dataClusterName = wsGetItemPKsByCriteria.getWsDataClusterPK().getPk();

            // Check if user is allowed to read the cluster
            ILocalUser user = LocalUser.getLocalUser();
            boolean authorized = false;
            if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { //$NON-NLS-1$
                authorized = true;
            } else if (user.userCanRead(DataClusterPOJO.class, dataClusterName)) {
                authorized = true;
            }
            if (!authorized) {
                throw new RemoteException("Unauthorized read access on data cluster '" + dataClusterName + "' by user '"
                        + user.getUsername() + "'");
            }

            // If not all concepts are store in the same revision,
            // force the concept to be specified by the user.
            // It would be too demanding to get all the concepts in all revisions (?)
            // The meat of this method should be ported to ItemCtrlBean
            String revisionID = null;
            String conceptName = wsGetItemPKsByCriteria.getConceptName();
            if ( conceptName == null) {
                if (user.getUniverse().getItemsRevisionIDs().size() > 0) {
                    throw new RemoteException("User " + user.getUsername() + " is using items coming from multiple revisions."
                            + " In that particular case, the concept must be specified");
                } else {
                    revisionID = user.getUniverse().getDefaultItemRevisionID();
                }
            } else {
                revisionID = user.getUniverse().getConceptRevisionID(conceptName);
            }

            ItemPKCriteria criteria = new ItemPKCriteria();
            criteria.setRevisionId(revisionID);
            criteria.setClusterName(dataClusterName);
            criteria.setConceptName(conceptName);
            criteria.setContentKeywords(wsGetItemPKsByCriteria.getContentKeywords());
            criteria.setKeysKeywords(wsGetItemPKsByCriteria.getKeysKeywords());
            criteria.setCompoundKeyKeywords(false);
            criteria.setFromDate(wsGetItemPKsByCriteria.getFromDate());
            criteria.setToDate(wsGetItemPKsByCriteria.getToDate());
            criteria.setMaxItems(wsGetItemPKsByCriteria.getMaxItems());
            criteria.setSkip(wsGetItemPKsByCriteria.getSkip());
            criteria.setUseFTSearch(false);            
            List<String> results = com.amalto.core.util.Util.getItemCtrl2Local().getItemPKsByCriteria(criteria);

            WSItemPKsByCriteriaResponseResults[] res = new WSItemPKsByCriteriaResponseResults[results.size()];
            int i = 0;
            for (String result : results) {
                result = result.replaceAll("\\s*__h", "").replaceAll("h__\\s*", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                Element r = Util.parse(result).getDocumentElement();
                long t = new Long(Util.getFirstTextNode(r, "t")).longValue(); //$NON-NLS-1$
                String cn = Util.getFirstTextNode(r, "n"); //$NON-NLS-1$
                String taskId = Util.getFirstTextNode(r, "taskId"); //$NON-NLS-1$
                taskId = taskId == null ? "" : taskId; //$NON-NLS-1$
                String[] ids = Util.getTextNodes(r, "ids/i"); //$NON-NLS-1$
                res[i++] = new WSItemPKsByCriteriaResponseResults(t, new WSItemPK(wsGetItemPKsByCriteria.getWsDataClusterPK(),
                        cn, ids), taskId);
            }
            return new WSItemPKsByCriteriaResponse(res);

        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                "getItem() " + wsGetItem.getWsItemPK().getConceptName() + "    "
                        + Util.joinStrings(wsGetItem.getWsItemPK().getIds(), "."));
        try {
            ItemPOJO vo = com.amalto.core.util.Util.getItemCtrl2Local().getItem(
                    new ItemPOJOPK(new DataClusterPOJOPK(wsGetItem.getWsItemPK().getWsDataClusterPK().getPk()), wsGetItem
                            .getWsItemPK().getConceptName(), wsGetItem.getWsItemPK().getIds()));
            return new WSItem(wsGetItem.getWsItemPK().getWsDataClusterPK(), vo.getDataModelName(), vo.getDataModelRevision(),
                    wsGetItem.getWsItemPK().getConceptName(), wsGetItem.getWsItemPK().getIds(), vo.getInsertionTime(),
                    vo.getTaskId(), vo.getProjectionAsString());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
        try {
            return new WSBoolean((com.amalto.core.util.Util.getItemCtrl2Local().existsItem(
                    new ItemPOJOPK(new DataClusterPOJOPK(wsExistsItem.getWsItemPK().getWsDataClusterPK().getPk()), wsExistsItem
                            .getWsItemPK().getConceptName(), wsExistsItem.getWsItemPK().getIds())) != null));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
        try {
            Collection c = com.amalto.core.util.Util.getItemCtrl2Local().quickSearch(
                    new DataClusterPOJOPK(wsQuickSearch.getWsDataClusterPK().getPk()),
                    new ViewPOJOPK(wsQuickSearch.getWsViewPK().getPk()), wsQuickSearch.getSearchedValue(),
                    wsQuickSearch.isMatchAllWords(), wsQuickSearch.getSpellTreshold(), wsQuickSearch.getOrderBy(),
                    wsQuickSearch.getDirection(), wsQuickSearch.getMaxItems(), wsQuickSearch.getSkip());
            if (c == null)
                return null;
            return new WSStringArray((String[]) c.toArray(new String[c.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString getBusinessConceptValue(WSGetBusinessConceptValue wsGetBusinessConceptValue) throws RemoteException {
        try {
            ItemPOJO iv = com.amalto.core.util.Util.getItemCtrl2Local().getItem(
                    new ItemPOJOPK(new DataClusterPOJOPK(wsGetBusinessConceptValue.getWsDataClusterPK().getPk()),
                            wsGetBusinessConceptValue.getWsBusinessConceptPK().getConceptName(), wsGetBusinessConceptValue
                                    .getWsBusinessConceptPK().getIds()));
            return new WSString(itemAsString(iv));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
        try {

            Collection res = Util.getItemCtrl2Local().getFullPathValues(
                    new DataClusterPOJOPK(wsGetFullPathValues.getWsDataClusterPK().getPk()), wsGetFullPathValues.getFullPath(),
                    XConverter.WS2VO(wsGetFullPathValues.getWhereItem()), wsGetFullPathValues.getSpellThreshold(),
                    wsGetFullPathValues.getOrderBy(), wsGetFullPathValues.getDirection());

            if (res == null)
                return null;

            return new WSStringArray((String[]) res.toArray(new String[res.size()]));

        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    private String itemAsString(ItemPOJO iv) throws Exception {

        String item = "<businessconcept>" + "	<cluster>" + iv.getDataClusterPOJOPK().getUniqueId() + "</cluster>";
        String[] ids = iv.getItemIds();
        for (int i = 0; i < ids.length; i++) {
            item += "	<id>" + ids[i] + "</id>";
        }
        item += "	<lastmodifiedtime>" + iv.getInsertionTime() + "</lastmodifiedtime>";
        item += "	<projection>" + iv.getProjection() + "</projection>";
        item += "</businessconcept>";

        return item;
    }

    /***************************************************************************
     * Put Item
     * **************************************************************************/

    public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
        try {
            String projection = wsPutItem.getXmlString();
            Element root = Util.parse(projection, null).getDocumentElement();

            String concept = root.getLocalName();

            DataModelPOJO dataModel = com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
                    new DataModelPOJOPK(wsPutItem.getWsDataModelPK().getPk()));
            Document schema = Util.parseXSD(dataModel.getSchema());
            XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, concept);
            // get key values
            String[] itemKeyValues = Util.getKeyValuesFromItem(root, conceptKey);
            DataClusterPOJOPK dcpk = new DataClusterPOJOPK(wsPutItem.getWsDataClusterPK().getPk());
            // update the item using new field values
            // load the item first if itemkey provided
            // this only operate non system items
            if (!XSystemObjects.isXSystemObject(XObjectType.DATA_CLUSTER, wsPutItem.getWsDataClusterPK().getPk())) {
                if (wsPutItem.getIsUpdate()) {
                     // FIXME BUG 0019650  remove the multi-occurence elements and save,the elements are still displayed
                     // old node override new when they are different
//                    if (itemKeyValues.length > 0) {
                        // check if only update the key ,do nothing see 0012169
                        // if(Util.isOnlyUpdateKey(root, concept, conceptKey, itemKeyValues))
                        // return null;
//                        ItemPOJO pj = new ItemPOJO(dcpk, concept, itemKeyValues, System.currentTimeMillis(), projection);
//                        String revisionId = LocalUser.getLocalUser().getUniverse().getConceptRevisionID(concept);
//                        pj = ItemPOJO.load(revisionId, pj.getItemPOJOPK(), false);
                        // normal case no polym //FIXME a bad solution
//                        if (pj != null && projection.indexOf("xsi:type") == -1 && projection.indexOf("tmdm:type") == -1) {
//                            // get updated path
//                            Node old = pj.getProjection();
//                            Node newNode = root;
//                            HashMap<String, UpdateReportItem> updatedPath = Util.compareElement("/" + old.getLocalName(),
//                                    newNode, old);
//                            if (updatedPath.size() > 0) {
//                                if ("sequence".equals(Util.getConceptModelType(concept, dataModel.getSchema()))) { // if
//                                                                                                                   // the
//                                                                                                                   // concept
//                                                                                                                   // is
//                                                                                                                   // sequence
//                                    // update the Node according to schema to keep the sequence as the same with the
//                                    // schema
//                                    old = Util.updateNodeBySchema(concept, dataModel.getSchema(), old);
//                                }
//                                old = Util.updateElement("/" + old.getLocalName(), old, updatedPath);
//                                projection = Util.getXMLStringFromNode(old);
//                            } else {// if no update, return see 0012116
//                                return null;
//                            }
//                        }
//                    }
                } else {
                    if (Util.containsUUIDType(concept, dataModel.getSchema(), root)) {
                        // update the item according to datamodel if there is UUID/AUTO_INCREMENT field and it's empty
                        // we need to regenerate an empty field like '<uuid_field/>'
                        Node newNode = Util.updateNodeBySchema(concept, dataModel.getSchema(), root);
                        projection = Util.getXMLStringFromNode(newNode);
                    }
                }
            }

            ItemPOJOPK itemPOJOPK = com.amalto.core.util.Util.getItemCtrl2Local().putItem(
                    new ItemPOJO(dcpk, concept, itemKeyValues, System.currentTimeMillis(), projection), dataModel);
            if (itemPOJOPK == null)
                return null;

            // update vocabulary
            // com.amalto.core.util.Util.getDataClusterCtrlLocal().addToVocabulary(dcpk, projection);

            return new WSItemPK(new WSDataClusterPK(itemPOJOPK.getDataClusterPOJOPK().getUniqueId()),
                    itemPOJOPK.getConceptName(), itemPOJOPK.getIds());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Extract Items
     * **************************************************************************/
    public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
        throw new RemoteException("Not Supported!");
    }

    public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView)
            throws RemoteException {
        try {
            TransformerContext context = com.amalto.core.util.Util.getItemCtrl2Local().extractUsingTransformerThroughView(
                    new DataClusterPOJOPK(wsExtractUsingTransformerThruView.getWsDataClusterPK().getPk()),
                    new TransformerV2POJOPK(wsExtractUsingTransformerThruView.getWsTransformerPK().getPk()),
                    new ViewPOJOPK(wsExtractUsingTransformerThruView.getWsViewPK().getPk()),
                    XConverter.WS2VO(wsExtractUsingTransformerThruView.getWhereItem()),
                    wsExtractUsingTransformerThruView.getSpellTreshold(), wsExtractUsingTransformerThruView.getOrderBy(),
                    wsExtractUsingTransformerThruView.getDirection(), wsExtractUsingTransformerThruView.getSkip(),
                    wsExtractUsingTransformerThruView.getMaxItems());
            HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>) context
                    .get(TransformerCtrlBean.CTX_PIPELINE);
            return XConverter.POJO2WSOLD(pipeline);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Delete Items
     * **************************************************************************/
    public WSItemPK deleteItem(WSDeleteItem wsDeleteItem) throws RemoteException {
        try {
            ItemPOJOPK itemPK = new ItemPOJOPK(new DataClusterPOJOPK(wsDeleteItem.getWsItemPK().getWsDataClusterPK().getPk()),
                    wsDeleteItem.getWsItemPK().getConceptName(), wsDeleteItem.getWsItemPK().getIds());
            ItemPOJOPK ipk = com.amalto.core.util.Util.getItemCtrl2Local().deleteItem(itemPK);
            return ipk == null ? null : wsDeleteItem.getWsItemPK();

        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSInt deleteItems(WSDeleteItems wsDeleteItems) throws RemoteException {
        try {
            int numItems = com.amalto.core.util.Util.getItemCtrl2Local().deleteItems(
                    new DataClusterPOJOPK(wsDeleteItems.getWsDataClusterPK().getPk()), wsDeleteItems.getConceptName(),
                    XConverter.WS2VO(wsDeleteItems.getWsWhereItem()), wsDeleteItems.getSpellTreshold());
            return new WSInt(numItems);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
            // } catch (com.amalto.webapp.util.XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * DirectQuery
     * **************************************************************************/

    public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
        try {
            DataClusterPOJOPK dcpk = (wsRunQuery.getWsDataClusterPK() == null) ? null : new DataClusterPOJOPK(wsRunQuery
                    .getWsDataClusterPK().getPk());
            Collection<String> result = com.amalto.core.util.Util.getItemCtrl2Local().runQuery(wsRunQuery.getRevisionID(), dcpk,
                    wsRunQuery.getQuery(), wsRunQuery.getParameters());
            return new WSStringArray(result.toArray(new String[result.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    };

    /***************************************************************************
     * RoutingRule
     * **************************************************************************/
    public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet) throws RemoteException {
        try {
            return XConverter.VO2WS(Util.getRoutingRuleCtrlLocal().getRoutingRule(
                    new RoutingRulePOJOPK(wsRoutingRuleGet.getWsRoutingRulePK().getPk())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
            // } catch (com.amalto.webapp.util.XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule) throws RemoteException {
        try {
            return new WSBoolean(Util.getRoutingRuleCtrlLocal().existsRoutingRule(
                    new RoutingRulePOJOPK(wsExistsRoutingRule.getWsRoutingRulePK().getPk())) != null);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regexp) throws RemoteException {
        try {
            Collection<RoutingRulePOJOPK> pks = Util.getRoutingRuleCtrlLocal().getRoutingRulePKs(regexp.getRegex());
            ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
            for (Iterator iter = pks.iterator(); iter.hasNext();) {
                RoutingRulePOJOPK pk = (RoutingRulePOJOPK) iter.next();
                list.add(new WSRoutingRulePK(pk.getUniqueId()));
            }
            return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule) throws RemoteException {
        try {
            return new WSRoutingRulePK(Util.getRoutingRuleCtrlLocal()
                    .removeRoutingRule(new RoutingRulePOJOPK(wsDeleteRoutingRule.getWsRoutingRulePK().getPk())).getUniqueId());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule) throws RemoteException {
        try {
            return new WSRoutingRulePK(Util.getRoutingRuleCtrlLocal()
                    .putRoutingRule(XConverter.WS2VO(wsRoutingRule.getWsRoutingRule())).getUniqueId());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
            // } catch (com.amalto.webapp.util.XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * SERVICES
     * **************************************************************************/

    public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsGetConfiguration.getJndiName());

            String configuration = (String) com.amalto.core.util.Util.getMethod(service, "getConfiguration").invoke(service,
                    new Object[] { wsGetConfiguration.getOptionalParameter() });
            return new WSString(configuration);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsPutConfiguration.getJndiName());

            com.amalto.core.util.Util.getMethod(service, "putConfiguration").invoke(service,
                    new Object[] { wsPutConfiguration.getConfiguration() });
            return new WSString(wsPutConfiguration.getConfiguration());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("serviceAction() " + wsServiceAction.getJndiName());
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsServiceAction.getJndiName());
            String result = "";

            if (WSServiceActionCode.EXECUTE.equals(wsServiceAction.getWsAction())) {

                Method method = com.amalto.core.util.Util.getMethod(service, wsServiceAction.getMethodName());

                result = (String) method.invoke(service, wsServiceAction.getMethodParameters());
            } else {
                if (WSServiceActionCode.START.equals(wsServiceAction.getWsAction())) {
                    com.amalto.core.util.Util.getMethod(service, "start").invoke(service, new Object[] {});
                } else if (WSServiceActionCode.STOP.equals(wsServiceAction.getWsAction())) {
                    com.amalto.core.util.Util.getMethod(service, "stop").invoke(service, new Object[] {});
                }
                result = (String) com.amalto.core.util.Util.getMethod(service, "getStatus").invoke(service, new Object[] {});
            }
            return new WSString(result);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
        try {
            ArrayList<WSServicesListItem> wsList = new ArrayList<WSServicesListItem>();
            InitialContext ctx = new InitialContext();
            NamingEnumeration<NameClassPair> list = ctx.list("amalto/local/service");
            while (list.hasMore()) {
                NameClassPair nc = list.next();
                WSServicesListItem item = new WSServicesListItem();
                item.setJndiName(nc.getName());
                wsList.add(item);
            }
            return new WSServicesList(wsList.toArray(new WSServicesListItem[wsList.size()]));
            // } catch (com.amalto.core.util.XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Stored Procedures
     * **************************************************************************/

    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJOPK pk = ctrl.removeStoredProcedure(new StoredProcedurePOJOPK(wsStoredProcedureDelete
                    .getWsStoredProcedurePK().getPk()));
            return new WSStoredProcedurePK(pk.getIds()[0]);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            Collection c = ctrl.execute(new StoredProcedurePOJOPK(wsExecuteStoredProcedure.getWsStoredProcedurePK().getPk()),
                    wsExecuteStoredProcedure.getRevisionID(), new DataClusterPOJOPK(wsExecuteStoredProcedure.getWsDataClusterPK()
                            .getPk()), wsExecuteStoredProcedure.getParameters());
            if (c == null)
                return null;
            String[] xmls = new String[c.size()];
            int i = 0;
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                xmls[i++] = (String) iter.next();
            }
            return new WSStringArray(xmls);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJO pojo = ctrl.getStoredProcedure(new StoredProcedurePOJOPK(wsGetStoredProcedure
                    .getWsStoredProcedurePK().getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJO pojo = ctrl.existsStoredProcedure(new StoredProcedurePOJOPK(wsExistsStoredProcedure
                    .getWsStoredProcedurePK().getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            Collection c = ctrl.getStoredProcedurePKs(regex.getRegex());
            if (c == null)
                return null;
            WSStoredProcedurePK[] pks = new WSStoredProcedurePK[c.size()];
            int i = 0;
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                pks[i++] = new WSStoredProcedurePK(((StoredProcedurePOJOPK) iter.next()).getIds()[0]);
            }
            return new WSStoredProcedurePKArray(pks);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJOPK pk = ctrl.putStoredProcedure(XConverter.WS2POJO(wsStoredProcedure.getWsStoredProcedure()));
            return new WSStoredProcedurePK(pk.getIds()[0]);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /***************************************************************************
     * Ping - test that we can authenticate by getting a server response
     * **************************************************************************/

    public WSString ping(WSPing wsPing) throws RemoteException {
        try {
            return new WSString(wsPing.getEcho());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Xtentis JCA Connector support
     * **************************************************************************/

    private transient ConnectionFactory cxFactory = null;

    public WSConnectorInteractionResponse connectorInteraction(WSConnectorInteraction wsConnectorInteraction)
            throws RemoteException {
        // This one does not call an EJB

        WSConnectorInteractionResponse response = new WSConnectorInteractionResponse();
        Connection conx = null;
        try {

            String JNDIName = wsConnectorInteraction.getJNDIName();
            conx = getConnection(JNDIName);

            Interaction interaction = conx.createInteraction();
            InteractionSpecImpl interactionSpec = new InteractionSpecImpl();

            MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);

            WSConnectorFunction cf = wsConnectorInteraction.getFunction();
            if ((WSConnectorFunction.GET_STATUS).equals(cf)) {
                interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
            } else if ((WSConnectorFunction.PULL).equals(cf)) {
                interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
            } else if ((WSConnectorFunction.PUSH).equals(cf)) {
                interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
            } else if ((WSConnectorFunction.START).equals(cf)) {
                interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
            } else if ((WSConnectorFunction.STOP).equals(cf)) {
                interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
            }

            recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, getMapFromKeyValues(wsConnectorInteraction.getParameters()));

            MappedRecord recordOut = (MappedRecord) interaction.execute(interactionSpec, recordIn);

            String code = (String) recordOut.get(RecordFactoryImpl.STATUS_CODE_OUT);
            HashMap map = (HashMap) recordOut.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);

            if ("OK".equals(code)) {
                response.setCode(WSConnectorResponseCode.OK);
            } else if ("STOPPED".equals(code)) {
                response.setCode(WSConnectorResponseCode.STOPPED);
            } else if ("ERROR".equals(code)) {
                response.setCode(WSConnectorResponseCode.ERROR);
            } else {
                throw new RemoteException("Unknown code: " + code);
            }
            response.setParameters(getKeyValuesFromMap(map));

        } catch (ResourceException e) {
            throw new RemoteException(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        } finally {
            try {
                conx.close();
            } catch (Exception cx) {
                org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                        "connectorInteraction() Connection close exception: " + cx.getLocalizedMessage());
            }
        }
        return response;

    }

    private Connection getConnection(String JNDIName) throws RemoteException {
        try {
            if (cxFactory == null)
                cxFactory = (ConnectionFactory) (new InitialContext()).lookup(JNDIName);
            return cxFactory.getConnection();
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private HashMap getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != null) {
                        String key = params[i].getKey();
                        byte[] bytes = (new BASE64Decoder()).decodeBuffer(params[i].getBase64StringValue());
                        if (bytes != null) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            map.put(key, ois.readObject());
                        } else {
                            map.put(key, null);
                        }
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private WSBase64KeyValue[] getKeyValuesFromMap(HashMap params) throws RemoteException {
        try {
            if (params == null)
                return null;
            WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
            Set keys = params.keySet();
            int i = 0;
            for (Iterator iter = keys.iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                Object value = params.get(key);
                if (value != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(value);
                    String base64Value = Base64Encoder.encode(baos.toByteArray());
                    keyValues[i] = new WSBase64KeyValue();
                    keyValues[i].setKey(key);
                    keyValues[i].setBase64StringValue(base64Value);
                    i++;
                }
            }
            return keyValues;
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /***************************************************************************
     * Transformer
     * **************************************************************************/

    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            return new WSTransformerPK(ctrl.removeTransformer(
                    new TransformerPOJOPK(wsTransformerDelete.getWsTransformerPK().getPk())).getUniqueId());

        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            TransformerPOJO pojo = ctrl.getTransformer(new TransformerPOJOPK(wsGetTransformer.getWsTransformerPK().getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            TransformerPOJO pojo = ctrl
                    .existsTransformer(new TransformerPOJOPK(wsExistsTransformer.getWsTransformerPK().getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            Collection c = ctrl.getTransformerPKs(regex.getRegex());
            if (c == null)
                return null;
            WSTransformerPK[] pks = new WSTransformerPK[c.size()];
            int i = 0;
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                pks[i++] = new WSTransformerPK(((TransformerPOJOPK) iter.next()).getUniqueId());
            }
            return new WSTransformerPKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            TransformerPOJOPK pk = ctrl.putTransformer(XConverter.WS2POJO(wsTransformer.getWsTransformer()));
            return new WSTransformerPK(pk.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProcessBytesUsingTransformer)
            throws RemoteException {
        try {
            com.amalto.core.util.TransformerPluginContext context = Util.getTransformerCtrlLocal().process(
                    new com.amalto.core.util.TypedContent(null, wsProcessBytesUsingTransformer.getWsBytes().getBytes(),
                            wsProcessBytesUsingTransformer.getContentType()),
                    new TransformerPOJOPK(wsProcessBytesUsingTransformer.getWsTransformerPK().getPk()),
                    XConverter.WS2POJO(wsProcessBytesUsingTransformer.getWsOutputDecisionTable()));
            HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>) context
                    .get(TransformerCtrlBean.CTX_PIPELINE);
            // Add the Item PKs to the pipeline as comma seperated lines
            String pksAsLine = "";
            Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>) context.get(TransformerCtrlBean.CTX_PKS);
            for (Iterator iter = pks.iterator(); iter.hasNext();) {
                ItemPOJOPK pk = (ItemPOJOPK) iter.next();
                if (!"".equals(pksAsLine))
                    pksAsLine += "\n";
                pksAsLine += pk.getConceptName() + "," + Util.joinStrings(pk.getIds(), ",");
            }
            pipeline.put(TransformerCtrlBean.CTX_PKS, new com.amalto.core.util.TypedContent(null, pksAsLine.getBytes("UTF-8"),
                    "text/plain; charset=\"utf-8\""));
            // return the pipeline
            return XConverter.POJO2WSOLD(pipeline);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(
            WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
        try {
            return new WSBackgroundJobPK(Util
                    .getTransformerCtrlLocal()
                    .processBytesAsBackgroundJob(wsProcessBytesUsingTransformerAsBackgroundJob.getWsBytes().getBytes(),
                            wsProcessBytesUsingTransformerAsBackgroundJob.getContentType(),
                            new TransformerPOJOPK(wsProcessBytesUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
                            XConverter.WS2POJO(wsProcessBytesUsingTransformerAsBackgroundJob.getWsOutputDecisionTable()))
                    .getUniqueId());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

    }

    public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFileUsingTransformer)
            throws RemoteException {
        try {
            // read the entire file into bytes

            com.amalto.core.util.TransformerPluginContext context = Util.getTransformerCtrlLocal().process(
                    new com.amalto.core.util.TypedContent(new FileInputStream(new File(
                            wsProcessFileUsingTransformer.getFileName())), null, wsProcessFileUsingTransformer.getContentType()),
                    new TransformerPOJOPK(wsProcessFileUsingTransformer.getWsTransformerPK().getPk()),
                    XConverter.WS2POJO(wsProcessFileUsingTransformer.getWsOutputDecisionTable()));
            HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>) context
                    .get(TransformerCtrlBean.CTX_PIPELINE);
            // Add the Item PKs to the pipeline as comma seperated lines
            String pksAsLine = "";
            Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>) context.get(TransformerCtrlBean.CTX_PIPELINE);
            for (Iterator iter = pks.iterator(); iter.hasNext();) {
                ItemPOJOPK pk = (ItemPOJOPK) iter.next();
                if (!"".equals(pksAsLine))
                    pksAsLine += "\n";
                pksAsLine += pk.getConceptName() + "," + Util.joinStrings(pk.getIds(), ",");
            }
            pipeline.put(TransformerCtrlBean.CTX_PIPELINE,
                    new com.amalto.core.util.TypedContent(null, pksAsLine.getBytes("UTF-8"), "text/plain; charset=\"utf-8\""));
            // return the pipeline
            return XConverter.POJO2WSOLD(pipeline);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

    }

    public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(
            WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
        try {
            return new WSBackgroundJobPK(Util
                    .getTransformerCtrlLocal()
                    .processFileAsBackgroundJob(wsProcessFileUsingTransformerAsBackgroundJob.getFileName(),
                            wsProcessFileUsingTransformerAsBackgroundJob.getContentType(),
                            new TransformerPOJOPK(wsProcessFileUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
                            XConverter.WS2POJO(wsProcessFileUsingTransformerAsBackgroundJob.getWsOutputDecisionTable()))
                    .getUniqueId());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * TransformerV2
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            return new WSTransformerV2PK(ctrl.removeTransformer(
                    new TransformerV2POJOPK(wsTransformerV2Delete.getWsTransformerV2PK().getPk())).getUniqueId());

        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            TransformerV2POJO pojo = ctrl.getTransformer(new TransformerV2POJOPK(wsGetTransformerV2.getWsTransformerV2PK()
                    .getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            TransformerV2POJO pojo = ctrl.existsTransformer(new TransformerV2POJOPK(wsExistsTransformerV2.getWsTransformerV2PK()
                    .getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            Collection c = ctrl.getTransformerPKs(regex.getRegex());
            if (c == null)
                return null;
            WSTransformerV2PK[] pks = new WSTransformerV2PK[c.size()];
            int i = 0;
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                pks[i++] = new WSTransformerV2PK(((TransformerV2POJOPK) iter.next()).getUniqueId());
            }
            return new WSTransformerV2PKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            TransformerV2POJOPK pk = ctrl.putTransformer(XConverter.WS2POJO(wsTransformerV2.getWsTransformerV2()));
            return new WSTransformerV2PK(pk.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
        try {
            final String RUNNING = "XtentisWSBean.executeTransformerV2.running";
            TransformerContext context = XConverter.WS2POJO(wsExecuteTransformerV2.getWsTransformerContext());
            context.put(RUNNING, Boolean.TRUE);
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            ctrl.execute(context, XConverter.WS2POJO(wsExecuteTransformerV2.getWsTypedContent()), new TransformerCallBack() {

                public void contentIsReady(TransformerContext context) throws com.amalto.core.util.XtentisException {
                    org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                            "XtentisWSBean.executeTransformerV2.contentIsReady() ");
                }

                public void done(TransformerContext context) throws com.amalto.core.util.XtentisException {
                    org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.done() ");
                    context.put(RUNNING, Boolean.FALSE);
                }
            });
            while (((Boolean) context.get(RUNNING)).booleanValue()) {
                Thread.sleep(100);
            }
            return XConverter.POJO2WS(context);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
            throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            BackgroundJobPOJOPK bgPK = ctrl.executeAsJob(
                    XConverter.WS2POJO(wsExecuteTransformerV2AsJob.getWsTransformerContext()), new TransformerCallBack() {

                        public void contentIsReady(TransformerContext context) throws com.amalto.core.util.XtentisException {
                            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                    "XtentisWSBean.executeTransformerV2AsJob.contentIsReady() ");
                        }

                        public void done(TransformerContext context) throws com.amalto.core.util.XtentisException {
                            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                    "XtentisWSBean.executeTransformerV2AsJob.done() ");
                        }
                    });
            return new WSBackgroundJobPK(bgPK.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2)
            throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            return XConverter.POJO2WS(ctrl.extractThroughTransformer(new TransformerV2POJOPK(wsExtractThroughTransformerV2
                    .getWsTransformerV2PK().getPk()), XConverter.WS2POJO(wsExtractThroughTransformerV2.getWsItemPK())));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    // private WSPipeline POJO2WS(HashMap<String,TypedContent> pipeline) throws Exception{
    // if (pipeline == null) return null;
    //
    // ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
    // Set keys = pipeline.keySet();
    // for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
    // String output = (String) iter.next();
    // TypedContent content = pipeline.get(output);
    // byte[] bytes = content.getContentBytes();
    // WSExtractedContent wsContent = new WSExtractedContent(
    // new WSByteArray(bytes),
    // content.getContentType()
    // );
    // WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(
    // output,
    // wsContent
    // );
    // entries.add(wsEntry);
    // }
    // return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
    // }

    /***************************************************************************
     * TRANSFORMER PLUGINS V2
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPlugin) throws RemoteException {
        try {
            return new WSBoolean(Util.existsComponent(null, wsExistsTransformerPlugin.getJndiName()));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration)
            throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, wsGetConfiguration.getJndiName());

            String configuration = (String) Util.getMethod(service, "getConfiguration").invoke(service,
                    new Object[] { wsGetConfiguration.getOptionalParameter() });
            return new WSString(configuration);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration)
            throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, wsPutConfiguration.getJndiName());

            Util.getMethod(service, "putConfiguration").invoke(service, new Object[] { wsPutConfiguration.getConfiguration() });
            return new WSString(wsPutConfiguration.getConfiguration());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerPluginV2Details getTransformerPluginV2Details(
            WSGetTransformerPluginV2Details wsGetTransformerPluginDetails) throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, wsGetTransformerPluginDetails.getJndiName());
            String description = (String) Util.getMethod(service, "getDescription").invoke(
                    service,
                    new Object[] { wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails
                            .getLanguage() });
            String documentation = (String) Util.getMethod(service, "getDocumentation").invoke(
                    service,
                    new Object[] { wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails
                            .getLanguage() });
            String parametersSchema = (String) Util.getMethod(service, "getParametersSchema").invoke(service, new Object[] {});

            ArrayList<TransformerPluginVariableDescriptor> inputVariableDescriptors = (ArrayList<TransformerPluginVariableDescriptor>) Util
                    .getMethod(service, "getInputVariableDescriptors").invoke(
                            service,
                            new Object[] { wsGetTransformerPluginDetails.getLanguage() == null ? ""
                                    : wsGetTransformerPluginDetails.getLanguage() });
            ArrayList<WSTransformerPluginV2VariableDescriptor> wsInputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
            if (inputVariableDescriptors != null) {
                for (Iterator iter = inputVariableDescriptors.iterator(); iter.hasNext();) {
                    TransformerPluginVariableDescriptor descriptor = (TransformerPluginVariableDescriptor) iter.next();
                    wsInputVariableDescriptors.add(XConverter.POJO2WS(descriptor));
                }
            }

            ArrayList<TransformerPluginVariableDescriptor> outputVariableDescriptors = (ArrayList<TransformerPluginVariableDescriptor>) Util
                    .getMethod(service, "getOutputVariableDescriptors").invoke(
                            service,
                            new Object[] { wsGetTransformerPluginDetails.getLanguage() == null ? ""
                                    : wsGetTransformerPluginDetails.getLanguage() });
            ArrayList<WSTransformerPluginV2VariableDescriptor> wsOutputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
            if (outputVariableDescriptors != null) {
                for (Iterator iter = outputVariableDescriptors.iterator(); iter.hasNext();) {
                    TransformerPluginVariableDescriptor descriptor = (TransformerPluginVariableDescriptor) iter.next();
                    wsOutputVariableDescriptors.add(XConverter.POJO2WS(descriptor));
                }
            }

            return new WSTransformerPluginV2Details(
                    wsInputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsInputVariableDescriptors
                            .size()]),
                    wsOutputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsOutputVariableDescriptors
                            .size()]), description, documentation, parametersSchema);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginsList)
            throws RemoteException {
        try {
            ArrayList<WSTransformerPluginV2SListItem> wsList = new ArrayList<WSTransformerPluginV2SListItem>();
            InitialContext ctx = new InitialContext();
            NamingEnumeration<NameClassPair> list = ctx.list("amalto/local/transformer/plugin");
            while (list.hasMore()) {
                NameClassPair nc = list.next();
                WSTransformerPluginV2SListItem item = new WSTransformerPluginV2SListItem();
                item.setJndiName(nc.getName());
                Object service = Util.retrieveComponent(null, "amalto/local/transformer/plugin/" + nc.getName());
                String description = (String) Util.getMethod(service, "getDescription").invoke(
                        service,
                        new Object[] { wsGetTransformerPluginsList.getLanguage() == null ? "" : wsGetTransformerPluginsList
                                .getLanguage() });
                item.setDescription(description);
                wsList.add(item);
            }
            return new WSTransformerPluginV2SList(wsList.toArray(new WSTransformerPluginV2SListItem[wsList.size()]));
            // } catch (XtentisException e) {
            // throw(new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Menu
     * **************************************************************************/

    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            return new WSMenuPK(ctrl.removeMenu(new MenuPOJOPK(wsMenuDelete.getWsMenuPK().getPk())).getUniqueId());

        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            MenuPOJO pojo = ctrl.getMenu(new MenuPOJOPK(wsGetMenu.getWsMenuPK().getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            MenuPOJO pojo = ctrl.existsMenu(new MenuPOJOPK(wsExistsMenu.getWsMenuPK().getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            Collection c = ctrl.getMenuPKs(regex.getRegex());
            if (c == null)
                return null;
            WSMenuPK[] pks = new WSMenuPK[c.size()];
            int i = 0;
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                pks[i++] = new WSMenuPK(((MenuPOJOPK) iter.next()).getUniqueId());
            }
            return new WSMenuPKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            MenuPOJOPK pk = ctrl.putMenu(XConverter.WS2POJO(wsMenu.getWsMenu()));
            return new WSMenuPK(pk.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /***************************************************************************
     * BACKGROUND JOBS
     * **************************************************************************/

    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs status) throws RemoteException {
        try {
            throw new RemoteException("WSBackgroundJobPKArray is not implemented in this version of the core");
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsGetBackgroundJob) throws RemoteException {
        try {
            return XConverter.POJO2WS(BackgroundJobCtrlUtil.getLocalHome().create()
                    .getBackgroundJob(new BackgroundJobPOJOPK(wsGetBackgroundJob.getPk())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsPutBackgroundJob) throws RemoteException {
        try {
            return new WSBackgroundJobPK(BackgroundJobCtrlUtil.getLocalHome().create()
                    .putBackgroundJob(XConverter.WS2POJO(wsPutBackgroundJob.getWsBackgroundJob())).getUniqueId());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString count(WSCount wsCount) throws RemoteException {
        try {

            String countPath = wsCount.getCountPath();
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, countPath);

            long count = Util.getItemCtrl2Local().count(new DataClusterPOJOPK(wsCount.getWsDataClusterPK().getPk()),
                    wsCount.getCountPath(),
                    XConverter.WS2VO(wsCount.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsCount.getSpellTreshold());
            return new WSString(count + "");
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Routing Order V2
     * **************************************************************************/

    public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            return XConverter.POJO2WS(ctrl.removeRoutingOrder(XConverter.WS2POJO(wsDeleteRoutingOrder.getWsRoutingOrderPK())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(
            WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(XConverter.WS2POJO(wsExecuteRoutingOrderAsynchronously
                    .getRoutingOrderV2PK()));
            ctrl.executeAsynchronously(ro);
            return XConverter.POJO2WS(ro.getAbstractRoutingOrderPOJOPK());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously)
            throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(XConverter.WS2POJO(wsExecuteRoutingOrderSynchronously
                    .getRoutingOrderV2PK()));
            return new WSString(ctrl.executeSynchronously(ro));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            return XConverter.POJO2WS(ctrl.existsRoutingOrder(XConverter.WS2POJO(wsExistsRoutingOrder.getWsRoutingOrderPK())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrderV2) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            return XConverter.POJO2WS(ctrl.getRoutingOrder(XConverter.WS2POJO(wsGetRoutingOrderV2.getWsRoutingOrderPK())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
            WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
        try {
            WSRoutingOrderV2PKArray wsPKArray = new WSRoutingOrderV2PKArray();
            ArrayList<WSRoutingOrderV2PK> list = new ArrayList<WSRoutingOrderV2PK>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2PKsByCriteria
                    .getWsSearchCriteria());
            for (Iterator<AbstractRoutingOrderV2POJOPK> iterator = pks.iterator(); iterator.hasNext();) {
                AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK = iterator.next();
                list.add(XConverter.POJO2WS(abstractRoutingOrderV2POJOPK));
            }
            wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2PK[list.size()]));
            return wsPKArray;
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
            throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            WSRoutingOrderV2Array wsPKArray = new WSRoutingOrderV2Array();
            ArrayList<WSRoutingOrderV2> list = new ArrayList<WSRoutingOrderV2>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2SByCriteria
                    .getWsSearchCriteria());
            for (Iterator<AbstractRoutingOrderV2POJOPK> iterator = pks.iterator(); iterator.hasNext();) {
                AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK = iterator.next();
                list.add(XConverter.POJO2WS(ctrl.getRoutingOrder(abstractRoutingOrderV2POJOPK)));
            }
            wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2[list.size()]));
            return wsPKArray;
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    private Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrdersByCriteria(WSRoutingOrderV2SearchCriteria criteria)
            throws Exception {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            Class<? extends AbstractRoutingOrderV2POJO> clazz = null;
            if (criteria.getStatus().equals(WSRoutingOrderV2Status.ACTIVE)) {
                clazz = ActiveRoutingOrderV2POJO.class;
            } else if (criteria.getStatus().equals(WSRoutingOrderV2Status.COMPLETED)) {
                clazz = CompletedRoutingOrderV2POJO.class;
            } else if (criteria.getStatus().equals(WSRoutingOrderV2Status.FAILED)) {
                clazz = FailedRoutingOrderV2POJO.class;
            }
            Collection<AbstractRoutingOrderV2POJOPK> pks = ctrl.getRoutingOrderPKsByCriteria(clazz,
                    criteria.getAnyFieldContains(), criteria.getNameContains(), criteria.getTimeCreatedMin(),
                    criteria.getTimeCreatedMax(), criteria.getTimeScheduledMin(), criteria.getTimeScheduledMax(),
                    criteria.getTimeLastRunStartedMin(), criteria.getTimeLastRunStartedMax(),
                    criteria.getTimeLastRunCompletedMin(), criteria.getTimeLastRunCompletedMax(),
                    criteria.getItemPKConceptContains(), criteria.getItemPKIDFieldsContain(), criteria.getServiceJNDIContains(),
                    criteria.getServiceParametersContain(), criteria.getMessageContain());

            return pks;
        } catch (Exception e) {
            String err = "ERROR SYSTRACE: " + e.getMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(err, e);
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /***************************************************************************
     * Versioning
     * **************************************************************************/

    /***************************************************************************
     * Routing Engine V2
     * **************************************************************************/

    public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            RoutingRulePOJOPK[] rules = ctrl.route(XConverter.WS2POJO(wsRouteItem.getWsItemPK()));
            ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
            if (rules == null || rules.length == 0)
                return null;
            for (int i = 0; i < rules.length; i++) {
                list.add(new WSRoutingRulePK(rules[i].getUniqueId()));
            }
            return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException {
        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.START)) {
                ctrl.start();
            } else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.STOP)) {
                ctrl.stop();
            } else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.SUSPEND)) {
                ctrl.suspend(true);
            } else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.RESUME)) {
                ctrl.suspend(false);
            } else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.STATUS)) {
                // done below;
            }
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            int status = ctrl.getStatus();
            switch (status) {
            case RoutingEngineV2POJO.RUNNING:
                return WSRoutingEngineV2Status.RUNNING;
            case RoutingEngineV2POJO.STOPPED:
                return WSRoutingEngineV2Status.STOPPED;
            case RoutingEngineV2POJO.SUSPENDED:
                return WSRoutingEngineV2Status.SUSPENDED;
            default:
                return WSRoutingEngineV2Status.DEAD;
            }
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public com.amalto.webapp.util.webservices.WSServiceGetDocument getServiceDocument(WSString serviceName)
            throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, "amalto/local/service/" + serviceName.getValue());

            String desc = (String) Util.getMethod(service, "getDescription").invoke(service, new Object[] { "" });
            String configuration = (String) Util.getMethod(service, "getConfiguration").invoke(service, new Object[] { "" });
            String doc = "";
            try {
                doc = (String) Util.getMethod(service, "getDocumentation").invoke(service, new Object[] { "" });
            } catch (Exception e) {
                e.printStackTrace();
            }
            String schema = "";
            schema = (String) Util.getMethod(service, "getConfigurationSchema").invoke(service, new Object[] {});
            String defaultConf = "";
            defaultConf = (String) Util.getMethod(service, "getDefaultConfiguration").invoke(service, new Object[] {});
            return new com.amalto.webapp.util.webservices.WSServiceGetDocument(desc, configuration, doc, schema, defaultConf);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDroppedItemPK dropItem(WSDropItem wsDropItem) throws RemoteException {
        try {
            WSItemPK wsItemPK = wsDropItem.getWsItemPK();
            String partPath = wsDropItem.getPartPath();

            DroppedItemPOJOPK droppedItemPOJOPK = Util.getItemCtrl2Local().dropItem(XConverter.WS2POJO(wsItemPK), partPath);

            return XConverter.POJO2WS(droppedItemPOJOPK);

        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex) throws RemoteException {
        try {

            List droppedItemPOJOPKs = Util.getDroppedItemCtrlLocal().findAllDroppedItemsPKs(regex.getRegex());

            WSDroppedItemPK[] wsDroppedItemPKs = new WSDroppedItemPK[droppedItemPOJOPKs.size()];
            for (int i = 0; i < droppedItemPOJOPKs.size(); i++) {
                DroppedItemPOJOPK droppedItemPOJOPK = (DroppedItemPOJOPK) droppedItemPOJOPKs.get(i);
                wsDroppedItemPKs[i] = XConverter.POJO2WS(droppedItemPOJOPK);
            }

            return new WSDroppedItemPKArray(wsDroppedItemPKs);

        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem) throws RemoteException {
        try {

            DroppedItemPOJO droppedItemPOJO = Util.getDroppedItemCtrlLocal().loadDroppedItem(
                    XConverter.WS2POJO(wsLoadDroppedItem.getWsDroppedItemPK()));

            return XConverter.POJO2WS(droppedItemPOJO);

        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem) throws RemoteException {
        try {

            ItemPOJOPK itemPOJOPK = Util.getDroppedItemCtrlLocal().recoverDroppedItem(
                    XConverter.WS2POJO(wsRecoverDroppedItem.getWsDroppedItemPK()));

            return XConverter.POJO2WS(itemPOJOPK);

        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
        }
    }

    public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
        try {

            DroppedItemPOJOPK droppedItemPOJOPK = Util.getDroppedItemCtrlLocal().removeDroppedItem(
                    XConverter.WS2POJO(wsRemoveDroppedItem.getWsDroppedItemPK()));

            return XConverter.POJO2WS(droppedItemPOJOPK);

        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    private UpdateReportItemPOJO WS2POJO(WSUpdateReportItemPOJO wsUpdateReportItemPOJO) throws Exception {
        return new UpdateReportItemPOJO(wsUpdateReportItemPOJO.getPath(), wsUpdateReportItemPOJO.getOldValue(),
                wsUpdateReportItemPOJO.getNewValue());
    }

    public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        try {

            com.amalto.webapp.util.webservices.WSPutItem wsPutItem = wsPutItemWithReport.getWsPutItem();
            String source = wsPutItemWithReport.getSource();
            String operationType = "";
            Map<String, UpdateReportItemPOJO> updateReportItemsMap = new HashMap<String, UpdateReportItemPOJO>();

            // before saving
            String projection = wsPutItem.getXmlString();
            Element root = Util.parse(projection).getDocumentElement();

            String concept = root.getLocalName();

            DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(
                    new DataModelPOJOPK(wsPutItem.getWsDataModelPK().getPk()));
            Document schema = Util.parseXSD(dataModel.getSchema());
            XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, concept);

            // get key values
            String[] ids = com.amalto.core.util.Util.getKeyValuesFromItem(root, conceptKey);
            DataClusterPOJOPK dcpk = new DataClusterPOJOPK(wsPutItem.getWsDataClusterPK().getPk());
            ItemPOJOPK itemPOJOPK = new ItemPOJOPK(dcpk, concept, ids);
            // get operationType
            ItemPOJO itemPoJo = ItemPOJO.load(itemPOJOPK);
            HashMap<String, UpdateReportItem> updatedPath = new HashMap<String, UpdateReportItem>();

            if (itemPoJo == null) {
                operationType = UpdateReportPOJO.OPERATIONTYPE_CREATE;
            } else {
                operationType = UpdateReportPOJO.OPERATIONTYPE_UPDATEE;
                // get updated path
                Element old = itemPoJo.getProjection();
                Element newNode = root;
                updatedPath = Util.compareElement("/" + old.getLocalName(), newNode, old);
                WSUpdateReportItemArray wsUpdateReportItemArray = new WSUpdateReportItemArray();
                for (Entry<String, UpdateReportItem> entry : updatedPath.entrySet()) {
                    UpdateReportItemPOJO pojo = new UpdateReportItemPOJO(entry.getValue().getPath(), entry.getValue()
                            .getOldValue(), entry.getValue().getNewValue());
                    updateReportItemsMap.put(entry.getKey(), pojo);
                }
            }

            // create resultUpdateReport
            String resultUpdateReport = Util.createUpdateReport(ids, concept, operationType, updatedPath, wsPutItem
                    .getWsDataModelPK().getPk(), wsPutItem.getWsDataClusterPK().getPk());

            // invoke before saving
            String outputErrorMessage = null;
            String errorCode = null;
            if (wsPutItemWithReport.getInvokeBeforeSaving()) {
                outputErrorMessage = Util.beforeSaving(concept, projection, resultUpdateReport);
                if (outputErrorMessage != null) {
                    Document doc = Util.parse(outputErrorMessage);
                    // TODO what if multiple error nodes ?
                    String xpath = "/descendant::error"; //$NON-NLS-1$
                    Node errorNode = XPathAPI.selectSingleNode(doc, xpath);
                    if (errorNode instanceof Element) {
                        Element errorElement = (Element) errorNode;
                        errorCode = errorElement.getAttribute("code"); //$NON-NLS-1$
                    }
                }
            }
            wsPutItemWithReport.setSource(outputErrorMessage);

            WSItemPK wsi = null;
            if (outputErrorMessage == null || "0".equals(errorCode)) {
                String dataClusterPK = wsPutItem.getWsDataClusterPK().getPk();

                org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                        "[putItem-of-putItemWithReport] in dataCluster:" + dataClusterPK);
                wsi = putItem(wsPutItem);
                if (wsi == null)
                    return null;
                concept = wsi.getConceptName();
                ids = wsi.getIds();
                // additional attributes for data changes log
                ILocalUser user = LocalUser.getLocalUser();
                String userName = user.getUsername();
                String revisionID = "";
                UniversePOJO universe = user.getUniverse();
                if (universe != null) {
                    revisionID = universe.getConceptRevisionID(concept);
                }
                String dataModelPK = wsPutItem.getWsDataModelPK().getPk();

                org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                        "[pushUpdateReport-of-putItemWithReport] with concept:" + concept + " operation:" + operationType);
                UpdateReportPOJO updateReportPOJO = new UpdateReportPOJO(concept, Util.joinStrings(ids, "."), operationType,
                        source, System.currentTimeMillis(), dataClusterPK, dataModelPK, userName, revisionID,
                        updateReportItemsMap);

                WSItemPK itemPK = putItem(new WSPutItem(new WSDataClusterPK("UpdateReport"), updateReportPOJO.serialize(),
                        new WSDataModelPK("UpdateReport"), false));

                try {
                    routeItemV2(new WSRouteItemV2(itemPK));
                } catch (RemoteException ex) {
                    throw (new RemoteException("routing failed: " + ex.getLocalizedMessage()));
                }
            }

            return wsi;
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

    }

    public WSMDMConfig getMDMConfiguration() throws RemoteException {
        WSMDMConfig mdmConfig = new WSMDMConfig();
        Properties property = MDMConfiguration.getConfiguration();
        try {
            mdmConfig.setServerName(property.getProperty("xmldb.server.name"));
            mdmConfig.setServerPort(property.getProperty("xmldb.server.port"));
            mdmConfig.setUserName(property.getProperty("xmldb.administrator.username"));
            mdmConfig.setPassword(property.getProperty("xmldb.administrator.password"));
            mdmConfig.setXdbDriver(property.getProperty("xmldb.driver"));
            mdmConfig.setXdbID(property.getProperty("xmldb.dbid"));
            mdmConfig.setXdbUrl(property.getProperty("xmldb.dburl"));
            mdmConfig.setIsupurl(property.getProperty("xmldb.isupurl"));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

        return mdmConfig;
    }

    public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public WSItemPKArray putItemWithReportArray(WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public WSStringArray getChildrenItems(WSGetChildrenItems wsGetChildrenItems) throws RemoteException {
        try {
            Collection res = Util.getItemCtrl2Local().getChildrenItems(wsGetChildrenItems.getClusterName(),
                    wsGetChildrenItems.getConceptName(), wsGetChildrenItems.getPKXpaths().getStrings(),
                    wsGetChildrenItems.getFKXpath(), wsGetChildrenItems.getLabelXpath(), wsGetChildrenItems.getFatherPK(),
                    XConverter.WS2VO(wsGetChildrenItems.getWhereItem()), wsGetChildrenItems.getStart(),
                    wsGetChildrenItems.getLimit());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSItemPKsByCriteriaResponse getItemPKsByFullCriteria(WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria)
            throws RemoteException {
        return doGetItemPKsByCriteria(wsGetItemPKsByFullCriteria.getWsGetItemPKsByCriteria(),
                wsGetItemPKsByFullCriteria.isUseFTSearch());
    }

    public WSCategoryData getMDMCategory(WSCategoryData wsCategoryDataRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public WSItemPK putItemByOperatorType(WSPutItemByOperatorType putItemByOperatorType) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public WSBoolean isItemModifiedByOther(WSIsItemModifiedByOther wsItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public WSString countItemsByCustomFKFilters(WSCountItemsByCustomFKFilters wsCountItemsByCustomFKFilters)
            throws RemoteException {
        try {
            long count = Util.getItemCtrl2Local().countItemsByCustomFKFilters(
                    new DataClusterPOJOPK(wsCountItemsByCustomFKFilters.getWsDataClusterPK().getPk()),
                    wsCountItemsByCustomFKFilters.getConceptName(), wsCountItemsByCustomFKFilters.getInjectedXpath());
            return new WSString(count + "");
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    public WSStringArray getItemsByCustomFKFilters(WSGetItemsByCustomFKFilters wsGetItemsByCustomFKFilters)
            throws RemoteException {
        try {
            ArrayList res = Util.getItemCtrl2Local().getItemsByCustomFKFilters(
                    new DataClusterPOJOPK(wsGetItemsByCustomFKFilters.getWsDataClusterPK().getPk()),
                    wsGetItemsByCustomFKFilters.getConceptName(),
                    new ArrayList<String>(Arrays.asList(wsGetItemsByCustomFKFilters.getViewablePaths().getStrings())),
                    wsGetItemsByCustomFKFilters.getInjectedXpath(), wsGetItemsByCustomFKFilters.getSkip(),
                    wsGetItemsByCustomFKFilters.getMaxItems(), wsGetItemsByCustomFKFilters.getOrderBy(),
                    wsGetItemsByCustomFKFilters.getDirection());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    private WSItemPKsByCriteriaResponse doGetItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria, boolean useFTSearch)
            throws RemoteException {
        try {

            String dataClusterName = wsGetItemPKsByCriteria.getWsDataClusterPK().getPk();

            // Check if user is allowed to read the cluster
            ILocalUser user = LocalUser.getLocalUser();
            boolean authorized = false;
            if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { //$NON-NLS-1$
                authorized = true;
            } else if (user.userCanRead(DataClusterPOJO.class, dataClusterName)) {
                authorized = true;
            }
            if (!authorized) {
                throw new RemoteException("Unauthorized read access on data cluster '" + dataClusterName + "' by user '"
                        + user.getUsername() + "'");
            }

            // If not all concepts are store in the same revision,
            // force the concept to be specified by the user.
            // It would be too demanding to get all the concepts in all revisions (?)
            // The meat of this method should be ported to ItemCtrlBean
            String revisionID = null;
            String conceptName = wsGetItemPKsByCriteria.getConceptName(); 
            if (conceptName == null) {
                if (user.getUniverse().getItemsRevisionIDs().size() > 0) {
                    throw new RemoteException("User " + user.getUsername() + " is using items coming from multiple revisions."
                            + " In that particular case, the concept must be specified");
                } else {
                    revisionID = user.getUniverse().getDefaultItemRevisionID();
                }
            } else {
                revisionID = user.getUniverse().getConceptRevisionID(conceptName);
            }

            ItemPKCriteria criteria = new ItemPKCriteria();
            criteria.setRevisionId(revisionID);
            criteria.setClusterName(dataClusterName);
            criteria.setConceptName(conceptName);
            criteria.setContentKeywords(wsGetItemPKsByCriteria.getContentKeywords());
            criteria.setKeysKeywords(wsGetItemPKsByCriteria.getKeysKeywords());
            criteria.setCompoundKeyKeywords(true);
            criteria.setFromDate(wsGetItemPKsByCriteria.getFromDate());
            criteria.setToDate(wsGetItemPKsByCriteria.getToDate());
            criteria.setMaxItems(wsGetItemPKsByCriteria.getMaxItems());
            criteria.setSkip(wsGetItemPKsByCriteria.getSkip());
            criteria.setUseFTSearch(false);
            List<String> results = com.amalto.core.util.Util.getItemCtrl2Local().getItemPKsByCriteria(criteria);

            XPath xpath = XPathFactory.newInstance().newXPath();
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            WSItemPKsByCriteriaResponseResults[] res = new WSItemPKsByCriteriaResponseResults[results.size()];
            int i = 0;
            for (Iterator iter = results.iterator(); iter.hasNext();) {
                String result = (String) iter.next();
                if (i == 0) {
                    res[i++] = new WSItemPKsByCriteriaResponseResults(System.currentTimeMillis(), new WSItemPK(
                            wsGetItemPKsByCriteria.getWsDataClusterPK(), result, null), ""); //$NON-NLS-1$
                    continue;
                }
                // result = _highlightLeft.matcher(result).replaceAll("");
                // result = _highlightRight.matcher(result).replaceAll("");
                Element r = documentBuilder.parse(new InputSource(new StringReader(result))).getDocumentElement();
                long t = new Long(xpath.evaluate("t", r)).longValue(); //$NON-NLS-1$
                String cn = xpath.evaluate("n", r); //$NON-NLS-1$
                String taskId = xpath.evaluate("taskId", r); //$NON-NLS-1$

                NodeList idsList = (NodeList) xpath.evaluate("./ids/i", r, XPathConstants.NODESET); //$NON-NLS-1$
                String[] ids = new String[idsList.getLength()];
                for (int j = 0; j < idsList.getLength(); j++) {
                    ids[j] = (idsList.item(j).getFirstChild() == null ? "" : idsList.item(j).getFirstChild().getNodeValue()); //$NON-NLS-1$
                }
                res[i++] = new WSItemPKsByCriteriaResponseResults(t, new WSItemPK(wsGetItemPKsByCriteria.getWsDataClusterPK(),
                        cn, ids), taskId);
            }
            return new WSItemPKsByCriteriaResponse(res);

        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage(); //$NON-NLS-1$
                LOG.debug(err, e);
            }
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }
}
