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
package com.amalto.core.delegator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.DroppedItemPOJOPK;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.TransformerCtrlBean;
import com.amalto.core.ejb.TransformerPOJO;
import com.amalto.core.ejb.TransformerPOJOPK;
import com.amalto.core.ejb.UpdateReportItemPOJO;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.migration.MigrationRepository;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.menu.ejb.MenuEntryPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJOPK;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRuleExpressionPOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJOPK;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.SaveException;
import com.amalto.core.save.SaverHelper;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.util.ArrayListHolder;
import com.amalto.core.util.DigestHelper;
import com.amalto.core.util.EntityNotFoundException;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RemoteExceptionFactory;
import com.amalto.core.util.TransformerPluginContext;
import com.amalto.core.util.TransformerPluginSpec;
import com.amalto.core.util.Util;
import com.amalto.core.util.ValidateException;
import com.amalto.core.util.Version;
import com.amalto.core.util.WhereConditionFilter;
import com.amalto.core.util.WhereConditionForcePivotFilter;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereOr;

public abstract class IXtentisWSDelegator implements IBeanDelegator {

    private static Logger LOG = Logger.getLogger(IXtentisWSDelegator.class);

    /***************************************************************************
     * 
     * S E R V I C E S
     * 
     * **************************************************************************/

    /***************************************************************************
     * Components Management
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
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
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
        }
    }

    /***************************************************************************
     * Ping
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString ping(WSPing wsPing) throws RemoteException {
        if ("Studio".equals(wsPing.getEcho())) {// check view user can't use studio //$NON-NLS-1$
            try {
                if (LocalUser.getLocalUser().getRoles().contains(XSystemObjects.ROLE_DEFAULT_VIEWER.getName())) {
                    throw new RemoteException("Viewer user can't use MDM Studio!");
                }
            } catch (Exception e) {
                throw new RemoteException(e.getLocalizedMessage(), e);
            }
        }
        return new WSString(wsPing.getEcho());
    }

    /***************************************************************************
     * Logout
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString logout(WSLogout logout) throws RemoteException {
        String msg = "OK";
        try {
            ILocalUser user = LocalUser.getLocalUser();
            user.logout();
        } catch (Exception e) {
            msg = e.getMessage();
        }
        return new WSString(msg);
    }

    /***************************************************************************
     * Initialize
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSInt initMDM(WSInitData initData) throws RemoteException {
        // run migration tasks
        MigrationRepository.getInstance().execute(true);
        return new WSInt(0);
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSMDMConfig getMDMConfiguration() throws RemoteException {
        WSMDMConfig mdmConfig = new WSMDMConfig();
        Properties property = MDMConfiguration.getConfiguration();
        try {
            mdmConfig.setServerName(property.getProperty("xmldb.server.name")); //$NON-NLS-1$
            mdmConfig.setServerPort(property.getProperty("xmldb.server.port")); //$NON-NLS-1$
            mdmConfig.setUserName(property.getProperty("admin.user")); //$NON-NLS-1$
            mdmConfig.setPassword(property.getProperty("admin.password")); //$NON-NLS-1$
            mdmConfig.setXdbDriver(property.getProperty("xmldb.driver")); //$NON-NLS-1$
            mdmConfig.setXdbID(property.getProperty("xmldb.dbid")); //$NON-NLS-1$
            mdmConfig.setXdbUrl(property.getProperty("xmldb.dburl")); //$NON-NLS-1$
            mdmConfig.setIsupurl(property.getProperty("xmldb.isupurl")); //$NON-NLS-1$
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

        return mdmConfig;
    }

    /***************************************************************************
     * Data Model
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataModel getDataModel(WSGetDataModel wsGetDataModel) throws RemoteException {
        try {
            return VO2WS(Util.getDataModelCtrlLocal()
                    .getDataModel(new DataModelPOJOPK(wsGetDataModel.getWsDataModelPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel) throws RemoteException {
        try {
            return new WSBoolean((Util.getDataModelCtrlLocal().existsDataModel(
                    new DataModelPOJOPK(wsExistsDataModel.getWsDataModelPK().getPk())) != null));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     * 
     */
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp) throws RemoteException {
        try {
            WSDataModelPKArray array = new WSDataModelPKArray();
            Collection<DataModelPOJOPK> list = Util.getDataModelCtrlLocal().getDataModelPKs(regexp.getRegex());
            ArrayList<WSDataModelPK> wsList = new ArrayList<WSDataModelPK>();
            for (DataModelPOJOPK pk : list) {
                WSDataModelPK wsPK = new WSDataModelPK(pk.getUniqueId());
                wsList.add(wsPK);
            }
            array.setWsDataModelPKs(wsList.toArray(new WSDataModelPK[list.size()]));
            return array;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel) throws RemoteException {
        try {
            return new WSDataModelPK(Util.getDataModelCtrlLocal()
                    .removeDataModel(new DataModelPOJOPK(wsDeleteDataModel.getWsDataModelPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * 
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel) throws RemoteException {
        try {
            WSDataModelPK wsDataModelPK = new WSDataModelPK(Util.getDataModelCtrlLocal()
                    .putDataModel(WS2VO(wsDataModel.getWsDataModel())).getUniqueId());
            SaverSession session = SaverSession.newSession();
            session.invalidateTypeCache(wsDataModelPK.getPk());
            session.end();
            return wsDataModelPK;
        } catch (Exception e) {
            throw RemoteExceptionFactory.aggregateCauses(e, true);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * 
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
        try {
            return new WSString(Util.getDataModelCtrlLocal().checkSchema(wsSchema.getSchema()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * 
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    @SuppressWarnings("nls")
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept) throws RemoteException {
        WSBusinessConcept bc = wsPutBusinessConcept.getBusinessConcept();
        try {
            String s = "<xsd:element name=\"" + bc.getName() + "\" type=\"" + bc.getBusinessTemplate() + "\">"
                    + "	<xsd:annotation>";
            WSI18NString[] labels = bc.getWsLabel();
            for (WSI18NString label : labels) {
                s += "<xsd:appinfo source=\"" + label.getLanguage().getValue() + "\">" + label.getLabel() + "</xsd:appinfo>";
            }
            WSI18NString[] docs = bc.getWsDescription();
            for (WSI18NString doc : docs) {
                s += "<xsd:documentation xml:lang=\"" + doc.getLanguage().getValue() + "\">" + doc.getLabel()
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

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * 
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws RemoteException {
        try {
            return new WSString(Util.getDataModelCtrlLocal().putBusinessConceptSchema(
                    new DataModelPOJOPK(wsPutBusinessConceptSchema.getWsDataModelPK().getPk()),
                    wsPutBusinessConceptSchema.getBusinessConceptSchema()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString deleteBusinessConcept(WSDeleteBusinessConcept wsDeleteBusinessConcept) throws RemoteException {
        try {
            return new WSString(Util.getDataModelCtrlLocal().deleteBusinessConcept(
                    new DataModelPOJOPK(wsDeleteBusinessConcept.getWsDataModelPK().getPk()),
                    wsDeleteBusinessConcept.getBusinessConceptName()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getBusinessConcepts(WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
        try {
            return new WSStringArray(Util.getDataModelCtrlLocal().getAllBusinessConceptsNames(
                    new DataModelPOJOPK(wsGetBusinessConcepts.getWsDataModelPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
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

    protected WSDataModel VO2WS(DataModelPOJO pojo) {
        WSDataModel s = new WSDataModel();
        s.setDescription(pojo.getDescription());
        s.setName(pojo.getName());
        s.setXsdSchema(pojo.getSchema());
        return s;
    }

    protected DataModelPOJO WS2VO(WSDataModel ws) throws Exception {
        DataModelPOJO dv = new DataModelPOJO();
        dv.setName(ws.getName());
        dv.setDescription(ws.getDescription());
        dv.setSchema(ws.getXsdSchema());
        return dv;
    }

    /***************************************************************************
     * DataCluster
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet) throws RemoteException {
        try {
            return VO2WS(Util.getDataClusterCtrlLocal().getDataCluster(
                    new DataClusterPOJOPK(wsDataClusterGet.getWsDataClusterPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster) throws RemoteException {
        try {
            return new WSBoolean(Util.getDataClusterCtrlLocal().existsDataCluster(
                    new DataClusterPOJOPK(wsExistsDataCluster.getWsDataClusterPK().getPk())) != null);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
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

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp) throws RemoteException {
        try {
            WSDataClusterPKArray array = new WSDataClusterPKArray();
            ArrayList<WSDataClusterPK> l = new ArrayList<WSDataClusterPK>();
            Collection<DataClusterPOJOPK> vos = Util.getDataClusterCtrlLocal().getDataClusterPKs(regexp.getRegex());
            for (DataClusterPOJOPK pk : vos) {
                l.add(new WSDataClusterPK(pk.getUniqueId()));
            }
            array.setWsDataClusterPKs(l.toArray(new WSDataClusterPK[l.size()]));
            return array;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
        try {
            return new WSDataClusterPK(Util.getDataClusterCtrlLocal()
                    .removeDataCluster(new DataClusterPOJOPK(wsDeleteDataCluster.getWsDataClusterPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster) throws RemoteException {
        try {
            return new WSDataClusterPK(Util.getDataClusterCtrlLocal().putDataCluster(WS2VO(wsDataCluster.getWsDataCluster()))
                    .getUniqueId());
        } catch (Exception e) {
            throw RemoteExceptionFactory.aggregateCauses(e, true);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster) throws RemoteException {
        try {
            Util.getXmlServerCtrlLocal().createCluster(wsDataCluster.getRevisionID(), wsDataCluster.getName());
            DataClusterPOJO pojo = new DataClusterPOJO(wsDataCluster.getName(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
            pojo.store(wsDataCluster.getRevisionID());
            return new WSBoolean(true);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
        try {
            Set<String> concepts = Util.getItemCtrl2Local()
                    .getConceptsInDataCluster(new DataClusterPOJOPK(wsGetConceptsInDataCluster.getWsDataClusterPK().getPk()))
                    .keySet();
            return new WSStringArray(concepts.toArray(new String[concepts.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
            WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
        try {
            UniversePOJO pojo = null;
            if (wsGetConceptsInDataClusterWithRevisions == null
                    || wsGetConceptsInDataClusterWithRevisions.getUniversePK() == null
                    || wsGetConceptsInDataClusterWithRevisions.getUniversePK().getPk() == null
                    || "".equals(wsGetConceptsInDataClusterWithRevisions.getUniversePK().getPk())) { //$NON-NLS-1$
                pojo = new UniversePOJO();// default head revision
            }
            // get conceptRevisions
            DataClusterPOJOPK dataClusterPOJOPK = new DataClusterPOJOPK(wsGetConceptsInDataClusterWithRevisions
                    .getDataClusterPOJOPK().getPk());
            Map concepts = Util.getItemCtrl2Local().getConceptsInDataCluster(dataClusterPOJOPK, pojo);
            if (concepts == null) {
                return null;
            }
            // convert
            WSConceptRevisionMapMapEntry[] mapEntry = new WSConceptRevisionMapMapEntry[concepts.size()];
            int i = 0;
            for (Iterator iterator = concepts.keySet().iterator(); iterator.hasNext(); i++) {
                String concept = (String) iterator.next();
                String revisionId = (String) concepts.get(concept);
                if (revisionId == null) {
                    revisionId = ""; //$NON-NLS-1$
                }
                WSConceptRevisionMapMapEntry entry = new WSConceptRevisionMapMapEntry(concept, revisionId);
                mapEntry[i] = entry;
            }
            return new WSConceptRevisionMap(mapEntry);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    protected WSDataCluster VO2WS(DataClusterPOJO vo) {
        WSDataCluster s = new WSDataCluster();
        s.setDescription(vo.getDescription());
        s.setName(vo.getName());
        s.setVocabulary(vo.getVocabulary());
        return s;
    }

    protected DataClusterPOJO WS2VO(WSDataCluster ws) throws Exception {
        DataClusterPOJO vo = new DataClusterPOJO();
        vo.setName(ws.getName());
        vo.setDescription(ws.getDescription());
        vo.setVocabulary(""); //$NON-NLS-1$
        return vo;
    }

    /***************************************************************************
     * View
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSView getView(WSGetView wsViewGet) throws RemoteException {
        try {
            return VO2WS(Util.getViewCtrlLocal().getView(new ViewPOJOPK(wsViewGet.getWsViewPK().getPk())));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsView(WSExistsView wsExistsView) throws RemoteException {
        try {
            return new WSBoolean(Util.getViewCtrlLocal().existsView(new ViewPOJOPK(wsExistsView.getWsViewPK().getPk())) != null);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
        try {
            WSViewPKArray array = new WSViewPKArray();
            String regex = ((regexp.getRegex() == null) || ("".equals(regexp.getRegex())) || ("*".equals(regexp.getRegex())) ? ".*" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    : regexp.getRegex());
            Collection<ViewPOJOPK> pks = Util.getViewCtrlLocal().getViewPKs(regex);
            List<WSViewPK> list = new ArrayList<WSViewPK>();
            for (ViewPOJOPK pk : pks) {
                list.add(new WSViewPK(pk.getIds()[0]));
            }
            array.setWsViewPK(list.toArray(new WSViewPK[list.size()]));
            return array;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSViewPK deleteView(WSDeleteView wsDeleteView) throws RemoteException {
        try {
            return new WSViewPK(
                    Util.getViewCtrlLocal().removeView(new ViewPOJOPK(wsDeleteView.getWsViewPK().getPk())).getIds()[0]);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSViewPK putView(WSPutView wsView) throws RemoteException {
        try {
            return new WSViewPK(Util.getViewCtrlLocal().putView(WS2VO(wsView.getWsView())).getIds()[0]);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    protected WSView VO2WS(ViewPOJO pojo) throws Exception {
        WSView s = new WSView();
        s.setDescription(pojo.getDescription());
        s.setName(pojo.getName());
        s.setIsTransformerActive(new WSBoolean(pojo.isTransformerActive()));
        s.setTransformerPK(pojo.getTransformerPK());
        String[] bes = null;
        Collection collection = pojo.getSearchableBusinessElements().getList();
        if (collection != null) {
            bes = new String[collection.size()];
            int i = 0;
            for (Object o : collection) {
                String be = (String) o;
                bes[i++] = be;
            }
        }
        s.setSearchableBusinessElements(bes);
        collection = pojo.getViewableBusinessElements().getList();
        if (collection != null) {
            bes = new String[collection.size()];
            int i = 0;
            for (Object o : collection) {
                String be = (String) o;
                bes[i++] = be;
            }
        }
        s.setViewableBusinessElements(bes);
        collection = pojo.getWhereConditions().getList();
        if (collection != null) {
            WSWhereCondition[] wcs = new WSWhereCondition[collection.size()];
            int i = 0;
            for (Object o : collection) {
                WhereCondition wh = (WhereCondition) o;
                wcs[i++] = VO2WS(wh);
            }
            s.setWhereConditions(wcs);
        }
        return s;
    }

    protected ViewPOJO WS2VO(WSView ws) throws Exception {
        ViewPOJO pojo = new ViewPOJO();
        pojo.setName(ws.getName());
        pojo.setDescription(ws.getDescription());
        pojo.setTransformerPK(ws.getTransformerPK());
        pojo.setTransformerActive(ws.getIsTransformerActive().is_true());
        List l = new LinkedList();
        String[] s = ws.getSearchableBusinessElements();
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                l.add(ws.getSearchableBusinessElements()[i]);
            }
        }
        pojo.setSearchableBusinessElements(new ArrayListHolder(l));
        l = new LinkedList();
        s = ws.getViewableBusinessElements();
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                l.add(ws.getViewableBusinessElements()[i]);
            }
        }
        pojo.setViewableBusinessElements(new ArrayListHolder(l));
        l = new LinkedList();
        WSWhereCondition[] whs = ws.getWhereConditions();
        if (whs != null) {
            for (WSWhereCondition wh : whs) {
                l.add(WS2VO(wh));
            }
        }
        pojo.setWhereConditions(new ArrayListHolder(l));

        return pojo;
    }

    protected WSWhereCondition VO2WS(WhereCondition vo) throws Exception {
        WSWhereCondition ws = new WSWhereCondition();
        WSWhereOperator op = WSWhereOperator.CONTAINS;
        String operator = vo.getOperator();
        if (operator.equals(WhereCondition.CONTAINS)) {
            op = WSWhereOperator.CONTAINS;
        } else if (operator.equals(WhereCondition.STRICTCONTAINS)) {
            op = WSWhereOperator.STRICTCONTAINS;
        } else if (operator.equals(WhereCondition.STARTSWITH)) {
            op = WSWhereOperator.STARTSWITH;
        } else if (operator.equals(WhereCondition.JOINS)) {
            op = WSWhereOperator.JOIN;
        } else if (operator.equals(WhereCondition.EQUALS)) {
            op = WSWhereOperator.EQUALS;
        } else if (operator.equals(WhereCondition.NOT_EQUALS)) {
            op = WSWhereOperator.NOT_EQUALS;
        } else if (operator.equals(WhereCondition.GREATER_THAN)) {
            op = WSWhereOperator.GREATER_THAN;
        } else if (operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
            op = WSWhereOperator.GREATER_THAN_OR_EQUAL;
        } else if (operator.equals(WhereCondition.LOWER_THAN)) {
            op = WSWhereOperator.LOWER_THAN;
        } else if (operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
            op = WSWhereOperator.LOWER_THAN_OR_EQUAL;
        } else if (operator.equals(WhereCondition.NO_OPERATOR)) {
            op = WSWhereOperator.NO_OPERATOR;
        } else if (operator.equals(WhereCondition.EMPTY_NULL)) {
            op = WSWhereOperator.EMPTY_NULL;
        }
        String predicate = vo.getStringPredicate();
        WSStringPredicate pr = WSStringPredicate.NONE;
        if ((predicate == null) || predicate.equals(WhereCondition.PRE_NONE)) {
            pr = WSStringPredicate.NONE;
        } else if (predicate.equals(WhereCondition.PRE_AND)) {
            pr = WSStringPredicate.AND;
        } else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
            pr = WSStringPredicate.EXACTLY;
        } else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
            pr = WSStringPredicate.STRICTAND;
        } else if (predicate.equals(WhereCondition.PRE_OR)) {
            pr = WSStringPredicate.OR;
        } else if (predicate.equals(WhereCondition.PRE_NOT)) {
            pr = WSStringPredicate.NOT;
        }
        ws.setLeftPath(vo.getLeftPath());
        ws.setOperator(op);
        ws.setRightValueOrPath(vo.getRightValueOrPath());
        ws.setStringPredicate(pr);
        return ws;
    }

    protected IWhereItem WS2VO(WSWhereItem ws) throws Exception {
        return WS2VO(ws, null);
    }

    protected IWhereItem WS2VO(WSWhereItem ws, WhereConditionFilter wcf) throws Exception {
        if (ws == null) {
            return null;
        }
        if (ws.getWhereAnd() != null) {
            WhereAnd wand = new WhereAnd();
            WSWhereItem[] children = ws.getWhereAnd().getWhereItems();
            if (children != null) {
                for (WSWhereItem child : children) {
                    wand.add(WS2VO(child, wcf));
                }
            }
            return wand;
        } else if (ws.getWhereOr() != null) {
            WhereOr wor = new WhereOr();
            WSWhereItem[] children = ws.getWhereOr().getWhereItems();
            if (children != null) {
                for (WSWhereItem child : children) {
                    wor.add(WS2VO(child, wcf));
                }
            }
            return wor;
        } else if (ws.getWhereCondition() != null) {
            return WS2VO(ws.getWhereCondition(), wcf);
        } else {
            throw new IllegalArgumentException("The WSWhereItem must have at least one child");
        }
    }

    protected WhereCondition WS2VO(WSWhereCondition ws) throws Exception {
        return WS2VO(ws, null);
    }

    protected WhereCondition WS2VO(WSWhereCondition ws, WhereConditionFilter wcf) throws Exception {
        assert ws.getOperator() != null;

        String operator = WhereCondition.CONTAINS;
        if (ws.getOperator().equals(WSWhereOperator.CONTAINS)) {
            operator = WhereCondition.CONTAINS;
        } else if (ws.getOperator().equals(WSWhereOperator.STRICTCONTAINS)) {
            operator = WhereCondition.STRICTCONTAINS;
        } else if (ws.getOperator().equals(WSWhereOperator.STARTSWITH)) {
            operator = WhereCondition.STARTSWITH;
        } else if (ws.getOperator().equals(WSWhereOperator.JOIN)) {
            operator = WhereCondition.JOINS;
        } else if (ws.getOperator().equals(WSWhereOperator.EQUALS)) {
            operator = WhereCondition.EQUALS;
        } else if (ws.getOperator().equals(WSWhereOperator.NOT_EQUALS)) {
            operator = WhereCondition.NOT_EQUALS;
        } else if (ws.getOperator().equals(WSWhereOperator.GREATER_THAN)) {
            operator = WhereCondition.GREATER_THAN;
        } else if (ws.getOperator().equals(WSWhereOperator.GREATER_THAN_OR_EQUAL)) {
            operator = WhereCondition.GREATER_THAN_OR_EQUAL;
        } else if (ws.getOperator().equals(WSWhereOperator.LOWER_THAN)) {
            operator = WhereCondition.LOWER_THAN;
        } else if (ws.getOperator().equals(WSWhereOperator.LOWER_THAN_OR_EQUAL)) {
            operator = WhereCondition.LOWER_THAN_OR_EQUAL;
        } else if (ws.getOperator().equals(WSWhereOperator.NO_OPERATOR)) {
            operator = WhereCondition.NO_OPERATOR;
        } else if (ws.getOperator().equals(WSWhereOperator.EMPTY_NULL)) {
            operator = WhereCondition.EMPTY_NULL;
        } else if (ws.getOperator().equals(WSWhereOperator.FULLTEXTSEARCH)) {
            operator = WhereCondition.FULLTEXTSEARCH;
        } else if (ws.getOperator().equals(WSWhereOperator.CONTAINS_TEXT_OF)) {
            operator = WhereCondition.CONTAINS_TEXT_OF;
        }

        assert ws.getStringPredicate() != null;

        String predicate = WhereCondition.PRE_AND;
        if (ws.getStringPredicate().equals(WSStringPredicate.NONE)) {
            predicate = WhereCondition.PRE_NONE;
        } else if (ws.getStringPredicate().equals(WSStringPredicate.AND)) {
            predicate = WhereCondition.PRE_AND;
        } else if (ws.getStringPredicate().equals(WSStringPredicate.EXACTLY)) {
            predicate = WhereCondition.PRE_EXACTLY;
        } else if (ws.getStringPredicate().equals(WSStringPredicate.STRICTAND)) {
            predicate = WhereCondition.PRE_STRICTAND;
        } else if (ws.getStringPredicate().equals(WSStringPredicate.OR)) {
            predicate = WhereCondition.PRE_OR;
        } else if (ws.getStringPredicate().equals(WSStringPredicate.NOT)) {
            predicate = WhereCondition.PRE_NOT;
        }

        WhereCondition myWhereCondition = new WhereCondition(ws.getLeftPath(), operator, ws.getRightValueOrPath(), predicate,
                ws.isSpellCheck());

        if (wcf != null) {
            wcf.doFilter(myWhereCondition);
        }

        return myWhereCondition;
    }

    /***************************************************************************
     * Search
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
        WSWhereItem whereItem = wsViewSearch.getWhereItem();
        if (whereItem != null && whereItem.getWhereAnd() == null && whereItem.getWhereOr() == null
                && whereItem.getWhereCondition() == null) {
            whereItem = null;
        }

        try {
            Collection res = Util.getItemCtrl2Local().viewSearch(
                    new DataClusterPOJOPK(wsViewSearch.getWsDataClusterPK().getPk()),
                    new ViewPOJOPK(wsViewSearch.getWsViewPK().getPk()), WS2VO(whereItem), wsViewSearch.getSpellTreshold(),
                    wsViewSearch.getOrderBy(), wsViewSearch.getDirection(), wsViewSearch.getSkip(), wsViewSearch.getMaxItems());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
        try {
            if (wsXPathsSearch.getReturnCount() == null) {
                wsXPathsSearch.setReturnCount(Boolean.FALSE);
            }
            Collection res = Util.getItemCtrl2Local().xPathsSearch(
                    new DataClusterPOJOPK(wsXPathsSearch.getWsDataClusterPK().getPk()), wsXPathsSearch.getPivotPath(),
                    new ArrayList<String>(Arrays.asList(wsXPathsSearch.getViewablePaths().getStrings())),
                    WS2VO(wsXPathsSearch.getWhereItem()), wsXPathsSearch.getSpellTreshold(), wsXPathsSearch.getOrderBy(),
                    wsXPathsSearch.getDirection(), wsXPathsSearch.getSkip(), wsXPathsSearch.getMaxItems(),
                    wsXPathsSearch.getReturnCount());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    protected LinkedHashMap WS2VO(WSLinkedHashMap wsLinkedHashMap) throws Exception {
        LinkedHashMap vo = new LinkedHashMap();
        WSGetItemsPivotIndexPivotWithKeysTypedContentEntry[] typedContentEntries = wsLinkedHashMap.getTypedContentEntry();
        for (WSGetItemsPivotIndexPivotWithKeysTypedContentEntry typedContentEntry : typedContentEntries) {
            String key = typedContentEntry.getKey();
            String[] value = typedContentEntry.getValue().getStrings();
            vo.put(key, value);
        }
        return vo;
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
        try {
            Collection res = Util.getItemCtrl2Local().getItemsPivotIndex(
                    wsGetItemsPivotIndex.getClusterName(),
                    wsGetItemsPivotIndex.getMainPivotName(),
                    WS2VO(wsGetItemsPivotIndex.getPivotWithKeys()),
                    wsGetItemsPivotIndex.getIndexPaths().getStrings(),
                    WS2VO(wsGetItemsPivotIndex.getWhereItem()),
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

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getChildrenItems(WSGetChildrenItems wsGetChildrenItems) throws RemoteException {
        try {
            Collection res = Util.getItemCtrl2Local().getChildrenItems(wsGetChildrenItems.getClusterName(),
                    wsGetChildrenItems.getConceptName(), wsGetChildrenItems.getPKXpaths().getStrings(),
                    wsGetChildrenItems.getFKXpath(), wsGetChildrenItems.getLabelXpath(), wsGetChildrenItems.getFatherPK(),
                    WS2VO(wsGetChildrenItems.getWhereItem()), wsGetChildrenItems.getStart(), wsGetChildrenItems.getLimit());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString count(WSCount wsCount) throws RemoteException {
        try {
            String countPath = wsCount.getCountPath();
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, countPath);
            long count = Util.getItemCtrl2Local().count(new DataClusterPOJOPK(wsCount.getWsDataClusterPK().getPk()),
                    wsCount.getCountPath(), WS2VO(wsCount.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsCount.getSpellTreshold());
            return new WSString(count + ""); //$NON-NLS-1$
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
        try {
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, wsGetItems.getConceptName());
            Collection res = Util.getItemCtrl2Local().getItems(new DataClusterPOJOPK(wsGetItems.getWsDataClusterPK().getPk()),
                    wsGetItems.getConceptName(),
                    WS2VO(wsGetItems.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsGetItems.getSpellTreshold(), wsGetItems.getSkip(), wsGetItems.getMaxItems(),
                    wsGetItems.getTotalCountOnFirstResult() == null ? false : wsGetItems.getTotalCountOnFirstResult());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getItemsSort(WSGetItemsSort wsGetItemsSort) throws RemoteException {
        try {
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, wsGetItemsSort.getConceptName());
            Collection res = Util.getItemCtrl2Local().getItems(
                    new DataClusterPOJOPK(wsGetItemsSort.getWsDataClusterPK().getPk()), wsGetItemsSort.getConceptName(),
                    WS2VO(wsGetItemsSort.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsGetItemsSort.getSpellTreshold(), wsGetItemsSort.getSort(), wsGetItemsSort.getDir(),
                    wsGetItemsSort.getSkip(), wsGetItemsSort.getMaxItems(),
                    wsGetItemsSort.getTotalCountOnFirstResult() == null ? false : wsGetItemsSort.getTotalCountOnFirstResult());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {

        return doGetItemPKsByCriteria(wsGetItemPKsByCriteria, false);
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItemPKsByCriteriaResponse getItemPKsByFullCriteria(WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria)
            throws RemoteException {

        return doGetItemPKsByCriteria(wsGetItemPKsByFullCriteria.getWsGetItemPKsByCriteria(),
                wsGetItemPKsByFullCriteria.isUseFTSearch());
    }

    private WSItemPKsByCriteriaResponse doGetItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria, boolean useFTSearch)
            throws RemoteException {
        // With Universe, this method must be reviewed since various concepts
        // may be store in various revisions

        try {

            String dataClusterName = wsGetItemPKsByCriteria.getWsDataClusterPK().getPk();

            // Check if user is allowed to read the cluster
            ILocalUser user = LocalUser.getLocalUser();
            boolean authorized = false;
            if (MDMConfiguration.getAdminUser().equals(user.getUsername())
                    || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) {
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
            String revisionID;
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
            criteria.setCompoundKeyKeywords(false);
            criteria.setFromDate(wsGetItemPKsByCriteria.getFromDate());
            criteria.setToDate(wsGetItemPKsByCriteria.getToDate());
            criteria.setMaxItems(wsGetItemPKsByCriteria.getMaxItems());
            criteria.setSkip(wsGetItemPKsByCriteria.getSkip());
            criteria.setUseFTSearch(useFTSearch);
            List<String> results = com.amalto.core.util.Util.getItemCtrl2Local().getItemPKsByCriteria(criteria);

            XPath xpath = XPathFactory.newInstance().newXPath();
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            WSItemPKsByCriteriaResponseResults[] res = new WSItemPKsByCriteriaResponseResults[results.size()];
            int i = 0;
            for (String result : results) {
                if (i == 0) {
                    res[i++] = new WSItemPKsByCriteriaResponseResults(System.currentTimeMillis(), new WSItemPK(
                            wsGetItemPKsByCriteria.getWsDataClusterPK(), result, null), ""); //$NON-NLS-1$
                    continue;
                }
                Element r = documentBuilder.parse(new InputSource(new StringReader(result))).getDocumentElement();
                long t = new Long(xpath.evaluate("t", r)); //$NON-NLS-1$
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
                LOG.debug(e.getMessage(), e);
            }
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
        try {

            if (wsGetItem.getWsItemPK().getIds() == null) {
                throw (new RemoteException("input ids is null!"));
            }
            ItemPOJOPK pk = new ItemPOJOPK(new DataClusterPOJOPK(wsGetItem.getWsItemPK().getWsDataClusterPK().getPk()), wsGetItem
                    .getWsItemPK().getConceptName(), wsGetItem.getWsItemPK().getIds());

            ItemPOJO pojo = Util.getItemCtrl2Local().getItem(pk);

            return new WSItem(wsGetItem.getWsItemPK().getWsDataClusterPK(), pojo.getDataModelName(), pojo.getDataModelRevision(),
                    wsGetItem.getWsItemPK().getConceptName(), wsGetItem.getWsItemPK().getIds(), pojo.getInsertionTime(),
                    pojo.getTaskId(), pojo.getProjectionAsString());
        } catch (XtentisException e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
        try {
            return new WSBoolean((Util.getItemCtrl2Local().existsItem(
                    new ItemPOJOPK(new DataClusterPOJOPK(wsExistsItem.getWsItemPK().getWsDataClusterPK().getPk()), wsExistsItem
                            .getWsItemPK().getConceptName(), wsExistsItem.getWsItemPK().getIds())) != null));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
        try {
            Collection c = Util.getItemCtrl2Local().quickSearch(
                    new DataClusterPOJOPK(wsQuickSearch.getWsDataClusterPK().getPk()),
                    new ViewPOJOPK(wsQuickSearch.getWsViewPK().getPk()), wsQuickSearch.getSearchedValue(),
                    wsQuickSearch.isMatchAllWords(), wsQuickSearch.getSpellTreshold(), wsQuickSearch.getOrderBy(),
                    wsQuickSearch.getDirection(), wsQuickSearch.getSkip(), wsQuickSearch.getMaxItems());
            if (c == null) {
                return null;
            }
            return new WSStringArray((String[]) c.toArray(new String[c.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    protected WSItemPK POJO2WS(ItemPOJOPK itemPK) throws Exception {
        return new WSItemPK(new WSDataClusterPK(itemPK.getDataClusterPOJOPK().getUniqueId()), itemPK.getConceptName(),
                itemPK.getIds());
    }

    protected ItemPOJOPK WS2POJO(WSItemPK wsItemPK) throws Exception {
        return new ItemPOJOPK(new DataClusterPOJOPK(wsItemPK.getWsDataClusterPK().getPk()), wsItemPK.getConceptName(),
                wsItemPK.getIds());
    }

    protected ItemPOJOPK[] WS2POJO(WSItemPK[] wsItemPKs) throws Exception {
        if (wsItemPKs == null) {
            return null;
        } else {
            ItemPOJOPK[] itemPOJOPKs = new ItemPOJOPK[wsItemPKs.length];
            for (int i = 0; i < itemPOJOPKs.length; i++) {
                itemPOJOPKs[i] = WS2POJO(wsItemPKs[i]);
            }
            return itemPOJOPKs;
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString getBusinessConceptValue(WSGetBusinessConceptValue wsGetBusinessConceptValue) throws RemoteException {
        try {
            ItemPOJO iv = Util.getItemCtrl2Local().getItem(
                    new ItemPOJOPK(new DataClusterPOJOPK(wsGetBusinessConceptValue.getWsDataClusterPK().getPk()),
                            wsGetBusinessConceptValue.getWsBusinessConceptPK().getConceptName(), wsGetBusinessConceptValue
                                    .getWsBusinessConceptPK().getIds()));
            return new WSString(itemAsString(iv));

        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
        try {
            Collection res = Util.getItemCtrl2Local().getFullPathValues(
                    new DataClusterPOJOPK(wsGetFullPathValues.getWsDataClusterPK().getPk()), wsGetFullPathValues.getFullPath(),
                    WS2VO(wsGetFullPathValues.getWhereItem()), wsGetFullPathValues.getSpellThreshold(),
                    wsGetFullPathValues.getOrderBy(), wsGetFullPathValues.getDirection());
            if (res == null) {
                return null;
            }
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * Serializes the object to an xml string
     * 
     * @return the xml string
     * 
     */
    protected String itemAsString(ItemPOJO iv) throws Exception {
        StringBuilder item = new StringBuilder();
        item.append("<businessconcept><cluster>").append(iv.getDataClusterPOJOPK().getUniqueId()).append("</cluster>"); //$NON-NLS-1$ //$NON-NLS-2$ 
        String[] ids = iv.getItemIds();
        for (String id : ids) {
            item.append("<id>").append(id).append("</id>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        item.append("<lastmodifiedtime>").append(iv.getInsertionTime()).append("</lastmodifiedtime>"); //$NON-NLS-1$ //$NON-NLS-2$
        item.append("<projection>").append(iv.getProjection()).append("</projection>"); //$NON-NLS-1$ //$NON-NLS-2$
        item.append("</businessconcept>"); //$NON-NLS-1$
        return item.toString();
    }

    /***************************************************************************
     * Put Item
     * **************************************************************************/
    /**
     * partial put item
     * 
     * @throws RemoteException
     */
    public WSItemPK partialPutItem(WSPartialPutItem partialPutItem) throws RemoteException {
        try {
            SaverSession session = SaverSession.newSession();
            DocumentSaver saver = SaverHelper.saveItem(partialPutItem, session);
            // Cause items being saved to be committed to database.
            session.end();
            String[] savedId = saver.getSavedId();
            String savedConceptName = saver.getSavedConceptName();
            return new WSItemPK(new WSDataClusterPK(partialPutItem.getDatacluster()), savedConceptName, savedId);
        } catch (Exception e) {
            LOG.error("Could not do partial update.", e);
            throw new RemoteException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @param wsPutItem The record to be added/updated in MDM.
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     * @return The PK of the record created/updated.
     * @throws java.rmi.RemoteException In case of server exception.
     */
    public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
        try {
            WSDataClusterPK dataClusterPK = wsPutItem.getWsDataClusterPK();
            WSDataModelPK dataModelPK = wsPutItem.getWsDataModelPK();
            String dataClusterName = dataClusterPK.getPk();
            String dataModelName = dataModelPK.getPk();
            SaverSession session = SaverSession.newSession();
            DocumentSaver saver;
            try {
                saver = SaverHelper.saveItem(wsPutItem, session, dataClusterName, dataModelName);
            } catch (Exception e) {
                try {
                    session.abort();
                } catch (Exception e1) {
                    LOG.error("Could not abort save session.", e1);
                }
                throw new RuntimeException(e);
            }
            // Cause items being saved to be committed to database.
            session.end();
            String[] savedId = saver.getSavedId();
            String savedConceptName = saver.getSavedConceptName();
            return new WSItemPK(dataClusterPK, savedConceptName, savedId);
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            // TMDM-5594: Original cause was somehow lost during serialization,implementing a workaround here
            Throwable cause = e.getCause();
            if (cause == null) {
                throw new RemoteException(e.getLocalizedMessage(), e);
            } else {
                throw new RemoteException((cause.getCause() == null ? cause.getLocalizedMessage() : cause.getCause()
                        .getLocalizedMessage()), e);
            }
        }
    }

    /**
     * @param wsPutItemArray Items to save
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     * @return An array of ids saved to database.
     * @throws java.rmi.RemoteException In case of server error
     */
    public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
        WSPutItem[] items = wsPutItemArray.getWsPutItem();
        try {
            List<WSItemPK> pks = new LinkedList<WSItemPK>();
            SaverSession session = SaverSession.newSession();
            for (WSPutItem item : items) {
                String dataClusterName = item.getWsDataClusterPK().getPk();
                String dataModelName = item.getWsDataModelPK().getPk();

                DocumentSaver saver;
                try {
                    saver = SaverHelper.saveItem(item, session, dataClusterName, dataModelName);
                } catch (Exception e) {
                    try {
                        session.abort();
                    } catch (Exception e1) {
                        LOG.error("Could not abort save session.", e1);
                    }
                    throw new RuntimeException(e);
                }
                pks.add(new WSItemPK(new WSDataClusterPK(), saver.getSavedConceptName(), saver.getSavedId()));
            }
            // Cause items being saved to be committed to database.
            session.end();
            return new WSItemPKArray(pks.toArray(new WSItemPK[pks.size()]));
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @param wsPutItemWithReportArray Records to be added to MDM.
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     * @return An array of PKs for the records created/updated.
     * @throws java.rmi.RemoteException In case of server error.
     */
    public WSItemPKArray putItemWithReportArray(com.amalto.core.webservice.WSPutItemWithReportArray wsPutItemWithReportArray)
            throws RemoteException {
        try {
            WSPutItemWithReport[] items = wsPutItemWithReportArray.getWsPutItem();
            List<WSItemPK> pks = new LinkedList<WSItemPK>();
            SaverSession session = SaverSession.newSession();
            for (WSPutItemWithReport item : items) {
                WSPutItem wsPutItem = item.getWsPutItem();
                String source = item.getSource();
                String dataClusterName = wsPutItem.getWsDataClusterPK().getPk();
                String dataModelName = wsPutItem.getWsDataModelPK().getPk();
                DocumentSaver saver;
                try {
                    saver = SaverHelper.saveItemWithReport(wsPutItem, session, dataClusterName, dataModelName, source,
                            item.getInvokeBeforeSaving());
                    // Expected (legacy) behavior: set the before saving message as source.
                    item.setSource(saver.getBeforeSavingMessage());
                } catch (SaveException e) {
                    try {
                        session.abort();
                    } catch (Exception e1) {
                        LOG.error("Could not abort save session.", e1);
                    }
                    // Expected (legacy) behavior: set the before saving message as source.
                    item.setSource(e.getBeforeSavingMessage());
                    throw new RemoteException("Could not save record.", e);
                }
                pks.add(new WSItemPK(new WSDataClusterPK(), saver.getSavedConceptName(), saver.getSavedId()));
            }
            // Cause items being saved to be committed to database.
            session.end();
            return new WSItemPKArray(pks.toArray(new WSItemPK[pks.size()]));
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @param wsPutItemWithReport Object that describe the record to be added/updated.
     * @return The PK of the newly inserted document.
     * @throws java.rmi.RemoteException In case of server exception.
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItemPK putItemWithReport(com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        try {
            WSPutItem wsPutItem = wsPutItemWithReport.getWsPutItem();
            WSDataClusterPK dataClusterPK = wsPutItem.getWsDataClusterPK();
            WSDataModelPK dataModelPK = wsPutItem.getWsDataModelPK();
            String dataClusterName = dataClusterPK.getPk();
            String dataModelName = dataModelPK.getPk();
            SaverSession session = SaverSession.newSession();
            DocumentSaver saver;
            try {
                saver = SaverHelper.saveItemWithReport(wsPutItem, session, dataClusterName, dataModelName,
                        wsPutItemWithReport.getSource(), wsPutItemWithReport.getInvokeBeforeSaving());
                // Expected (legacy) behavior: set the before saving message as source.
                wsPutItemWithReport.setSource(saver.getBeforeSavingMessage());
            } catch (SaveException e) {
                try {
                    session.abort();
                } catch (Exception e1) {
                    LOG.error("Could not abort save session.", e1);
                }
                ValidateException ve = Util.getException(e, ValidateException.class);
                if (ve != null) {
                    throw e;
                }
                // Expected (legacy) behavior: set the before saving message as source.
                wsPutItemWithReport.setSource(e.getBeforeSavingMessage());
                throw new RemoteException("Could not save record.", e);
            }
            // Cause items being saved to be committed to database.
            session.end();

            String[] savedId = saver.getSavedId();
            String conceptName = saver.getSavedConceptName();
            return new WSItemPK(dataClusterPK, conceptName, savedId);
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @param wsPutItemWithCustomReport Information about a put item with report that includes a special user name.
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     * @return The PK of the newly created record.
     * @throws java.rmi.RemoteException In case of server exception.
     */
    public WSItemPK putItemWithCustomReport(com.amalto.core.webservice.WSPutItemWithCustomReport wsPutItemWithCustomReport)
            throws RemoteException {
        try {
            WSPutItemWithReport wsPutItemWithReport = wsPutItemWithCustomReport.getWsPutItemWithReport();
            WSPutItem wsPutItem = wsPutItemWithReport.getWsPutItem();
            WSDataClusterPK dataClusterPK = wsPutItem.getWsDataClusterPK();
            WSDataModelPK dataModelPK = wsPutItem.getWsDataModelPK();
            String dataClusterName = dataClusterPK.getPk();
            String dataModelName = dataModelPK.getPk();
            // This method uses a special user
            SaverSession session = SaverSession.newUserSession(wsPutItemWithCustomReport.getUser());
            DocumentSaver saver;
            try {
                saver = SaverHelper.saveItemWithReport(wsPutItem, session, dataClusterName, dataModelName,
                        wsPutItemWithReport.getSource(), wsPutItemWithReport.getInvokeBeforeSaving());
                // Expected (legacy) behavior: set the before saving message as source.
                wsPutItemWithReport.setSource(saver.getBeforeSavingMessage());
                // Cause items being saved to be committed to database.
                session.end();
            } catch (SaveException e) {
                try {
                    session.abort();
                } catch (Exception e1) {
                    LOG.error("Could not abort save session.", e1);
                }
                // Expected (legacy) behavior: set the before saving message as source.
                wsPutItemWithReport.setSource(e.getBeforeSavingMessage());
                throw new RemoteException("Could not save record.", e);
            }
            String[] savedId = saver.getSavedId();
            String conceptName = saver.getSavedConceptName();
            return new WSItemPK(dataClusterPK, conceptName, savedId);
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Extract Items
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
        throw new RemoteException("Not supported.");
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView)
            throws RemoteException {
        try {
            TransformerContext context = Util.getItemCtrl2Local().extractUsingTransformerThroughView(
                    new DataClusterPOJOPK(wsExtractUsingTransformerThruView.getWsDataClusterPK().getPk()),
                    new TransformerV2POJOPK(wsExtractUsingTransformerThruView.getWsTransformerPK().getPk()),
                    new ViewPOJOPK(wsExtractUsingTransformerThruView.getWsViewPK().getPk()),
                    WS2VO(wsExtractUsingTransformerThruView.getWhereItem()),
                    wsExtractUsingTransformerThruView.getSpellTreshold(), wsExtractUsingTransformerThruView.getOrderBy(),
                    wsExtractUsingTransformerThruView.getDirection(), wsExtractUsingTransformerThruView.getSkip(),
                    wsExtractUsingTransformerThruView.getMaxItems());
            HashMap<String, TypedContent> pipeline = context.getPipelineClone();
            return POJO2WS(pipeline);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Delete Items
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItemPK deleteItem(WSDeleteItem wsDeleteItem) throws RemoteException {
        try {
            ItemPOJOPK itemPK = new ItemPOJOPK(new DataClusterPOJOPK(wsDeleteItem.getWsItemPK().getWsDataClusterPK().getPk()),
                    wsDeleteItem.getWsItemPK().getConceptName(), wsDeleteItem.getWsItemPK().getIds());
            Util.getItemCtrl2Local().deleteItem(itemPK, wsDeleteItem.getOverride());
            return wsDeleteItem.getWsItemPK();
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    private void pushToUpdateReport(WSDeleteItemWithReport wsDeleteItem, String dataClusterPK, String dataModelPK,
            String concept, String[] ids, boolean trigger) throws Exception {

        ILocalUser user = LocalUser.getLocalUser();
        String source = wsDeleteItem.getSource();
        String operationType = wsDeleteItem.getOperateType();
        Map<String, UpdateReportItemPOJO> updateReportItemsMap = new HashMap<String, UpdateReportItemPOJO>();

        String userName;
        if (wsDeleteItem.getUser() != null && wsDeleteItem.getUser().length() > 0) {
            userName = wsDeleteItem.getUser();
        } else {
            userName = user.getUsername();
        }
        String revisionID = ""; //$NON-NLS-1$
        UniversePOJO universe = user.getUniverse();
        if (universe != null) {
            revisionID = universe.getConceptRevisionID(concept);
        }
        UpdateReportPOJO updateReportPOJO = new UpdateReportPOJO(concept, Util.joinStrings(ids, "."), operationType, //$NON-NLS-1$
                source, System.currentTimeMillis(), dataClusterPK, dataModelPK, userName, revisionID, updateReportItemsMap);
        WSItemPK itemPK = putItem(new WSPutItem(new WSDataClusterPK(UpdateReportPOJO.DATA_CLUSTER), updateReportPOJO.serialize(),
                new WSDataModelPK(UpdateReportPOJO.DATA_MODEL), false));
        if (trigger) {
            routeItemV2(new WSRouteItemV2(itemPK));
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString deleteItemWithReport(WSDeleteItemWithReport wsDeleteItem) throws RemoteException {
        try {
            String dataClusterPK = wsDeleteItem.getWsItemPK().getWsDataClusterPK().getPk();
            String concept = wsDeleteItem.getWsItemPK().getConceptName();
            String[] ids = wsDeleteItem.getWsItemPK().getIds();

            ItemPOJOPK pk = new ItemPOJOPK(new DataClusterPOJOPK(dataClusterPK), concept, ids);
            ItemPOJO pojo = Util.getItemCtrl2Local().getItem(pk);
            if (pojo == null) {
                throw new EntityNotFoundException(pk);
            }
            String dataModelPK = pojo.getDataModelName();

            if (UpdateReportPOJO.OPERATION_TYPE_LOGICAL_DELETE.equals(wsDeleteItem.getOperateType())) {
                dropItem(new WSDropItem(wsDeleteItem.getWsItemPK(), wsDeleteItem.getUpdatePath(), wsDeleteItem.getOverride()));
                if (wsDeleteItem.getPushToUpdateReport()) {
                    pushToUpdateReport(wsDeleteItem, dataClusterPK, dataModelPK, concept, ids,
                            wsDeleteItem.getInvokeBeforeSaving());
                }
                return new WSString("logical delete item successful!");
            }
            String outputErrorMessage = null;
            String errorCode = null;
            String message = ""; //$NON-NLS-1$
            if (wsDeleteItem.getInvokeBeforeSaving()) {
                outputErrorMessage = com.amalto.core.util.Util.beforeDeleting(dataClusterPK, concept, ids);
                if (outputErrorMessage != null) {
                    Document doc = Util.parse(outputErrorMessage);
                    // TODO what if multiple error nodes ?
                    String xpath = "/report/message"; //$NON-NLS-1$
                    Node errorNode = (Node) XPathFactory.newInstance().newXPath().evaluate(xpath, doc, XPathConstants.NODE);
                    if (errorNode instanceof Element) {
                        Element errorElement = (Element) errorNode;
                        errorCode = errorElement.getAttribute("type"); //$NON-NLS-1$
                        Node child = errorElement.getFirstChild();
                        if (child instanceof Text) {
                            message = child.getTextContent();
                        }
                    }
                }
            }
            if (outputErrorMessage == null || "info".equals(errorCode)) { //$NON-NLS-1$
                if (ids != null) {
                    WSItemPK wsItem = deleteItem(new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids),
                            wsDeleteItem.getOverride()));
                    if (wsItem != null && !UpdateReportPOJO.DATA_CLUSTER.equals(dataClusterPK)) {
                        if (wsDeleteItem.getPushToUpdateReport()) {
                            pushToUpdateReport(wsDeleteItem, dataClusterPK, dataModelPK, concept, ids,
                                    wsDeleteItem.getInvokeBeforeSaving());
                        }
                    } else {
                        message = "ERROR - Unable to delete item";
                    }
                    if (outputErrorMessage != null) {
                        message = "The validation process completed successfully. The record was deleted successfully.";
                    }
                } else {
                    if (outputErrorMessage != null) {
                        message = "Could not retrieve the validation process result. An error might have occurred. The record was not deleted.";
                    }
                    return new WSString(message + " - No update report was produced");
                }
            } else {
                // Anything but 0 is unsuccessful
                if (message == null || message.length() == 0) {
                    message = "Could not retrieve the validation process result. An error might have occurred. The record was not deleted.";
                }
            }
            return new WSString(message);
        } catch (Exception e) {
            throw new RemoteException(e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSInt deleteItems(WSDeleteItems wsDeleteItems) throws RemoteException {
        try {
            int numItems = Util.getItemCtrl2Local().deleteItems(
                    new DataClusterPOJOPK(wsDeleteItems.getWsDataClusterPK().getPk()), wsDeleteItems.getConceptName(),
                    WS2VO(wsDeleteItems.getWsWhereItem()), wsDeleteItems.getSpellTreshold(), wsDeleteItems.getOverride());
            return new WSInt(numItems);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDroppedItemPK dropItem(WSDropItem wsDropItem) throws RemoteException {
        try {
            WSItemPK wsItemPK = wsDropItem.getWsItemPK();
            String partPath = wsDropItem.getPartPath();
            DroppedItemPOJOPK droppedItemPOJOPK = Util.getItemCtrl2Local().dropItem(WS2POJO(wsItemPK), partPath,
                    wsDropItem.getOverride());
            return POJO2WS(droppedItemPOJOPK);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * DirectQuery
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "administration, DataManagerAdministration" view-type = "service-endpoint"
     */
    public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
        try {
            DataClusterPOJOPK dcpk = (wsRunQuery.getWsDataClusterPK() == null) ? null : new DataClusterPOJOPK(wsRunQuery
                    .getWsDataClusterPK().getPk());
            Collection<String> result = Util.getItemCtrl2Local().runQuery(wsRunQuery.getRevisionID(), dcpk,
                    wsRunQuery.getQuery(), wsRunQuery.getParameters());
            // stored procedure may modify the db, so we need to clear the cache
            Util.getXmlServerCtrlLocal().clearCache();
            return new WSStringArray(result.toArray(new String[result.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * SERVICES
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, "amalto/local/service/" + serviceName.getValue()); //$NON-NLS-1$
            String desc = ""; //$NON-NLS-1$
            Object descObject = Util.getMethod(service, "getDescription").invoke(service, ""); //$NON-NLS-1$ //$NON-NLS-2$
            if (descObject != null) {
                desc = (String) descObject;
            }
            String configuration = ""; //$NON-NLS-1$
            Object configurationObject = Util.getMethod(service, "getConfiguration").invoke(service, ""); //$NON-NLS-1$ //$NON-NLS-2$
            if (configurationObject != null) {
                configuration = (String) configurationObject;
            }
            String doc = ""; //$NON-NLS-1$
            String schema = ""; //$NON-NLS-1$
            String defaultConf = ""; //$NON-NLS-1$
            try {
                Method getDocumentationMethod = Util.getMethod(service, "getDocumentation"); //$NON-NLS-1$
                if (getDocumentationMethod != null) {
                    Object docObject = getDocumentationMethod.invoke(service, ""); //$NON-NLS-1$
                    if (docObject != null) {
                        doc = (String) docObject;
                    }
                }
                Method getDefaultConfigurationMethod = Util.getMethod(service, "getDefaultConfiguration"); //$NON-NLS-1$
                if (getDefaultConfigurationMethod != null) {
                    Object defaultConfObject = getDefaultConfigurationMethod.invoke(service);
                    if (defaultConfObject != null) {
                        defaultConf = (String) defaultConfObject;
                    }
                }

                Method getConfigurationSchemaMethod = Util.getMethod(service, "getConfigurationSchema"); //$NON-NLS-1$
                if (getConfigurationSchemaMethod != null) {
                    Object schemaObject = getConfigurationSchemaMethod.invoke(service);
                    if (schemaObject != null) {
                        schema = (String) schemaObject;
                    }
                }
            } catch (Exception e) {
                LOG.error("IXtentisWSDelegator.getServiceDocument error.", e);
            }
            return new WSServiceGetDocument(desc, configuration, doc, schema, defaultConf);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, wsGetConfiguration.getJndiName());

            String configuration = (String) Util.getMethod(service, "getConfiguration").invoke(service, //$NON-NLS-1$
                    wsGetConfiguration.getOptionalParameter());
            return new WSString(configuration);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, "amalto/local/service/" + serviceName.getJndiName()); //$NON-NLS-1$

            Boolean result = (Boolean) Util.getMethod(service, "checkConfigure").invoke(service, //$NON-NLS-1$
                    serviceName.getConf());
            return new WSCheckServiceConfigResponse(result);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, wsPutConfiguration.getJndiName());
            Util.getMethod(service, "putConfiguration").invoke(service, wsPutConfiguration.getConfiguration()); //$NON-NLS-1$
            return new WSString(wsPutConfiguration.getConfiguration());
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("serviceAction() " + wsServiceAction.getJndiName());
        }
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsServiceAction.getJndiName());
            String result;
            if (WSServiceActionCode.EXECUTE.equals(wsServiceAction.getWsAction())) {
                Method method = com.amalto.core.util.Util.getMethod(service, wsServiceAction.getMethodName());
                result = (String) method.invoke(service, wsServiceAction.getMethodParameters());
            } else {
                if (WSServiceActionCode.START.equals(wsServiceAction.getWsAction())) {
                    com.amalto.core.util.Util.getMethod(service, "start").invoke(service); //$NON-NLS-1$
                } else if (WSServiceActionCode.STOP.equals(wsServiceAction.getWsAction())) {
                    com.amalto.core.util.Util.getMethod(service, "stop").invoke(service); //$NON-NLS-1$
                }
                result = (String) com.amalto.core.util.Util.getMethod(service, "getStatus").invoke(service); //$NON-NLS-1$
            }
            return new WSString(result);
        } catch (com.amalto.core.util.XtentisException e) {
            LOG.error(e.getMessage(), e);
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
        try {
            ArrayList<WSServicesListItem> wsList = new ArrayList<WSServicesListItem>();
            List<String> jndiList = Util.getRuntimeServiceJndiList();
            String serviceJndiPrefix = "amalto/local/service/"; //$NON-NLS-1$
            for (String jndi : jndiList) {
                WSServicesListItem item = new WSServicesListItem();
                item.setJndiName(jndi.replaceAll(serviceJndiPrefix, "")); //$NON-NLS-1$
                wsList.add(item);
            }
            return new WSServicesList(wsList.toArray(new WSServicesListItem[wsList.size()]));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Ping - test that we can authenticate by getting a server response
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString ping() throws RemoteException {
        try {
            return new WSString("OK");
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Xtentis JCA Connector support
     * **************************************************************************/

    protected transient ConnectionFactory cxFactory = null;

    protected Connection getConnection(String JNDIName) throws RemoteException {
        try {
            if (cxFactory == null) {
                cxFactory = (ConnectionFactory) (new InitialContext()).lookup(JNDIName);
            }
            return cxFactory.getConnection();
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /***************************************************************************
     * Stored Procedure
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJOPK pk = ctrl.removeStoredProcedure(new StoredProcedurePOJOPK(wsStoredProcedureDelete
                    .getWsStoredProcedurePK().getPk()));
            return new WSStoredProcedurePK(pk.getIds()[0]);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = Util.getStoredProcedureCtrlLocal();
            DataClusterPOJOPK dcpk = null;
            if (wsExecuteStoredProcedure.getWsDataClusterPK() != null) {
                dcpk = new DataClusterPOJOPK(wsExecuteStoredProcedure.getWsDataClusterPK().getPk());
            }
            Collection collection = ctrl.execute(new StoredProcedurePOJOPK(wsExecuteStoredProcedure.getWsStoredProcedurePK()
                    .getPk()), wsExecuteStoredProcedure.getRevisionID(), dcpk, wsExecuteStoredProcedure.getParameters());
            if (collection == null) {
                return null;
            }
            String[] documents = new String[collection.size()];
            int i = 0;
            for (Object o : collection) {
                documents[i++] = (String) o;
            }
            return new WSStringArray(documents);
        } catch (Exception e) {
            throw new RemoteException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJO pojo = ctrl.getStoredProcedure(new StoredProcedurePOJOPK(wsGetStoredProcedure
                    .getWsStoredProcedurePK().getPk()));
            return POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJO pojo = ctrl.existsStoredProcedure(new StoredProcedurePOJOPK(wsExistsStoredProcedure
                    .getWsStoredProcedurePK().getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = Util.getStoredProcedureCtrlLocal();
            Collection collection = ctrl.getStoredProcedurePKs(regex.getRegex());
            if (collection == null) {
                return null;
            }
            WSStoredProcedurePK[] pks = new WSStoredProcedurePK[collection.size()];
            int i = 0;
            for (Object o : collection) {
                pks[i++] = new WSStoredProcedurePK(((StoredProcedurePOJOPK) o).getIds()[0]);
            }
            return new WSStoredProcedurePKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = Util.getStoredProcedureCtrlLocal();
            StoredProcedurePOJOPK pk = ctrl.putStoredProcedure(WS2POJO(wsStoredProcedure.getWsStoredProcedure()));
            return new WSStoredProcedurePK(pk.getIds()[0]);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    protected WSStoredProcedure POJO2WS(StoredProcedurePOJO storedProcedurePOJO) throws Exception {
        WSStoredProcedure ws = new WSStoredProcedure();
        ws.setName(storedProcedurePOJO.getName());
        ws.setDescription(storedProcedurePOJO.getDescription());
        ws.setProcedure(storedProcedurePOJO.getProcedure());
        ws.setRefreshCache(storedProcedurePOJO.isRefreshCache());
        return ws;
    }

    protected StoredProcedurePOJO WS2POJO(WSStoredProcedure wsStoredProcedure) throws Exception {
        StoredProcedurePOJO pojo = new StoredProcedurePOJO();
        pojo.setName(wsStoredProcedure.getName());
        pojo.setDescription(wsStoredProcedure.getDescription());
        pojo.setProcedure(wsStoredProcedure.getProcedure());
        pojo.setRefreshCache(wsStoredProcedure.getRefreshCache());
        return pojo;
    }

    /***************************************************************************
     * Menu
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            return new WSMenuPK(ctrl.removeMenu(new MenuPOJOPK(wsMenuDelete.getWsMenuPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            MenuPOJO pojo = ctrl.getMenu(new MenuPOJOPK(wsGetMenu.getWsMenuPK().getPk()));
            return POJO2WS(pojo);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            MenuPOJO pojo = ctrl.existsMenu(new MenuPOJOPK(wsExistsMenu.getWsMenuPK().getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            Collection collection = ctrl.getMenuPKs(regex.getRegex());
            if (collection == null) {
                return null;
            }
            WSMenuPK[] pks = new WSMenuPK[collection.size()];
            int i = 0;
            for (Object o : collection) {
                pks[i++] = new WSMenuPK(((MenuPOJOPK) o).getUniqueId());
            }
            return new WSMenuPKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            MenuPOJOPK pk = ctrl.putMenu(WS2POJO(wsMenu.getWsMenu()));
            return new WSMenuPK(pk.getUniqueId());
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    protected WSMenu POJO2WS(MenuPOJO pojo) throws Exception {
        WSMenu ws = new WSMenu();
        ws.setName(pojo.getName());
        ws.setDescription(pojo.getDescription());
        if (pojo.getMenuEntries() != null) {
            WSMenuEntry[] wsSubMenus = new WSMenuEntry[pojo.getMenuEntries().size()];
            int i = 0;
            for (MenuEntryPOJO menuEntry : pojo.getMenuEntries()) {
                wsSubMenus[i++] = POJO2WS(menuEntry);
            }
            ws.setMenuEntries(wsSubMenus);
        }
        return ws;
    }

    protected MenuPOJO WS2POJO(WSMenu ws) throws Exception {
        MenuPOJO pojo = new MenuPOJO();
        pojo.setName(ws.getName());
        pojo.setDescription(ws.getDescription());
        ArrayList<MenuEntryPOJO> menuEntries = new ArrayList<MenuEntryPOJO>();
        if (ws.getMenuEntries() != null) {
            for (int i = 0; i < ws.getMenuEntries().length; i++) {
                menuEntries.add(WS2POJO(ws.getMenuEntries()[i]));
            }
        }
        pojo.setMenuEntries(menuEntries);
        return pojo;
    }

    protected WSMenuEntry POJO2WS(MenuEntryPOJO pojo) throws Exception {
        WSMenuEntry ws = new WSMenuEntry();
        ws.setId(pojo.getId());
        Set<String> languages = pojo.getDescriptions().keySet();
        WSMenuMenuEntriesDescriptions[] wsDescriptions = new WSMenuMenuEntriesDescriptions[languages.size()];
        int i = 0;
        for (String language : languages) {
            wsDescriptions[i] = new WSMenuMenuEntriesDescriptions();
            wsDescriptions[i].setLanguage(language);
            wsDescriptions[i].setLabel(pojo.getDescriptions().get(language));
            i++;
        }
        ws.setDescriptions(wsDescriptions);
        ws.setContext(pojo.getContext());
        ws.setApplication(pojo.getApplication());
        ws.setIcon(pojo.getIcon());
        if (pojo.getSubMenus() != null) {
            WSMenuEntry[] wsSubMenus = new WSMenuEntry[pojo.getSubMenus().size()];
            i = 0;
            for (MenuEntryPOJO menuEntry : pojo.getSubMenus()) {
                wsSubMenus[i++] = POJO2WS(menuEntry);
            }
            ws.setSubMenus(wsSubMenus);
        }
        return ws;
    }

    protected MenuEntryPOJO WS2POJO(WSMenuEntry ws) throws Exception {
        MenuEntryPOJO pojo = new MenuEntryPOJO();
        pojo.setId(ws.getId());
        HashMap<String, String> descriptions = new HashMap<String, String>();
        if (ws.getDescriptions() != null) {
            for (int i = 0; i < ws.getDescriptions().length; i++) {
                descriptions.put(ws.getDescriptions()[i].getLanguage(), ws.getDescriptions()[i].getLabel());
            }
        }
        pojo.setDescriptions(descriptions);
        pojo.setContext(ws.getContext());
        pojo.setApplication(ws.getApplication());
        pojo.setIcon(ws.getIcon());
        ArrayList<MenuEntryPOJO> subMenus = new ArrayList<MenuEntryPOJO>();
        if (ws.getSubMenus() != null) {
            for (int i = 0; i < ws.getSubMenus().length; i++) {
                subMenus.add(WS2POJO(ws.getSubMenus()[i]));
            }
        }
        pojo.setSubMenus(subMenus);
        return pojo;
    }

    /***************************************************************************
     * BackgroundJob
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsBackgroundJobGet) throws RemoteException {
        try {
            return POJO2WS(Util.getBackgroundJobCtrlLocal().getBackgroundJob(new BackgroundJobPOJOPK(wsBackgroundJobGet.getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs wsFindBackgroundJobPKs) throws RemoteException {
        try {
            throw new RemoteException("WSBackgroundJobPKArray is not implemented in this version of the core");
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsPutJob) throws RemoteException {
        try {
            return new WSBackgroundJobPK(Util.getBackgroundJobCtrlLocal()
                    .putBackgroundJob(WS2POJO(wsPutJob.getWsBackgroundJob())).getUniqueId());
        } catch (Exception e) {
            throw new EJBException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
        }
    }

    protected WSBackgroundJob POJO2WS(BackgroundJobPOJO pojo) throws Exception {
        WSBackgroundJob s = new WSBackgroundJob();
        s.setId(pojo.getId());
        s.setDescription(pojo.getDescription());
        switch (pojo.getStatus()) {
        case 0:
            s.setStatus(BackgroundJobStatusType.COMPLETED);
            break;
        case 1:
            s.setStatus(BackgroundJobStatusType.RUNNING);
            break;
        case 2:
            s.setStatus(BackgroundJobStatusType.SUSPENDED);
            break;
        case 3:
            s.setStatus(BackgroundJobStatusType.STOPPED);
            break;
        case 4:
            s.setStatus(BackgroundJobStatusType.CANCEL_REQUESTED);
            break;
        case 5:
            s.setStatus(BackgroundJobStatusType.SCHEDULED);
            break;
        default:
            throw new Exception("Unknown BackgroundJob Status: " + pojo.getStatus());
        }
        s.setMessage(pojo.getMessage());
        s.setPercentage(pojo.getPercentage());
        s.setTimestamp(pojo.getTimestamp());
        s.setPipeline(pojo.getWsPipeline());
        return s;
    }

    protected BackgroundJobPOJO WS2POJO(WSBackgroundJob ws) throws Exception {
        BackgroundJobPOJO pojo = new BackgroundJobPOJO();
        pojo.setId(ws.getId());
        pojo.setMessage(ws.getMessage());
        pojo.setDescription(ws.getDescription());
        pojo.setPercentage(ws.getPercentage());
        pojo.setTimestamp(ws.getTimestamp());
        if (ws.getStatus().equals(BackgroundJobStatusType.CANCEL_REQUESTED)) {
            pojo.setStatus(BackgroundJobPOJO._CANCEL_REQUESTED_);
        } else if (ws.getStatus().equals(BackgroundJobStatusType.COMPLETED)) {
            pojo.setStatus(BackgroundJobPOJO._COMPLETED_);
        } else if (ws.getStatus().equals(BackgroundJobStatusType.RUNNING)) {
            pojo.setStatus(BackgroundJobPOJO._RUNNING_);
        } else if (ws.getStatus().equals(BackgroundJobStatusType.SCHEDULED)) {
            pojo.setStatus(BackgroundJobPOJO._SCHEDULED_);
        } else if (ws.getStatus().equals(BackgroundJobStatusType.STOPPED)) {
            pojo.setStatus(BackgroundJobPOJO._STOPPED_);
        } else if (ws.getStatus().equals(BackgroundJobStatusType.SUSPENDED)) {
            pojo.setStatus(BackgroundJobPOJO._SUSPENDED_);
        }
        pojo.setWsPipeline(ws.getPipeline());
        // we do not rewrite the pipeline
        return pojo;
    }

    /***************************************************************************
     * Universe
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
        try {
            // Fetch the user
            ILocalUser user = LocalUser.getLocalUser();
            return POJO2WS(user.getUniverse());
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    protected WSUniverse POJO2WS(UniversePOJO universePOJO) throws Exception {
        WSUniverse ws = new WSUniverse();
        ws.setName(universePOJO.getName());
        ws.setDescription(universePOJO.getDescription());
        // objects
        Set<String> objectTypes = universePOJO.getXtentisObjectsRevisionIDs().keySet();
        ArrayList<WSUniverseXtentisObjectsRevisionIDs> wsObjectsToRevisionIDs = new ArrayList<WSUniverseXtentisObjectsRevisionIDs>();
        for (String objectType : objectTypes) {
            String revisionID = universePOJO.getXtentisObjectsRevisionIDs().get(objectType);
            wsObjectsToRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs(objectType, revisionID));
        }
        ws.setXtentisObjectsRevisionIDs(wsObjectsToRevisionIDs
                .toArray(new WSUniverseXtentisObjectsRevisionIDs[wsObjectsToRevisionIDs.size()]));
        // default items
        ws.setDefaultItemsRevisionID(universePOJO.getDefaultItemRevisionID());
        // items
        Set<String> patterns = universePOJO.getItemsRevisionIDs().keySet();
        ArrayList<WSUniverseItemsRevisionIDs> wsItemsToRevisionIDs = new ArrayList<WSUniverseItemsRevisionIDs>();
        for (String pattern : patterns) {
            String revisionID = universePOJO.getItemsRevisionIDs().get(pattern);
            wsItemsToRevisionIDs.add(new WSUniverseItemsRevisionIDs(pattern, revisionID));
        }
        ws.setItemsRevisionIDs(wsItemsToRevisionIDs.toArray(new WSUniverseItemsRevisionIDs[wsItemsToRevisionIDs.size()]));
        return ws;
    }

    protected UniversePOJO WS2POJO(WSUniverse wsUniverse) throws Exception {
        UniversePOJO pojo = new UniversePOJO();
        pojo.setName(wsUniverse.getName());
        pojo.setDescription(wsUniverse.getDescription());
        // Xtentis Objects
        HashMap<String, String> xtentisObjectsRevisionIDs = new HashMap<String, String>();
        if (wsUniverse.getXtentisObjectsRevisionIDs() != null) {
            for (int i = 0; i < wsUniverse.getXtentisObjectsRevisionIDs().length; i++) {
                xtentisObjectsRevisionIDs.put(wsUniverse.getXtentisObjectsRevisionIDs()[i].getXtentisObjectName(),
                        wsUniverse.getXtentisObjectsRevisionIDs()[i].getRevisionID());
            }
        }
        pojo.setXtentisObjectsRevisionIDs(xtentisObjectsRevisionIDs);
        // Default Items
        pojo.setDefaultItemRevisionID(wsUniverse.getDefaultItemsRevisionID());
        // Items
        LinkedHashMap<String, String> itemRevisionIDs = new LinkedHashMap<String, String>();
        if (wsUniverse.getItemsRevisionIDs() != null) {
            for (int i = 0; i < wsUniverse.getItemsRevisionIDs().length; i++) {
                itemRevisionIDs.put(wsUniverse.getItemsRevisionIDs()[i].getConceptPattern(),
                        wsUniverse.getItemsRevisionIDs()[i].getRevisionID());
            }
        }
        pojo.setItemsRevisionIDs(itemRevisionIDs);
        return pojo;
    }

    /***************************************************************************
     * Transformer DEPRECATED
     * **************************************************************************/
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
            return new WSTransformerPK(ctrl.removeTransformer(
                    new TransformerPOJOPK(wsTransformerDelete.getWsTransformerPK().getPk())).getUniqueId());

        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
            TransformerPOJO pojo = ctrl.getTransformer(new TransformerPOJOPK(wsGetTransformer.getWsTransformerPK().getPk()));
            return POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
            TransformerPOJO pojo = ctrl
                    .existsTransformer(new TransformerPOJOPK(wsExistsTransformer.getWsTransformerPK().getPk()));
            return new WSBoolean(pojo != null);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
            Collection collection = ctrl.getTransformerPKs(regex.getRegex());
            if (collection == null) {
                return null;
            }
            WSTransformerPK[] pks = new WSTransformerPK[collection.size()];
            int i = 0;
            for (Object o : collection) {
                pks[i++] = new WSTransformerPK(((TransformerPOJOPK) o).getUniqueId());
            }
            return new WSTransformerPKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
            TransformerPOJOPK pk = ctrl.putTransformer(WS2POJO(wsTransformer.getWsTransformer()));
            return new WSTransformerPK(pk.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    protected WSTransformer POJO2WS(TransformerPOJO transformerPOJO) throws Exception {
        WSTransformer ws = new WSTransformer();
        ws.setName(transformerPOJO.getName());
        ws.setDescription(transformerPOJO.getDescription());
        ArrayList<WSTransformerPluginSpec> wsSpecs = new ArrayList<WSTransformerPluginSpec>();
        ArrayList<TransformerPluginSpec> pluginSpecs = transformerPOJO.getPluginSpecs();
        if (pluginSpecs != null) {
            for (TransformerPluginSpec pluginSpec : pluginSpecs) {
                WSTransformerPluginSpec wsSpec = new WSTransformerPluginSpec(pluginSpec.getPluginJNDI(),
                        pluginSpec.getDescription(), pluginSpec.getInput(), pluginSpec.getOutput(), pluginSpec.getParameters());
                wsSpecs.add(wsSpec);
            }
        }
        ws.setPluginSpecs(wsSpecs.toArray(new WSTransformerPluginSpec[wsSpecs.size()]));
        return ws;
    }

    protected TransformerPOJO WS2POJO(WSTransformer wsTransformer) throws Exception {
        TransformerPOJO pojo = new TransformerPOJO();
        pojo.setName(wsTransformer.getName());
        pojo.setDescription(wsTransformer.getDescription());
        ArrayList<TransformerPluginSpec> specs = new ArrayList<TransformerPluginSpec>();
        WSTransformerPluginSpec[] wsSpecs = wsTransformer.getPluginSpecs();
        if (wsSpecs != null) {
            for (WSTransformerPluginSpec wsSpec : wsSpecs) {
                TransformerPluginSpec spec = new TransformerPluginSpec(wsSpec.getPluginJNDI(), wsSpec.getDescription(),
                        wsSpec.getInput(), wsSpec.getOutput(), wsSpec.getParameters());
                specs.add(spec);
            }
        }
        pojo.setPluginSpecs(specs);

        return pojo;
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProjectBytes) throws RemoteException {
        try {
            TransformerPluginContext context = Util.getTransformerCtrlLocal().process(
                    new com.amalto.core.util.TypedContent(null, wsProjectBytes.getWsBytes().getBytes(),
                            wsProjectBytes.getContentType()), new TransformerPOJOPK(wsProjectBytes.getWsTransformerPK().getPk()),
                    WS2POJO(wsProjectBytes.getWsOutputDecisionTable()));
            HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>) context
                    .get(TransformerCtrlBean.CTX_PIPELINE);
            // Add the Item PKs to the pipeline as comma separated lines
            String pksAsLine = "";//$NON-NLS-1$
            Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>) context.get(TransformerCtrlBean.CTX_PKS);
            for (ItemPOJOPK pk : pks) {
                if (!"".equals(pksAsLine)) { //$NON-NLS-1$
                    pksAsLine += "\n";//$NON-NLS-1$
                }
                pksAsLine += pk.getConceptName() + "," + Util.joinStrings(pk.getIds(), ",");//$NON-NLS-1$ //$NON-NLS-2$
            }
            pipeline.put(TransformerCtrlBean.CTX_PKS, new com.amalto.core.util.TypedContent(null, pksAsLine.getBytes("UTF-8"), //$NON-NLS-1$
                    "text/plain; charset=\"utf-8\""));//$NON-NLS-1$
            // return the pipeline
            return POJO2WSOLD(pipeline);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFile) throws RemoteException {
        try {
            // read the entire file into bytes
            TransformerPluginContext context = Util.getTransformerCtrlLocal().process(
                    new com.amalto.core.util.TypedContent(new FileInputStream(new File(wsProcessFile.getFileName())), null,
                            wsProcessFile.getContentType()), new TransformerPOJOPK(wsProcessFile.getWsTransformerPK().getPk()),
                    WS2POJO(wsProcessFile.getWsOutputDecisionTable()));
            HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>) context
                    .get(TransformerCtrlBean.CTX_PIPELINE);
            // Add the Item PKs to the pipeline as comma separated lines
            String pksAsLine = "";//$NON-NLS-1$
            Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>) context.get(TransformerCtrlBean.CTX_PKS);
            for (ItemPOJOPK pk : pks) {
                if (!"".equals(pksAsLine)) { //$NON-NLS-1$
                    pksAsLine += "\n";//$NON-NLS-1$
                }
                pksAsLine += pk.getConceptName() + "," + Util.joinStrings(pk.getIds(), ",");//$NON-NLS-1$ //$NON-NLS-2$
            }
            pipeline.put(TransformerCtrlBean.CTX_PKS, new com.amalto.core.util.TypedContent(null, pksAsLine.getBytes("UTF-8"),//$NON-NLS-1$
                    "text/plain; charset=\"utf-8\""));//$NON-NLS-1$
            // return the pipeline
            return POJO2WSOLD(pipeline);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(
            WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
        try {
            return new WSBackgroundJobPK(Util
                    .getTransformerCtrlLocal()
                    .processBytesAsBackgroundJob(wsProcessBytesUsingTransformerAsBackgroundJob.getWsBytes().getBytes(),
                            wsProcessBytesUsingTransformerAsBackgroundJob.getContentType(),
                            new TransformerPOJOPK(wsProcessBytesUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
                            WS2POJO(wsProcessBytesUsingTransformerAsBackgroundJob.getWsOutputDecisionTable())).getUniqueId());
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(
            WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
        try {
            return new WSBackgroundJobPK(Util
                    .getTransformerCtrlLocal()
                    .processFileAsBackgroundJob(wsProcessFileUsingTransformerAsBackgroundJob.getFileName(),
                            wsProcessFileUsingTransformerAsBackgroundJob.getContentType(),
                            new TransformerPOJOPK(wsProcessFileUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
                            WS2POJO(wsProcessFileUsingTransformerAsBackgroundJob.getWsOutputDecisionTable())).getUniqueId());
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    protected HashMap<String, String> WS2POJO(WSOutputDecisionTable table) {
        HashMap<String, String> decisions = new HashMap<String, String>();
        if ((table == null) || (table.getDecisions() == null) || (table.getDecisions().length == 0)) {
            return decisions;
        }
        WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions[] wsDecisions = table.getDecisions();
        for (WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions wsDecision : wsDecisions) {
            String name = wsDecision.getOutputVariableName();
            if (name == null) {
                name = TransformerCtrlBean.DEFAULT_VARIABLE;
            }
            decisions.put(name, wsDecision.getDecision());
        }
        return decisions;
    }

    protected WSPipeline POJO2WSOLD(HashMap<String, com.amalto.core.util.TypedContent> pipeline) throws Exception {
        ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
        Set keys = pipeline.keySet();
        for (Object key : keys) {
            String output = (String) key;
            com.amalto.core.util.TypedContent content = pipeline.get(output);
            byte[] bytes = content.getBytes();
            if (bytes == null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int c;
                while ((c = content.getStream().read()) != -1) {
                    bos.write(c);
                }
                bytes = bos.toByteArray();
            }
            WSExtractedContent wsContent = new WSExtractedContent(new WSByteArray(bytes), content.getContentType());
            WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(
                    TransformerCtrlBean.DEFAULT_VARIABLE.equals(output) ? "" : output, wsContent);
            entries.add(wsEntry);
        }
        return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
    }

    protected WSDroppedItemPK POJO2WS(DroppedItemPOJOPK droppedItemPOJOPK) throws Exception {
        ItemPOJOPK refItemPOJOPK = droppedItemPOJOPK.getRefItemPOJOPK();
        return new WSDroppedItemPK(POJO2WS(refItemPOJOPK), droppedItemPOJOPK.getPartPath(), droppedItemPOJOPK.getRevisionId());
    }

    protected DroppedItemPOJOPK WS2POJO(WSDroppedItemPK wsDroppedItemPK) throws Exception {
        ItemPOJOPK refItemPOJOPK = WS2POJO(wsDroppedItemPK.getWsItemPK());
        return new DroppedItemPOJOPK(wsDroppedItemPK.getRevisionId(), refItemPOJOPK, wsDroppedItemPK.getPartPath());
    }

    protected WSDroppedItem POJO2WS(DroppedItemPOJO droppedItemPOJO) throws Exception {
        return new WSDroppedItem(droppedItemPOJO.getRevisionID(), new WSDataClusterPK(droppedItemPOJO.getDataClusterPOJOPK()
                .getUniqueId()), droppedItemPOJO.getUniqueId(), droppedItemPOJO.getConceptName(), droppedItemPOJO.getIds(),
                droppedItemPOJO.getPartPath(), droppedItemPOJO.getInsertionUserName(), droppedItemPOJO.getInsertionTime(),
                droppedItemPOJO.getProjection());
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex) throws RemoteException {
        try {
            List droppedItemPOJOPKs = Util.getDroppedItemCtrlLocal().findAllDroppedItemsPKs(regex.getRegex());
            WSDroppedItemPK[] wsDroppedItemPKs = new WSDroppedItemPK[droppedItemPOJOPKs.size()];
            for (int i = 0; i < droppedItemPOJOPKs.size(); i++) {
                DroppedItemPOJOPK droppedItemPOJOPK = (DroppedItemPOJOPK) droppedItemPOJOPKs.get(i);
                wsDroppedItemPKs[i] = POJO2WS(droppedItemPOJOPK);
            }
            return new WSDroppedItemPKArray(wsDroppedItemPKs);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem) throws RemoteException {
        try {
            DroppedItemPOJO droppedItemPOJO = Util.getDroppedItemCtrlLocal().loadDroppedItem(
                    WS2POJO(wsLoadDroppedItem.getWsDroppedItemPK()));
            return POJO2WS(droppedItemPOJO);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem) throws RemoteException {
        try {
            ItemPOJOPK itemPOJOPK = Util.getDroppedItemCtrlLocal().recoverDroppedItem(
                    WS2POJO(wsRecoverDroppedItem.getWsDroppedItemPK()));
            return POJO2WS(itemPOJOPK);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
        try {
            DroppedItemPOJOPK droppedItemPOJOPK = Util.getDroppedItemCtrlLocal().removeDroppedItem(
                    WS2POJO(wsRemoveDroppedItem.getWsDroppedItemPK()));
            return POJO2WS(droppedItemPOJOPK);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * RoutingRule
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet) throws RemoteException {
        try {
            if (Util.getRoutingRuleCtrlLocal().existsRoutingRule(
                    new RoutingRulePOJOPK(wsRoutingRuleGet.getWsRoutingRulePK().getPk())) == null) {
                return null;
            }
            return VO2WS(Util.getRoutingRuleCtrlLocal().getRoutingRule(
                    new RoutingRulePOJOPK(wsRoutingRuleGet.getWsRoutingRulePK().getPk())));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule) throws RemoteException {
        try {
            return new WSBoolean(Util.getRoutingRuleCtrlLocal().existsRoutingRule(
                    new RoutingRulePOJOPK(wsExistsRoutingRule.getWsRoutingRulePK().getPk())) != null);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule) throws RemoteException {
        try {
            return new WSRoutingRulePK(Util.getRoutingRuleCtrlLocal()
                    .removeRoutingRule(new RoutingRulePOJOPK(wsDeleteRoutingRule.getWsRoutingRulePK().getPk())).getUniqueId());
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule) throws RemoteException {
        try {
            return new WSRoutingRulePK(Util.getRoutingRuleCtrlLocal().putRoutingRule(WS2VO(wsRoutingRule.getWsRoutingRule()))
                    .getUniqueId());
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex) throws RemoteException {
        try {
            RoutingRuleCtrlLocal ctrl = Util.getRoutingRuleCtrlLocal();
            Collection collection = ctrl.getRoutingRulePKs(regex.getRegex());
            if (collection == null) {
                return null;
            }
            WSRoutingRulePK[] pks = new WSRoutingRulePK[collection.size()];
            int i = 0;
            for (Object o : collection) {
                pks[i++] = new WSRoutingRulePK(((RoutingRulePOJOPK) o).getUniqueId());
            }
            return new WSRoutingRulePKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    protected WSRoutingRule VO2WS(RoutingRulePOJO pojo) throws Exception {
        WSRoutingRule s = new WSRoutingRule();
        s.setName(pojo.getName());
        s.setDescription(pojo.getDescription());
        s.setConcept(pojo.getConcept());
        s.setParameters(pojo.getParameters());
        s.setServiceJNDI(pojo.getServiceJNDI());
        s.setSynchronous(pojo.isSynchronous());

        WSRoutingRuleExpression[] routingExpressions = null;
        Collection collection = pojo.getRoutingExpressions();
        if (collection != null) {
            routingExpressions = new WSRoutingRuleExpression[collection.size()];
            int i = 0;
            for (Object o : collection) {
                RoutingRuleExpressionPOJO rre = (RoutingRuleExpressionPOJO) o;
                routingExpressions[i++] = VO2WS(rre);
            }
        }
        s.setWsRoutingRuleExpressions(routingExpressions);
        s.setCondition(pojo.getCondition());
        s.setDeactive(pojo.isDeActive());
        return s;
    }

    protected RoutingRulePOJO WS2VO(WSRoutingRule ws) throws Exception {
        RoutingRulePOJO pojo = new RoutingRulePOJO();
        pojo.setName(ws.getName());
        pojo.setDescription(ws.getDescription());
        pojo.setConcept(ws.getConcept());
        pojo.setParameters(ws.getParameters());
        pojo.setServiceJNDI(ws.getServiceJNDI());
        pojo.setSynchronous(ws.isSynchronous());
        ArrayList l = new ArrayList();
        WSRoutingRuleExpression[] rre = ws.getWsRoutingRuleExpressions();
        if (rre != null) {
            for (WSRoutingRuleExpression aRre : rre) {
                l.add(WS2VO(aRre));
            }
        }
        pojo.setRoutingExpressions(l);
        pojo.setCondition(ws.getCondition());
        pojo.setDeActive(ws.getDeactive());
        return pojo;
    }

    protected WSRoutingRuleExpression VO2WS(RoutingRuleExpressionPOJO vo) throws Exception {
        WSRoutingRuleExpression ws = new WSRoutingRuleExpression();
        ws.setName(vo.getName());
        ws.setXpath(vo.getXpath());
        ws.setValue(vo.getValue());
        switch (vo.getOperator()) {
        case RoutingRuleExpressionPOJO.CONTAINS:
            ws.setWsOperator(WSRoutingRuleOperator.CONTAINS);
            break;
        case RoutingRuleExpressionPOJO.EQUALS:
            ws.setWsOperator(WSRoutingRuleOperator.EQUALS);
            break;
        case RoutingRuleExpressionPOJO.GREATER_THAN:
            ws.setWsOperator(WSRoutingRuleOperator.GREATER_THAN);
            break;
        case RoutingRuleExpressionPOJO.GREATER_THAN_OR_EQUAL:
            ws.setWsOperator(WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL);
            break;
        case RoutingRuleExpressionPOJO.IS_NOT_NULL:
            ws.setWsOperator(WSRoutingRuleOperator.IS_NOT_NULL);
            break;
        case RoutingRuleExpressionPOJO.IS_NULL:
            ws.setWsOperator(WSRoutingRuleOperator.IS_NULL);
            break;
        case RoutingRuleExpressionPOJO.LOWER_THAN:
            ws.setWsOperator(WSRoutingRuleOperator.LOWER_THAN);
            break;
        case RoutingRuleExpressionPOJO.LOWER_THAN_OR_EQUAL:
            ws.setWsOperator(WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL);
            break;
        case RoutingRuleExpressionPOJO.MATCHES:
            ws.setWsOperator(WSRoutingRuleOperator.MATCHES);
            break;
        case RoutingRuleExpressionPOJO.NOT_EQUALS:
            ws.setWsOperator(WSRoutingRuleOperator.NOT_EQUALS);
            break;
        case RoutingRuleExpressionPOJO.STARTSWITH:
            ws.setWsOperator(WSRoutingRuleOperator.STARTSWITH);
            break;
        }
        return ws;
    }

    protected RoutingRuleExpressionPOJO WS2VO(WSRoutingRuleExpression ws) throws Exception {
        if (ws == null) {
            return null;
        }
        int operator = 1;
        if (ws.getWsOperator().equals(WSRoutingRuleOperator.CONTAINS)) {
            operator = RoutingRuleExpressionPOJO.CONTAINS;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.EQUALS)) {
            operator = RoutingRuleExpressionPOJO.EQUALS;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN)) {
            operator = RoutingRuleExpressionPOJO.GREATER_THAN;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL)) {
            operator = RoutingRuleExpressionPOJO.GREATER_THAN_OR_EQUAL;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.IS_NOT_NULL)) {
            operator = RoutingRuleExpressionPOJO.IS_NOT_NULL;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.IS_NULL)) {
            operator = RoutingRuleExpressionPOJO.IS_NULL;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN)) {
            operator = RoutingRuleExpressionPOJO.LOWER_THAN;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL)) {
            operator = RoutingRuleExpressionPOJO.LOWER_THAN_OR_EQUAL;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.MATCHES)) {
            operator = RoutingRuleExpressionPOJO.MATCHES;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.NOT_EQUALS)) {
            operator = RoutingRuleExpressionPOJO.NOT_EQUALS;
        } else if (ws.getWsOperator().equals(WSRoutingRuleOperator.STARTSWITH)) {
            operator = RoutingRuleExpressionPOJO.STARTSWITH;
        }
        return new RoutingRuleExpressionPOJO(ws.getName(), ws.getXpath(), operator, ws.getValue());
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
            return POJO2WS(pojo);
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
            Collection collection = ctrl.getTransformerPKs(regex.getRegex());
            if (collection == null) {
                return null;
            }
            WSTransformerV2PK[] pks = new WSTransformerV2PK[collection.size()];
            int i = 0;
            for (Object o : collection) {
                pks[i++] = new WSTransformerV2PK(((TransformerV2POJOPK) o).getUniqueId());
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
            TransformerV2POJOPK pk = ctrl.putTransformer(WS2POJO(wsTransformerV2.getWsTransformerV2()));
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
            TransformerContext context = WS2POJO(wsExecuteTransformerV2.getWsTransformerContext());
            context.put(RUNNING, Boolean.TRUE);
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            ctrl.execute(context, WS2POJO(wsExecuteTransformerV2.getWsTypedContent()), new TransformerCallBack() {

                @Override
                public void contentIsReady(TransformerContext context) throws XtentisException {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("XtentisWSBean.executeTransformerV2.contentIsReady() ");
                    }
                }

                @Override
                public void done(TransformerContext context) throws XtentisException {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("XtentisWSBean.executeTransformerV2.done() ");
                    }
                    context.put(RUNNING, Boolean.FALSE);
                }
            });
            while ((Boolean) context.get(RUNNING)) {
                Thread.sleep(100);
            }
            return POJO2WS(context);
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
            BackgroundJobPOJOPK bgPK = ctrl.executeAsJob(WS2POJO(wsExecuteTransformerV2AsJob.getWsTransformerContext()),
                    new TransformerCallBack() {

                        @Override
                        public void contentIsReady(TransformerContext context) throws XtentisException {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("XtentisWSBean.executeTransformerV2AsJob.contentIsReady() ");
                            }
                        }

                        @Override
                        public void done(TransformerContext context) throws XtentisException {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("XtentisWSBean.executeTransformerV2AsJob.done() ");
                            }
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
            return POJO2WS(ctrl.extractThroughTransformer(new TransformerV2POJOPK(wsExtractThroughTransformerV2
                    .getWsTransformerV2PK().getPk()), WS2POJO(wsExtractThroughTransformerV2.getWsItemPK())));
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    protected WSTransformerContext POJO2WS(TransformerContext context) throws Exception {
        WSTransformerContext wsContext = new WSTransformerContext();

        WSTransformerContextPipeline wsPipeline = new WSTransformerContextPipeline();
        ArrayList<WSTransformerContextPipelinePipelineItem> wsList = new ArrayList<WSTransformerContextPipelinePipelineItem>();
        LinkedHashMap<String, TypedContent> pipeline = context.getPipelineClone();
        Set<String> variables = pipeline.keySet();
        for (String variable : variables) {
            WSTransformerContextPipelinePipelineItem wsItem = new WSTransformerContextPipelinePipelineItem();
            wsItem.setVariable(variable);
            wsItem.setWsTypedContent(POJO2WS(pipeline.get(variable)));
            wsList.add(wsItem);
        }
        wsPipeline.setPipelineItem(wsList.toArray(new WSTransformerContextPipelinePipelineItem[wsList.size()]));
        wsContext.setPipeline(wsPipeline);

        WSTransformerContextProjectedItemPKs wsProjectedItemPKs = new WSTransformerContextProjectedItemPKs();
        ArrayList<WSItemPK> wsPKList = new ArrayList<WSItemPK>();
        SortedSet<ItemPOJOPK> projectedPKs = context.getProjectedPKs();
        for (ItemPOJOPK pk : projectedPKs) {
            wsPKList.add(POJO2WS(pk));
        }
        wsProjectedItemPKs.setWsItemPOJOPK(wsPKList.toArray(new WSItemPK[wsPKList.size()]));
        wsContext.setProjectedItemPKs(wsProjectedItemPKs);
        return wsContext;
    }

    protected TransformerContext WS2POJO(WSTransformerContext wsContext) throws Exception {
        TransformerContext context = new TransformerContext(new TransformerV2POJOPK(wsContext.getWsTransformerPK().getPk()));
        if (wsContext.getPipeline() != null) {
            if (wsContext.getPipeline().getPipelineItem() != null) {
                for (int i = 0; i < wsContext.getPipeline().getPipelineItem().length; i++) {
                    WSTransformerContextPipelinePipelineItem wsItem = wsContext.getPipeline().getPipelineItem()[i];
                    context.putInPipeline(wsItem.getVariable(), WS2POJO(wsItem.getWsTypedContent()));
                }
            }
        }
        if (wsContext.getProjectedItemPKs() != null) {
            if (wsContext.getProjectedItemPKs().getWsItemPOJOPK() != null) {
                for (int i = 0; i < wsContext.getProjectedItemPKs().getWsItemPOJOPK().length; i++) {
                    WSItemPK wsPK = wsContext.getProjectedItemPKs().getWsItemPOJOPK()[i];
                    context.getProjectedPKs().add(WS2POJO(wsPK));
                }
            }
        }
        return context;
    }

    protected WSTypedContent POJO2WS(TypedContent content) throws Exception {
        if (content == null) {
            return null;
        }
        WSTypedContent wsTypedContent = new WSTypedContent();
        if (content.getUrl() == null) {
            wsTypedContent.setWsBytes(new WSByteArray(content.getContentBytes()));
        }
        wsTypedContent.setUrl(content.getUrl());
        wsTypedContent.setContentType(content.getContentType());
        return wsTypedContent;
    }

    protected TypedContent WS2POJO(WSTypedContent wsContent) throws Exception {
        if (wsContent == null) {
            return null;
        }
        TypedContent content;
        if (wsContent.getUrl() == null) {
            content = new TypedContent(wsContent.getWsBytes().getBytes(), wsContent.getContentType());
        } else {
            content = new TypedContent(wsContent.getUrl(), wsContent.getContentType());
        }
        return content;
    }

    protected WSTransformerVariablesMapping POJO2WS(TransformerVariablesMapping mappings) throws Exception {
        WSTransformerVariablesMapping wsMapping = new WSTransformerVariablesMapping();
        wsMapping.setPluginVariable(mappings.getPluginVariable());
        wsMapping.setPipelineVariable(mappings.getPipelineVariable());
        wsMapping.setHardCoding(POJO2WS(mappings.getHardCoding()));
        return wsMapping;
    }

    protected TransformerVariablesMapping WS2POJO(WSTransformerVariablesMapping wsMapping) throws Exception {
        TransformerVariablesMapping mapping = new TransformerVariablesMapping();
        mapping.setPluginVariable(wsMapping.getPluginVariable());
        mapping.setPipelineVariable(wsMapping.getPipelineVariable());
        mapping.setHardCoding(WS2POJO(wsMapping.getHardCoding()));
        return mapping;
    }

    protected WSTransformerProcessStep POJO2WS(TransformerProcessStep processStep) throws Exception {
        WSTransformerProcessStep wsProcessStep = new WSTransformerProcessStep();
        wsProcessStep.setDescription(processStep.getDescription());
        wsProcessStep.setDisabled(processStep.isDisabled());
        wsProcessStep.setParameters(processStep.getParameters());
        wsProcessStep.setPluginJNDI(processStep.getPluginJNDI());

        ArrayList<WSTransformerVariablesMapping> wsMappings = new ArrayList<WSTransformerVariablesMapping>();
        ArrayList<TransformerVariablesMapping> list = processStep.getInputMappings();
        for (TransformerVariablesMapping mapping : list) {
            wsMappings.add(POJO2WS(mapping));
        }
        wsProcessStep.setInputMappings(wsMappings.toArray(new WSTransformerVariablesMapping[wsMappings.size()]));
        wsMappings = new ArrayList<WSTransformerVariablesMapping>();
        list = processStep.getOutputMappings();
        for (TransformerVariablesMapping mapping : list) {
            wsMappings.add(POJO2WS(mapping));
        }
        wsProcessStep.setOutputMappings(wsMappings.toArray(new WSTransformerVariablesMapping[wsMappings.size()]));
        return wsProcessStep;
    }

    protected TransformerProcessStep WS2POJO(WSTransformerProcessStep wsProcessStep) throws Exception {
        TransformerProcessStep processStep = new TransformerProcessStep();
        processStep.setDescription(wsProcessStep.getDescription());
        processStep.setDisabled(wsProcessStep.getDisabled());
        processStep.setParameters(wsProcessStep.getParameters());
        processStep.setPluginJNDI(wsProcessStep.getPluginJNDI());
        ArrayList<TransformerVariablesMapping> inputMappings = new ArrayList<TransformerVariablesMapping>();
        if (wsProcessStep.getInputMappings() != null) {
            for (int i = 0; i < wsProcessStep.getInputMappings().length; i++) {
                inputMappings.add(WS2POJO(wsProcessStep.getInputMappings()[i]));
            }
        }
        processStep.setInputMappings(inputMappings);
        ArrayList<TransformerVariablesMapping> outputMappings = new ArrayList<TransformerVariablesMapping>();
        if (wsProcessStep.getOutputMappings() != null) {
            for (int i = 0; i < wsProcessStep.getOutputMappings().length; i++) {
                outputMappings.add(WS2POJO(wsProcessStep.getOutputMappings()[i]));
            }
        }
        processStep.setOutputMappings(outputMappings);
        return processStep;
    }

    protected WSTransformerV2 POJO2WS(TransformerV2POJO transformerPOJO) throws Exception {
        WSTransformerV2 ws = new WSTransformerV2();
        ws.setName(transformerPOJO.getName());
        ws.setDescription(transformerPOJO.getDescription());
        ArrayList<WSTransformerProcessStep> wsSteps = new ArrayList<WSTransformerProcessStep>();
        ArrayList<TransformerProcessStep> processSteps = transformerPOJO.getProcessSteps();
        if (processSteps != null) {
            for (TransformerProcessStep processStep : processSteps) {
                wsSteps.add(POJO2WS(processStep));
            }
        }
        ws.setProcessSteps(wsSteps.toArray(new WSTransformerProcessStep[wsSteps.size()]));
        return ws;
    }

    protected TransformerV2POJO WS2POJO(WSTransformerV2 wsTransformerV2) throws Exception {
        TransformerV2POJO pojo = new TransformerV2POJO();
        pojo.setName(wsTransformerV2.getName());
        pojo.setDescription(wsTransformerV2.getDescription());
        ArrayList<TransformerProcessStep> steps = new ArrayList<TransformerProcessStep>();
        WSTransformerProcessStep[] wsSteps = wsTransformerV2.getProcessSteps();
        if (wsSteps != null) {
            for (WSTransformerProcessStep wsStep : wsSteps) {
                TransformerProcessStep step = WS2POJO(wsStep);
                steps.add(step);
            }
        }
        pojo.setProcessSteps(steps);
        return pojo;
    }

    public static WSPipeline POJO2WS(HashMap<String, TypedContent> pipeline) throws Exception {
        if (pipeline == null) {
            return null;
        }

        ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
        Set keys = pipeline.keySet();
        for (Object key : keys) {
            String output = (String) key;
            TypedContent content = pipeline.get(output);
            byte[] bytes = content.getContentBytes();
            WSExtractedContent wsContent = new WSExtractedContent(new WSByteArray(bytes), content.getContentType());
            WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(output, wsContent);
            entries.add(wsEntry);
        }
        return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
    }

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
        } catch (XtentisException e) {
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
            String configuration = (String) Util.getMethod(service, "getConfiguration").invoke(service, //$NON-NLS-1$
                    wsGetConfiguration.getOptionalParameter());
            return new WSString(configuration);
        } catch (XtentisException e) {
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
            Util.getMethod(service, "putConfiguration").invoke(service, wsPutConfiguration.getConfiguration());//$NON-NLS-1$
            return new WSString(wsPutConfiguration.getConfiguration());
        } catch (XtentisException e) {
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
            String description = (String) Util.getMethod(service, "getDescription").invoke(//$NON-NLS-1$
                    service, wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails//$NON-NLS-1$
                            .getLanguage());
            String documentation = (String) Util.getMethod(service, "getDocumentation").invoke(//$NON-NLS-1$
                    service,
                    wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails.getLanguage());
            String parametersSchema = (String) Util.getMethod(service, "getParametersSchema").invoke(service);//$NON-NLS-1$

            ArrayList<TransformerPluginVariableDescriptor> inputVariableDescriptors = (ArrayList<TransformerPluginVariableDescriptor>) Util
                    .getMethod(service, "getInputVariableDescriptors").invoke(//$NON-NLS-1$
                            service, wsGetTransformerPluginDetails.getLanguage() == null ? ""//$NON-NLS-1$
                                    : wsGetTransformerPluginDetails.getLanguage());
            ArrayList<WSTransformerPluginV2VariableDescriptor> wsInputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
            if (inputVariableDescriptors != null) {
                for (TransformerPluginVariableDescriptor descriptor : inputVariableDescriptors) {
                    wsInputVariableDescriptors.add(POJO2WS(descriptor));
                }
            }
            ArrayList<TransformerPluginVariableDescriptor> outputVariableDescriptors = (ArrayList<TransformerPluginVariableDescriptor>) Util
                    .getMethod(service, "getOutputVariableDescriptors").invoke(//$NON-NLS-1$
                            service, wsGetTransformerPluginDetails.getLanguage() == null ? ""//$NON-NLS-1$
                                    : wsGetTransformerPluginDetails.getLanguage());
            ArrayList<WSTransformerPluginV2VariableDescriptor> wsOutputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
            if (outputVariableDescriptors != null) {
                for (TransformerPluginVariableDescriptor descriptor : outputVariableDescriptors) {
                    wsOutputVariableDescriptors.add(POJO2WS(descriptor));
                }
            }
            return new WSTransformerPluginV2Details(
                    wsInputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsInputVariableDescriptors
                            .size()]),
                    wsOutputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsOutputVariableDescriptors
                            .size()]), description, documentation, parametersSchema);
        } catch (XtentisException e) {
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
            NamingEnumeration<NameClassPair> list = ctx.list("amalto/local/transformer/plugin");//$NON-NLS-1$
            while (list.hasMore()) {
                NameClassPair nc = list.next();
                WSTransformerPluginV2SListItem item = new WSTransformerPluginV2SListItem();
                item.setJndiName(nc.getName());
                Object service = Util.retrieveComponent(null, "amalto/local/transformer/plugin/" + nc.getName());//$NON-NLS-1$
                String description = (String) Util.getMethod(service, "getDescription").invoke(//$NON-NLS-1$
                        service, wsGetTransformerPluginsList.getLanguage() == null ? "" : wsGetTransformerPluginsList//$NON-NLS-1$
                                .getLanguage());
                item.setDescription(description);
                wsList.add(item);
            }
            return new WSTransformerPluginV2SList(wsList.toArray(new WSTransformerPluginV2SListItem[wsList.size()]));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    protected WSTransformerPluginV2VariableDescriptor POJO2WS(TransformerPluginVariableDescriptor descriptor) throws Exception {
        WSTransformerPluginV2VariableDescriptor wsDescriptor = new WSTransformerPluginV2VariableDescriptor();
        wsDescriptor.setVariableName(descriptor.getVariableName());
        if (descriptor.getDescriptions().size() > 0) {
            wsDescriptor.setDescription(descriptor.getDescriptions().values().iterator().next());
        }
        wsDescriptor.setMandatory(descriptor.isMandatory());
        ArrayList<String> contentTypesRegex = new ArrayList<String>();
        if (descriptor.getContentTypesRegex() != null) {
            for (Pattern p : descriptor.getContentTypesRegex()) {
                contentTypesRegex.add(p.toString());
            }
        }
        wsDescriptor.setContentTypesRegex(contentTypesRegex.toArray(new String[contentTypesRegex.size()]));
        ArrayList<String> possibleValuesRegex = new ArrayList<String>();
        if (descriptor.getPossibleValuesRegex() != null) {
            for (Pattern p : descriptor.getPossibleValuesRegex()) {
                possibleValuesRegex.add(p.toString());
            }
        }
        wsDescriptor.setPossibleValuesRegex(possibleValuesRegex.toArray(new String[possibleValuesRegex.size()]));
        return wsDescriptor;
    }

    /***************************************************************************
     * Routing Order V2
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            return POJO2WS(ctrl.getRoutingOrder(WS2POJO(wsGetRoutingOrder.getWsRoutingOrderPK())));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            return POJO2WS(ctrl.existsRoutingOrder(WS2POJO(wsExistsRoutingOrder.getWsRoutingOrderPK())));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            return POJO2WS(ctrl.removeRoutingOrder(WS2POJO(wsDeleteRoutingOrder.getWsRoutingOrderPK())));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(
            WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(WS2POJO(wsExecuteRoutingOrderAsynchronously
                    .getRoutingOrderV2PK()));
            ctrl.executeAsynchronously(ro);
            return POJO2WS(ro.getAbstractRoutingOrderPOJOPK());
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously)
            throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            AbstractRoutingOrderV2POJO ro = ctrl
                    .getRoutingOrder(WS2POJO(wsExecuteRoutingOrderSynchronously.getRoutingOrderV2PK()));
            return new WSString(ctrl.executeSynchronously(ro));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    protected Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrdersByCriteria(WSRoutingOrderV2SearchCriteria criteria)
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
            return ctrl.getRoutingOrderPKsByCriteria(clazz, criteria.getAnyFieldContains(), criteria.getNameContains(),
                    criteria.getTimeCreatedMin(), criteria.getTimeCreatedMax(), criteria.getTimeScheduledMin(),
                    criteria.getTimeScheduledMax(), criteria.getTimeLastRunStartedMin(), criteria.getTimeLastRunStartedMax(),
                    criteria.getTimeLastRunCompletedMin(), criteria.getTimeLastRunCompletedMax(),
                    criteria.getItemPKConceptContains(), criteria.getItemPKIDFieldsContain(), criteria.getServiceJNDIContains(),
                    criteria.getServiceParametersContain(), criteria.getMessageContain());
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    protected Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrdersByCriteriaWithPaging(
            WSRoutingOrderV2SearchCriteriaWithPaging criteria) throws Exception {
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

            return ctrl.getRoutingOrderPKsByCriteriaWithPaging(clazz, criteria.getAnyFieldContains(), criteria.getNameContains(),
                    criteria.getTimeCreatedMin(), criteria.getTimeCreatedMax(), criteria.getTimeScheduledMin(),
                    criteria.getTimeScheduledMax(), criteria.getTimeLastRunStartedMin(), criteria.getTimeLastRunStartedMax(),
                    criteria.getTimeLastRunCompletedMin(), criteria.getTimeLastRunCompletedMax(),
                    criteria.getItemPKConceptContains(), criteria.getItemPKIDFieldsContain(), criteria.getServiceJNDIContains(),
                    criteria.getServiceParametersContain(), criteria.getMessageContain(), criteria.getSkip(),
                    criteria.getMaxItems(), criteria.getTotalCountOnFirstResult());
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
            WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
        try {
            WSRoutingOrderV2PKArray wsPKArray = new WSRoutingOrderV2PKArray();
            ArrayList<WSRoutingOrderV2PK> list = new ArrayList<WSRoutingOrderV2PK>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2PKsByCriteria
                    .getWsSearchCriteria());
            for (AbstractRoutingOrderV2POJOPK pk : pks) {
                list.add(POJO2WS(pk));
            }
            wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2PK[list.size()]));
            return wsPKArray;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
            throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            WSRoutingOrderV2Array wsPKArray = new WSRoutingOrderV2Array();
            ArrayList<WSRoutingOrderV2> list = new ArrayList<WSRoutingOrderV2>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2SByCriteria
                    .getWsSearchCriteria());
            for (AbstractRoutingOrderV2POJOPK pk : pks) {
                list.add(POJO2WS(ctrl.getRoutingOrder(pk)));
            }
            wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2[list.size()]));
            return wsPKArray;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingOrderV2Array getRoutingOrderV2ByCriteriaWithPaging(
            WSGetRoutingOrderV2ByCriteriaWithPaging wsGetRoutingOrderV2ByCriteriaWithPaging) throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            WSRoutingOrderV2Array wsPKArray = new WSRoutingOrderV2Array();
            ArrayList<WSRoutingOrderV2> list = new ArrayList<WSRoutingOrderV2>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteriaWithPaging(wsGetRoutingOrderV2ByCriteriaWithPaging
                    .getWsSearchCriteriaWithPaging());
            boolean withTotalCount = wsGetRoutingOrderV2ByCriteriaWithPaging.getWsSearchCriteriaWithPaging()
                    .getTotalCountOnFirstResult();
            boolean firstRecord = true;
            for (AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK : pks) {
                if (withTotalCount && firstRecord) {
                    firstRecord = false;
                    WSRoutingOrderV2 wsRoutingOrderV2 = new WSRoutingOrderV2();
                    // record totalCount and wsRoutingOrderV2 need to initialize attribute value
                    wsRoutingOrderV2.setName(abstractRoutingOrderV2POJOPK.getName());
                    wsRoutingOrderV2.setBindingUniverseName("");
                    wsRoutingOrderV2.setBindingUserToken("");
                    wsRoutingOrderV2.setMessage("");
                    wsRoutingOrderV2.setServiceJNDI("");
                    wsRoutingOrderV2.setServiceParameters("");
                    wsRoutingOrderV2.setStatus(WSRoutingOrderV2Status.COMPLETED);
                    wsRoutingOrderV2.setWsItemPK(new WSItemPK(new WSDataClusterPK(""), "", new String[0]));
                    list.add(wsRoutingOrderV2);
                    continue;
                }
                list.add(POJO2WS(ctrl.getRoutingOrder(abstractRoutingOrderV2POJOPK)));
            }
            wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2[list.size()]));
            return wsPKArray;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    protected WSRoutingOrderV2PK POJO2WS(AbstractRoutingOrderV2POJOPK pojo) throws Exception {
        if (pojo == null) {
            return null;
        }
        WSRoutingOrderV2PK ws = new WSRoutingOrderV2PK();
        ws.setName(pojo.getName());
        switch (pojo.getStatus()) {
        case AbstractRoutingOrderV2POJO.ACTIVE:
            ws.setStatus(WSRoutingOrderV2Status.ACTIVE);
            break;
        case AbstractRoutingOrderV2POJO.COMPLETED:
            ws.setStatus(WSRoutingOrderV2Status.COMPLETED);
            break;
        case AbstractRoutingOrderV2POJO.FAILED:
            ws.setStatus(WSRoutingOrderV2Status.FAILED);
            break;
        }
        return ws;
    }

    protected AbstractRoutingOrderV2POJOPK WS2POJO(WSRoutingOrderV2PK s) throws Exception {
        if (s == null) {
            return null;
        }
        AbstractRoutingOrderV2POJOPK pojo = null;
        if (s.getStatus().equals(WSRoutingOrderV2Status.ACTIVE)) {
            pojo = new ActiveRoutingOrderV2POJOPK(s.getName());
        } else if (s.getStatus().equals(WSRoutingOrderV2Status.COMPLETED)) {
            pojo = new CompletedRoutingOrderV2POJOPK(s.getName());
        } else if (s.getStatus().equals(WSRoutingOrderV2Status.FAILED)) {
            pojo = new FailedRoutingOrderV2POJOPK(s.getName());
        }
        return pojo;
    }

    protected WSRoutingOrderV2 POJO2WS(AbstractRoutingOrderV2POJO pojo) throws Exception {
        if (pojo == null) {
            return null;
        }
        WSRoutingOrderV2 ws = new WSRoutingOrderV2();
        ws.setMessage(pojo.getMessage());
        ws.setName(pojo.getName());
        ws.setServiceJNDI(pojo.getServiceJNDI());
        ws.setServiceParameters(pojo.getServiceParameters());
        switch (pojo.getStatus()) {
        case AbstractRoutingOrderV2POJO.ACTIVE:
            ws.setStatus(WSRoutingOrderV2Status.ACTIVE);
            break;
        case AbstractRoutingOrderV2POJO.COMPLETED:
            ws.setStatus(WSRoutingOrderV2Status.COMPLETED);
            break;
        case AbstractRoutingOrderV2POJO.FAILED:
            ws.setStatus(WSRoutingOrderV2Status.FAILED);
            break;
        }
        ws.setTimeCreated(pojo.getTimeCreated());
        ws.setTimeLastRunCompleted(pojo.getTimeLastRunCompleted());
        ws.setTimeLastRunStarted(pojo.getTimeLastRunStarted());
        ws.setTimeScheduled(pojo.getTimeScheduled());
        ws.setWsItemPK(POJO2WS(pojo.getItemPOJOPK()));
        ws.setBindingUniverseName(pojo.getBindingUniverseName());
        ws.setBindingUserToken(pojo.getBindingUserToken());
        return ws;
    }

    /***************************************************************************
     * Routing Engine V2
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            RoutingRulePOJOPK[] rules = ctrl.route(WS2POJO(wsRouteItem.getWsItemPK()));
            ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
            for (RoutingRulePOJOPK rule : rules) {
                list.add(new WSRoutingRulePK(rule.getUniqueId()));
            }
            return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
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
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
        // get status
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
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                String err = "ERROR SYSTRACE: " + e.getMessage();
                LOG.debug(err, e);
            }
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }

    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSAutoIncrement getAutoIncrement(WSAutoIncrement request) throws RemoteException {
        try {
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            if (request == null) {
                String xml = xmlServerCtrlLocal.getDocumentAsString(null, XSystemObjects.DC_CONF.getName(), "Auto_Increment");//$NON-NLS-1$
                if (xml != null) {
                    return new WSAutoIncrement(xml);
                }
            } else {
                xmlServerCtrlLocal.start(XSystemObjects.DC_CONF.getName());
                xmlServerCtrlLocal.putDocumentFromString(request.getAutoincrement(), "Auto_Increment",//$NON-NLS-1$
                        XSystemObjects.DC_CONF.getName(), null);
                xmlServerCtrlLocal.commit(XSystemObjects.DC_CONF.getName());
                return request;
            }
        } catch (XtentisException e) {
            LOG.error("IXtentisWSDelegator.getAutoIncrement error.", e);
        }
        return null;
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */

    public WSCategoryData getMDMCategory(WSCategoryData request) throws RemoteException {
        try {
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            if (request == null) {
                // create and retrieve an empty treeObject Category from xdb in the case of request being null
                String category = xmlServerCtrlLocal.getDocumentAsString(null, "CONF", "CONF.TREEOBJECT.CATEGORY");//$NON-NLS-1$ //$NON-NLS-2$
                if (category == null) {
                    String empty = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";//$NON-NLS-1$
                    empty += "<" + ICoreConstants.DEFAULT_CATEGORY_ROOT + "/>";//$NON-NLS-1$ //$NON-NLS-2$
                    xmlServerCtrlLocal.start("CONF");
                    xmlServerCtrlLocal.putDocumentFromString(empty, "CONF.TREEOBJECT.CATEGORY", "CONF", "");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    xmlServerCtrlLocal.commit("CONF");
                    category = empty;
                }
                return new WSCategoryData(category);
            } else {
                xmlServerCtrlLocal.start("CONF"); //$NON-NLS-1$
                xmlServerCtrlLocal.putDocumentFromString(request.getCategorySchema(), "CONF.TREEOBJECT.CATEGORY",//$NON-NLS-1$
                        "CONF", null);//$NON-NLS-1$
                xmlServerCtrlLocal.commit("CONF"); //$NON-NLS-1$
                return request;
            }
        } catch (XtentisException e) {
            LOG.error("IXtentisWSDelegator.getMDMCategory error.", e);
            return null;
        }
    }

    /**
     * **********************JOB***************************************
     */

    public static final String MDM_TIS_JOB = "MDMTISJOB";//$NON-NLS-1$

    public static final String JOB = "JOB";//$NON-NLS-1$

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean putMDMJob(WSPUTMDMJob job) throws RemoteException {
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc;
            Element jobElem, newOne;
            String xmlData = null;
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            try {
                xmlData = xmlServerCtrlLocal.getDocumentAsString(null, MDM_TIS_JOB, JOB);
            } catch (Exception e) {
                LOG.error("IXtentisWSDelegator.putMDMJob error.", e);
            }
            if (xmlData == null || xmlData.equals("")) {//$NON-NLS-1$
                doc = documentBuilder.newDocument();
                jobElem = doc.createElement("jobs");//$NON-NLS-1$
                doc.appendChild(jobElem);
            } else {
                doc = Util.parse(xmlData);
                jobElem = doc.getDocumentElement();
            }

            newOne = doc.createElement("job");//$NON-NLS-1$
            newOne.setAttribute("name", job.getJobName());//$NON-NLS-1$
            newOne.setAttribute("version", job.getJobVersion());//$NON-NLS-1$
            jobElem.appendChild(newOne);

            xmlServerCtrlLocal.start(MDM_TIS_JOB);
            xmlServerCtrlLocal.putDocumentFromString(Util.nodeToString(doc.getDocumentElement()), JOB, MDM_TIS_JOB, null);
            xmlServerCtrlLocal.commit(MDM_TIS_JOB);
            return new WSBoolean(true);
        } catch (Exception e) {
            LOG.error("IXtentisWSDelegator.putMDMJob error.", e);
        }
        return new WSBoolean(false);
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean deleteMDMJob(WSDELMDMJob job) throws RemoteException {
        Document doc;
        try {
            String xmlData = null;
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            try {
                xmlData = xmlServerCtrlLocal.getDocumentAsString(null, MDM_TIS_JOB, JOB);
            } catch (Exception e) {
                LOG.error("IXtentisWSDelegator.deleteMDMJob error.", e);
            }
            if (xmlData == null) {
                return new WSBoolean(false);
            }
            doc = Util.parse(xmlData);
            NodeList list = Util.getNodeList(doc, "/jobs/job[@name='" + job.getJobName() + "']");//$NON-NLS-1$ //$NON-NLS-2$
            if (list.getLength() > 0) {
                doc.getDocumentElement().removeChild(list.item(0));
                xmlData = Util.nodeToString(doc);
                xmlServerCtrlLocal.start(MDM_TIS_JOB);
                xmlServerCtrlLocal.putDocumentFromString(xmlData, JOB, MDM_TIS_JOB, null);
                xmlServerCtrlLocal.commit(MDM_TIS_JOB);
                return new WSBoolean(true);
            }
        } catch (Exception e) {
            LOG.error("IXtentisWSDelegator.deleteMDMJob error.", e);
        }
        return new WSBoolean(false);
    }

    /**
     * get job info from jboss deploy dir
     * 
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */

    public WSMDMJobArray getMDMJob(WSMDMNULL job) {
        WSMDMJobArray jobSet = new WSMDMJobArray();
        WSMDMJob[] jobs = Util.getMDMJobs();
        jobSet.setWsMDMJob(jobs);
        return jobSet;
    }

    /**
     * is Item modified by others
     * 
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean isItemModifiedByOther(WSItem item) throws RemoteException {
        try {
            boolean ret = Util.getItemCtrl2Local()
                    .isItemModifiedByOther(
                            new ItemPOJOPK(new DataClusterPOJOPK(item.getWsDataClusterPK().getPk()), item.getConceptName(),
                                    item.getIds()), item.getInsertionTime());
            return new WSBoolean(ret);
        } catch (Exception e) {
            throw new RemoteException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString countItemsByCustomFKFilters(WSCountItemsByCustomFKFilters wsCountItemsByCustomFKFilters)
            throws RemoteException {
        try {
            long count = Util.getItemCtrl2Local().countItemsByCustomFKFilters(
                    new DataClusterPOJOPK(wsCountItemsByCustomFKFilters.getWsDataClusterPK().getPk()),
                    wsCountItemsByCustomFKFilters.getConceptName(), wsCountItemsByCustomFKFilters.getInjectedXpath());
            return new WSString(String.valueOf(count));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSStringArray getItemsByCustomFKFilters(WSGetItemsByCustomFKFilters wsGetItemsByCustomFKFilters)
            throws RemoteException {
        try {
            Map context = Collections.emptyMap();
            ArrayList res = Util.getItemCtrl2Local().getItemsByCustomFKFilters(
                    new DataClusterPOJOPK(wsGetItemsByCustomFKFilters.getWsDataClusterPK().getPk()),
                    new ArrayList<String>(Arrays.asList(wsGetItemsByCustomFKFilters.getViewablePaths().getStrings())),
                    wsGetItemsByCustomFKFilters.getInjectedXpath(),
                    WS2VO(wsGetItemsByCustomFKFilters.getWhereItem(), new WhereConditionForcePivotFilter(context)),
                    wsGetItemsByCustomFKFilters.getSkip(), wsGetItemsByCustomFKFilters.getMaxItems(),
                    wsGetItemsByCustomFKFilters.getOrderBy(), wsGetItemsByCustomFKFilters.getDirection(),
                    wsGetItemsByCustomFKFilters.getReturnCount());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSString refreshCache(WSRefreshCache refreshCache) {
        ItemPOJO.clearCache();
        ObjectPOJO.clearCache();
        return new WSString("Refresh the item and object cache successfully!");
    }
    
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSBoolean isXmlDB() {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, null); // Retrieves SYSTEM storage
        return new WSBoolean(systemStorage == null);
    }
    
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSDigest getDigest(WSDigestKey wsDigestKey) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, null); // Retrieves SYSTEM storage
        if (systemStorage != null) {
            MetadataRepository repository = systemStorage.getMetadataRepository(); // This repository holds all system object types
            String type = wsDigestKey.getType();
            String name = wsDigestKey.getObjectName();
            systemStorage.begin(); // Storage needs an active transaction (even for read operations).
            try {
                String typeName = DigestHelper.getInstance().getTypeName(type);
                if (typeName != null) {
                    ComplexTypeMetadata storageType = repository.getComplexType(ClassRepository.format(typeName)); // Get the type definition for query 
                    UserQueryBuilder qb = UserQueryBuilder.from(storageType) 
                            .where(UserQueryBuilder.eq(storageType.getField("unique-id"), name)); //$NON-NLS-1$ // Select instance of type where unique-id equals provided name
                    StorageResults results = systemStorage.fetch(qb.getSelect());
                    
                    Iterator<DataRecord> iterator = results.iterator();
                    if (iterator.hasNext()) {
                        DataRecord result = iterator.next();
                        return new WSDigest(wsDigestKey,(String)result.get("digest"), result.getRecordMetadata().getLastModificationTime()); //$NON-NLS-1$
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } finally {
                systemStorage.rollback();
            }
        } else {
            return null;
        }
    }
    
    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSLong updateDigest(WSDigest wsDigest) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, null); // Retrieves SYSTEM storage
        if (systemStorage != null) {
            MetadataRepository repository = systemStorage.getMetadataRepository(); // This repository holds all system object types
            String type = wsDigest.getWsDigestKey().getType();
            String name = wsDigest.getWsDigestKey().getObjectName();
            systemStorage.begin(); // Storage needs an active transaction (even for read operations).
            try {
                String typeName = DigestHelper.getInstance().getTypeName(type);
                if (typeName != null) {
                    ComplexTypeMetadata storageType = repository.getComplexType(ClassRepository.format(typeName));
                    UserQueryBuilder qb = UserQueryBuilder.from(storageType)
                            .where(UserQueryBuilder.eq(storageType.getField("unique-id"), name)) //$NON-NLS-1$
                            .forUpdate(); // <- Important line here!
                    StorageResults results = systemStorage.fetch(qb.getSelect());
                    
                    Iterator<DataRecord> iterator = results.iterator();
                    if (iterator.hasNext()) {
                        DataRecord result = iterator.next();
                        FieldMetadata digestField = storageType.getField("digest"); //$NON-NLS-1$
                        result.set(digestField, MetadataUtils.convert(wsDigest.getDigestValue(), digestField)); // Using convert ensure type is correct                
                        systemStorage.update(result); // No need to set timestamp (update will update it).
                        systemStorage.commit();
                        return new WSLong(result.getRecordMetadata().getLastModificationTime());
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                systemStorage.rollback();
                throw new RuntimeException( e );
            }
        } else {
            return null;
        }
        
    }

    public WSMatchRulePK putMatchRule(WSPutMatchRule wsPutMatchRule) throws RemoteException {
        return null; // Supported only in EE.
    }

    public WSMatchRulePK deleteMatchRule(WSDeleteMatchRule wsDeleteMatchRule) throws RemoteException {
        return null; // Supported only in EE.
    }
}
