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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
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
import com.amalto.core.ejb.TransformerCtrlBean;
import com.amalto.core.ejb.TransformerPOJO;
import com.amalto.core.ejb.TransformerPOJOPK;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.integrity.FKIntegrityCheckResult;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlUtil;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
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
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.save.SaveException;
import com.amalto.core.save.SaverHelper;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.exception.FullTextQueryCompositeKeyException;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.Version;
import com.amalto.core.util.WhereConditionForcePivotFilter;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.util.webservices.*;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;

public abstract class IXtentisRMIPort implements XtentisPort {

    private static Logger LOG = Logger.getLogger(IXtentisRMIPort.class);

    private String INTEGRITY_CONSTRAINT_CHECK_FAILED_MESSAGE = "delete_failure_constraint_violation"; //$NON-NLS-1$

    // full text query entity include composite key
    public static final String FULLTEXT_QUERY_COMPOSITEKEY_EXCEPTION_MESSAGE = "fulltext_query_compositekey_fail"; //$NON-NLS-1$

    // default remote error
    public static final String DEFAULT_REMOTE_ERROR_MESSAGE = "default_remote_error_message"; //$NON-NLS-1$

    /***************************************************************************
     * 
     * S E R V I C E S
     * 
     * **************************************************************************/

    /***************************************************************************
     * Components Management
     * **************************************************************************/

    @Override
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

    @Override
    public WSInt initMDM(WSInitData initData) throws RemoteException {
        throw new RemoteException("initMDM not implemented as RMI call");
    }

    /***************************************************************************
     * Logout
     * **************************************************************************/

    @Override
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

    @Override
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget) throws RemoteException {
        try {
            return XConverter.VO2WS(com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
                    new DataModelPOJOPK(wsDataModelget.getWsDataModelPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp) throws RemoteException {
        try {

            WSDataModelPKArray array = new WSDataModelPKArray();
            Collection<DataModelPOJOPK> pks = com.amalto.core.util.Util.getDataModelCtrlLocal()
                    .getDataModelPKs(regexp.getRegex());
            ArrayList<WSDataModelPK> list = new ArrayList<WSDataModelPK>();
            for (DataModelPOJOPK pk : pks) {
                WSDataModelPK dmpk = new WSDataModelPK(pk.getUniqueId());
                list.add(dmpk);
            }
            array.setWsDataModelPKs(list.toArray(new WSDataModelPK[list.size()]));
            return array;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel) throws RemoteException {
        try {
            return new WSDataModelPK(com.amalto.core.util.Util.getDataModelCtrlLocal()
                    .removeDataModel(new DataModelPOJOPK(wsDeleteDataModel.getWsDataModelPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel) throws RemoteException {
        try {
            return new WSBoolean((Util.getDataModelCtrlLocal().existsDataModel(
                    new DataModelPOJOPK(wsExistsDataModel.getWsDataModelPK().getPk())) != null));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel) throws RemoteException {
        try {
            WSDataModelPK wsDataModelPK = new WSDataModelPK(Util.getDataModelCtrlLocal()
                    .putDataModel(XConverter.WS2VO(wsDataModel.getWsDataModel())).getUniqueId());

            SaverSession session = SaverSession.newSession();
            session.invalidateTypeCache(wsDataModelPK.getPk());
            session.end();

            return wsDataModelPK;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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
            Boolean checkResult = (Boolean) Util.getMethod(service, "checkConfigure").invoke(service);
            serviceConfigResponse.setCheckResult(checkResult);

        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

        return serviceConfigResponse;
    }

    @Override
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept) throws RemoteException {
        WSBusinessConcept bc = wsPutBusinessConcept.getBusinessConcept();
        try {
            String s = "<xsd:element name=" + bc.getName() + " type=" + bc.getBusinessTemplate() + ">" + "	<xsd:annotation>";
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

    @Override
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws RemoteException {
        try {
            return new WSString(Util.getDataModelCtrlLocal().putBusinessConceptSchema(
                    new DataModelPOJOPK(wsPutBusinessConceptSchema.getWsDataModelPK().getPk()),
                    wsPutBusinessConceptSchema.getBusinessConceptSchema()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSString deleteBusinessConcept(WSDeleteBusinessConcept wsDeleteBusinessConcept) throws RemoteException {
        try {
            return new WSString(com.amalto.core.util.Util.getDataModelCtrlLocal().deleteBusinessConcept(
                    new DataModelPOJOPK(wsDeleteBusinessConcept.getWsDataModelPK().getPk()),
                    wsDeleteBusinessConcept.getBusinessConceptName()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSStringArray getBusinessConcepts(WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
        try {
            return new WSStringArray(com.amalto.core.util.Util.getDataModelCtrlLocal().getAllBusinessConceptsNames(
                    new DataModelPOJOPK(wsGetBusinessConcepts.getWsDataModelPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
        try {
            MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
            MetadataRepository repository = metadataRepositoryAdmin.get(wsGetBusinessConceptKey.getWsDataModelPK().getPk());
            ComplexTypeMetadata type = repository.getComplexType(wsGetBusinessConceptKey.getConcept());
            Collection<FieldMetadata> keyFields = type.getKeyFields();
            String[] fields = new String[keyFields.size()];
            int i = 0;
            for (FieldMetadata keyField : keyFields) {
                fields[i++] = keyField.getName();
            }
            return new WSConceptKey(".", fields);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * DataCluster
     * **************************************************************************/

    @Override
    public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet) throws RemoteException {
        try {
            return XConverter.VO2WS(com.amalto.core.util.Util.getDataClusterCtrlLocal().getDataCluster(
                    new DataClusterPOJOPK(wsDataClusterGet.getWsDataClusterPK().getPk())));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster) throws RemoteException {
        try {
            return new WSBoolean(com.amalto.core.util.Util.getDataClusterCtrlLocal().existsDataCluster(
                    new DataClusterPOJOPK(wsExistsDataCluster.getWsDataClusterPK().getPk())) != null);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp) throws RemoteException {
        try {
            WSDataClusterPKArray array = new WSDataClusterPKArray();
            Collection<DataClusterPOJOPK> pks = com.amalto.core.util.Util.getDataClusterCtrlLocal().getDataClusterPKs(
                    regexp.getRegex());
            ArrayList<WSDataClusterPK> list = new ArrayList<WSDataClusterPK>();
            for (DataClusterPOJOPK pk : pks) {
                list.add(new WSDataClusterPK(pk.getUniqueId()));
            }
            array.setWsDataClusterPKs(list.toArray(new WSDataClusterPK[list.size()]));
            return array;
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
        try {
            return new WSDataClusterPK(com.amalto.core.util.Util.getDataClusterCtrlLocal()
                    .removeDataCluster(new DataClusterPOJOPK(wsDeleteDataCluster.getWsDataClusterPK().getPk())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster) throws RemoteException {
        try {
            return new WSDataClusterPK(com.amalto.core.util.Util.getDataClusterCtrlLocal()
                    .putDataCluster(XConverter.WS2VO(wsDataCluster.getWsDataCluster())).getUniqueId());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public WSBoolean existsView(WSExistsView wsExistsView) throws RemoteException {
        try {
            return new WSBoolean(com.amalto.core.util.Util.getViewCtrlLocalHome().create()
                    .existsView(new ViewPOJOPK(wsExistsView.getWsViewPK().getPk())) != null);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
        try {
            ArrayList l;
            String regex = regexp.getRegex() != null && !"".equals(regexp.getRegex()) && !"*".equals(regexp.getRegex()) ? regexp //$NON-NLS-1$
                    .getRegex() : ".*"; //$NON-NLS-1$
            Collection list = com.amalto.core.util.Util.getViewCtrlLocalHome().create().getViewPKs(regex);
            l = new ArrayList();
            ViewPOJOPK pk;
            for (Iterator iter = list.iterator(); iter.hasNext(); l.add(new WSViewPK(pk.getIds()[0]))) {
                pk = (ViewPOJOPK) iter.next();
            }
            return new WSViewPKArray((WSViewPK[]) l.toArray(new WSViewPK[l.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
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

    @Override
    public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
        try {
            Collection res = Util.getItemCtrl2Local().viewSearch(
                    new DataClusterPOJOPK(wsViewSearch.getWsDataClusterPK().getPk()),
                    new ViewPOJOPK(wsViewSearch.getWsViewPK().getPk()), XConverter.WS2VO(wsViewSearch.getWhereItem()),
                    wsViewSearch.getSpellTreshold(), wsViewSearch.getOrderBy(), wsViewSearch.getDirection(),
                    wsViewSearch.getSkip(), wsViewSearch.getMaxItems());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));

        } catch (com.amalto.core.util.XtentisException e) {
            throw handleException(e);
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
        try {
            Collection res = com.amalto.core.util.Util.getItemCtrl2Local().xPathsSearch(
                    new DataClusterPOJOPK(wsXPathsSearch.getWsDataClusterPK().getPk()), wsXPathsSearch.getPivotPath(),
                    new ArrayList<String>(Arrays.asList(wsXPathsSearch.getViewablePaths().getStrings())),
                    XConverter.WS2VO(wsXPathsSearch.getWhereItem()), wsXPathsSearch.getSpellTreshold(),
                    wsXPathsSearch.getOrderBy(), wsXPathsSearch.getDirection(), wsXPathsSearch.getSkip(),
                    wsXPathsSearch.getMaxItems(), wsXPathsSearch.getReturnCount());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
        try {
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, wsGetItems.getConceptName());

            Collection res = com.amalto.core.util.Util.getItemCtrl2Local().getItems(
                    new DataClusterPOJOPK(wsGetItems.getWsDataClusterPK().getPk()), wsGetItems.getConceptName(),
                    XConverter.WS2VO(wsGetItems.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsGetItems.getSpellTreshold(), wsGetItems.getSkip(), wsGetItems.getMaxItems(),
                    wsGetItems.getTotalCountOnFirstResult());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSStringArray getItemsSort(WSGetItemsSort wsGetItemsSort) throws RemoteException {
        try {
            Map wcfContext = new HashMap();
            wcfContext.put(WhereConditionForcePivotFilter.FORCE_PIVOT, wsGetItemsSort.getConceptName());

            Collection res = com.amalto.core.util.Util.getItemCtrl2Local().getItems(
                    new DataClusterPOJOPK(wsGetItemsSort.getWsDataClusterPK().getPk()), wsGetItemsSort.getConceptName(),
                    XConverter.WS2VO(wsGetItemsSort.getWhereItem(), new WhereConditionForcePivotFilter(wcfContext)),
                    wsGetItemsSort.getSpellTreshold(), wsGetItemsSort.getDir(), wsGetItemsSort.getSort(),
                    wsGetItemsSort.getSkip(), wsGetItemsSort.getMaxItems(), wsGetItemsSort.getTotalCountOnFirstResult());
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
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

    @Override
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
            String entityNotFoundErrorMessage = "entity_not_found";
            if (com.amalto.webapp.core.util.Util.causeIs(e, com.amalto.core.util.EntityNotFoundException.class)) {
                throw new RemoteException(
                        "", new WebCoreException(entityNotFoundErrorMessage, com.amalto.webapp.core.util.Util.cause(e, com.amalto.core.util.EntityNotFoundException.class))); //$NON-NLS-1$
            } else if (com.amalto.webapp.core.util.Util.causeIs(e, org.hibernate.ObjectNotFoundException.class)) {
                throw new RemoteException(
                        "", new WebCoreException(entityNotFoundErrorMessage, com.amalto.webapp.core.util.Util.cause(e, org.hibernate.ObjectNotFoundException.class))); //$NON-NLS-1$
            }
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
        try {
            Collection c = com.amalto.core.util.Util.getItemCtrl2Local().quickSearch(
                    new DataClusterPOJOPK(wsQuickSearch.getWsDataClusterPK().getPk()),
                    new ViewPOJOPK(wsQuickSearch.getWsViewPK().getPk()), wsQuickSearch.getSearchedValue(),
                    wsQuickSearch.isMatchAllWords(), wsQuickSearch.getSpellTreshold(), wsQuickSearch.getOrderBy(),
                    wsQuickSearch.getDirection(), wsQuickSearch.getSkip(), wsQuickSearch.getMaxItems());
            if (c == null) {
                return null;
            }
            return new WSStringArray((String[]) c.toArray(new String[c.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
        try {

            Collection res = Util.getItemCtrl2Local().getFullPathValues(
                    new DataClusterPOJOPK(wsGetFullPathValues.getWsDataClusterPK().getPk()), wsGetFullPathValues.getFullPath(),
                    XConverter.WS2VO(wsGetFullPathValues.getWhereItem()), wsGetFullPathValues.getSpellThreshold(),
                    wsGetFullPathValues.getOrderBy(), wsGetFullPathValues.getDirection());

            if (res == null) {
                return null;
            }

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
        for (String id : ids) {
            item += "	<id>" + id + "</id>";
        }
        item += "	<lastmodifiedtime>" + iv.getInsertionTime() + "</lastmodifiedtime>";
        item += "	<projection>" + iv.getProjection() + "</projection>";
        item += "</businessconcept>";

        return item;
    }

    /***************************************************************************
     * Put Item
     * **************************************************************************/
    @Override
    public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
        try {
            WSDataClusterPK dataClusterPK = wsPutItem.getWsDataClusterPK();
            WSDataModelPK dataModelPK = wsPutItem.getWsDataModelPK();

            String dataClusterName = dataClusterPK.getPk();
            String dataModelName = dataModelPK.getPk();

            SaverSession session = SaverSession.newSession();
            DocumentSaver saver;
            try {
                session.begin(dataClusterPK.getPk());
                saver = SaverHelper.saveItem(wsPutItem.getXmlString(), session, !wsPutItem.getIsUpdate(), dataClusterName,
                        dataModelName);
                // Cause items being saved to be committed to database.
                session.end();
            } catch (Exception e) {
                try {
                    session.abort();
                } catch (Exception e1) {
                    LOG.error("Exception occurred during rollback.", e1);
                }
                throw new RuntimeException(e);
            }

            String[] savedId = saver.getSavedId();
            String savedConceptName = saver.getSavedConceptName();
            return new WSItemPK(dataClusterPK, savedConceptName, savedId);
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Extract Items
     * **************************************************************************/
    @Override
    public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
        throw new RemoteException("Not Supported!");
    }

    @Override
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
    @Override
    public WSItemPK deleteItem(WSDeleteItem wsDeleteItem) throws RemoteException {
        try {
            ItemPOJOPK itemPK = new ItemPOJOPK(new DataClusterPOJOPK(wsDeleteItem.getWsItemPK().getWsDataClusterPK().getPk()),
                    wsDeleteItem.getWsItemPK().getConceptName(), wsDeleteItem.getWsItemPK().getIds());
            ItemPOJOPK ipk = com.amalto.core.util.Util.getItemCtrl2Local().deleteItem(itemPK, wsDeleteItem.getOverride());
            return ipk == null ? null : wsDeleteItem.getWsItemPK();
        } catch (com.amalto.core.util.XtentisException e) {
            if (com.amalto.webapp.core.util.Util.causeIs(e, com.amalto.core.storage.exception.ConstraintViolationException.class)) {
                throw new RemoteException(
                        "", new WebCoreException(INTEGRITY_CONSTRAINT_CHECK_FAILED_MESSAGE, com.amalto.webapp.core.util.Util.cause(e, com.amalto.core.storage.exception.ConstraintViolationException.class))); //$NON-NLS-1$                     
            }
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSInt deleteItems(WSDeleteItems wsDeleteItems) throws RemoteException {
        try {
            int numItems = com.amalto.core.util.Util.getItemCtrl2Local().deleteItems(
                    new DataClusterPOJOPK(wsDeleteItems.getWsDataClusterPK().getPk()), wsDeleteItems.getConceptName(),
                    XConverter.WS2VO(wsDeleteItems.getWsWhereItem()), wsDeleteItems.getSpellTreshold(),
                    wsDeleteItems.isOverride());
            return new WSInt(numItems);
        } catch (com.amalto.core.util.XtentisException e) {
            if (com.amalto.webapp.core.util.Util.causeIs(e, com.amalto.core.storage.exception.ConstraintViolationException.class)) {
                throw new RemoteException("", new WebCoreException(INTEGRITY_CONSTRAINT_CHECK_FAILED_MESSAGE, e.getCause())); //$NON-NLS-1$                     
            }
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * DirectQuery
     * **************************************************************************/

    @Override
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
    @Override
    public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet) throws RemoteException {
        try {
            return XConverter.VO2WS(Util.getRoutingRuleCtrlLocal().getRoutingRule(
                    new RoutingRulePOJOPK(wsRoutingRuleGet.getWsRoutingRulePK().getPk())));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regexp) throws RemoteException {
        try {
            Collection<RoutingRulePOJOPK> pks = Util.getRoutingRuleCtrlLocal().getRoutingRulePKs(regexp.getRegex());
            ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
            for (Object element : pks) {
                RoutingRulePOJOPK pk = (RoutingRulePOJOPK) element;
                list.add(new WSRoutingRulePK(pk.getUniqueId()));
            }
            return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule) throws RemoteException {
        try {
            return new WSRoutingRulePK(Util.getRoutingRuleCtrlLocal()
                    .putRoutingRule(XConverter.WS2VO(wsRoutingRule.getWsRoutingRule())).getUniqueId());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * SERVICES
     * **************************************************************************/

    @Override
    public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsGetConfiguration.getJndiName());

            String configuration = (String) com.amalto.core.util.Util.getMethod(service, "getConfiguration").invoke(service,//$NON-NLS-1$
                    new Object[] { wsGetConfiguration.getOptionalParameter() });
            return new WSString(configuration);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsPutConfiguration.getJndiName());

            com.amalto.core.util.Util.getMethod(service, "putConfiguration").invoke(service,//$NON-NLS-1$
                    new Object[] { wsPutConfiguration.getConfiguration() });
            return new WSString(wsPutConfiguration.getConfiguration());
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("serviceAction() " + wsServiceAction.getJndiName());
        try {
            Object service = com.amalto.core.util.Util.retrieveComponent(null, wsServiceAction.getJndiName());
            String result = "";//$NON-NLS-1$

            if (WSServiceActionCode.EXECUTE.equals(wsServiceAction.getWsAction())) {

                Method method = com.amalto.core.util.Util.getMethod(service, wsServiceAction.getMethodName());

                result = (String) method.invoke(service, wsServiceAction.getMethodParameters());
            } else {
                if (WSServiceActionCode.START.equals(wsServiceAction.getWsAction())) {
                    com.amalto.core.util.Util.getMethod(service, "start").invoke(service, new Object[] {});//$NON-NLS-1$
                } else if (WSServiceActionCode.STOP.equals(wsServiceAction.getWsAction())) {
                    com.amalto.core.util.Util.getMethod(service, "stop").invoke(service, new Object[] {});//$NON-NLS-1$
                }
                result = (String) com.amalto.core.util.Util.getMethod(service, "getStatus").invoke(service, new Object[] {});//$NON-NLS-1$
            }
            return new WSString(result);
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
        try {
            ArrayList<WSServicesListItem> wsList = new ArrayList<WSServicesListItem>();
            InitialContext ctx = new InitialContext();
            NamingEnumeration<NameClassPair> list = ctx.list("amalto/local/service");//$NON-NLS-1$
            while (list.hasMore()) {
                NameClassPair nc = list.next();
                WSServicesListItem item = new WSServicesListItem();
                item.setJndiName(nc.getName());
                wsList.add(item);
            }
            return new WSServicesList(wsList.toArray(new WSServicesListItem[wsList.size()]));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Stored Procedures
     * **************************************************************************/

    @Override
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

    @Override
    public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            Collection c = ctrl.execute(new StoredProcedurePOJOPK(wsExecuteStoredProcedure.getWsStoredProcedurePK().getPk()),
                    wsExecuteStoredProcedure.getRevisionID(), new DataClusterPOJOPK(wsExecuteStoredProcedure.getWsDataClusterPK()
                            .getPk()), wsExecuteStoredProcedure.getParameters());
            if (c == null) {
                return null;
            }
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

    @Override
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

    @Override
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

    @Override
    public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
        try {
            StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
            Collection c = ctrl.getStoredProcedurePKs(regex.getRegex());
            if (c == null) {
                return null;
            }
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

    @Override
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

    @Override
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

    @Override
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
            if (cxFactory == null) {
                cxFactory = (ConnectionFactory) (new InitialContext()).lookup(JNDIName);
            }
            return cxFactory.getConnection();
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private HashMap getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (params != null) {
                for (WSBase64KeyValue param : params) {
                    if (param != null) {
                        String key = param.getKey();
                        byte[] bytes = (new BASE64Decoder()).decodeBuffer(param.getBase64StringValue());
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
            if (params == null) {
                return null;
            }
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

    @Override
    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            return new WSTransformerPK(ctrl.removeTransformer(
                    new TransformerPOJOPK(wsTransformerDelete.getWsTransformerPK().getPk())).getUniqueId());

        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            TransformerPOJO pojo = ctrl.getTransformer(new TransformerPOJOPK(wsGetTransformer.getWsTransformerPK().getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    @Override
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

    @Override
    public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            Collection c = ctrl.getTransformerPKs(regex.getRegex());
            if (c == null) {
                return null;
            }
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

    @Override
    public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
        try {
            TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
            TransformerPOJOPK pk = ctrl.putTransformer(XConverter.WS2POJO(wsTransformer.getWsTransformer()));
            return new WSTransformerPK(pk.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    @Override
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
            for (Object element : pks) {
                ItemPOJOPK pk = (ItemPOJOPK) element;
                if (!"".equals(pksAsLine)) {
                    pksAsLine += "\n";
                }
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

    @Override
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

    @Override
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
            for (Object element : pks) {
                ItemPOJOPK pk = (ItemPOJOPK) element;
                if (!"".equals(pksAsLine)) {
                    pksAsLine += "\n";
                }
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

    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            Collection c = ctrl.getTransformerPKs(regex.getRegex());
            if (c == null) {
                return null;
            }
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
    @Override
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
    @Override
    public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
        try {
            final String RUNNING = "XtentisWSBean.executeTransformerV2.running";
            TransformerContext context = XConverter.WS2POJO(wsExecuteTransformerV2.getWsTransformerContext());
            context.put(RUNNING, Boolean.TRUE);
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            ctrl.execute(context, XConverter.WS2POJO(wsExecuteTransformerV2.getWsTypedContent()), new TransformerCallBack() {

                @Override
                public void contentIsReady(TransformerContext context) throws com.amalto.core.util.XtentisException {
                    org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                            "XtentisWSBean.executeTransformerV2.contentIsReady() ");
                }

                @Override
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
    @Override
    public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
            throws RemoteException {
        try {
            TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
            BackgroundJobPOJOPK bgPK = ctrl.executeAsJob(
                    XConverter.WS2POJO(wsExecuteTransformerV2AsJob.getWsTransformerContext()), new TransformerCallBack() {

                        @Override
                        public void contentIsReady(TransformerContext context) throws com.amalto.core.util.XtentisException {
                            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                    "XtentisWSBean.executeTransformerV2AsJob.contentIsReady() ");
                        }

                        @Override
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
    @Override
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

    /***************************************************************************
     * TRANSFORMER PLUGINS V2
     * **************************************************************************/

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
                for (Object element : inputVariableDescriptors) {
                    TransformerPluginVariableDescriptor descriptor = (TransformerPluginVariableDescriptor) element;
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
                for (Object element : outputVariableDescriptors) {
                    TransformerPluginVariableDescriptor descriptor = (TransformerPluginVariableDescriptor) element;
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
    @Override
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
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    /***************************************************************************
     * Menu
     * **************************************************************************/

    @Override
    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            return new WSMenuPK(ctrl.removeMenu(new MenuPOJOPK(wsMenuDelete.getWsMenuPK().getPk())).getUniqueId());

        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            MenuPOJO pojo = ctrl.getMenu(new MenuPOJOPK(wsGetMenu.getWsMenuPK().getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    @Override
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

    @Override
    public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            Collection c = ctrl.getMenuPKs(regex.getRegex());
            if (c == null) {
                return null;
            }
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

    @Override
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

    @Override
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs status) throws RemoteException {
        try {
            throw new RemoteException("WSBackgroundJobPKArray is not implemented in this version of the core");
        } catch (RemoteException e) {
            throw (new RemoteException(e.getLocalizedMessage()));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
            WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
        try {
            WSRoutingOrderV2PKArray wsPKArray = new WSRoutingOrderV2PKArray();
            ArrayList<WSRoutingOrderV2PK> list = new ArrayList<WSRoutingOrderV2PK>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2PKsByCriteria
                    .getWsSearchCriteria());
            for (AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK : pks) {
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

    @Override
    public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
            throws RemoteException {
        try {
            RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
            WSRoutingOrderV2Array wsPKArray = new WSRoutingOrderV2Array();
            ArrayList<WSRoutingOrderV2> list = new ArrayList<WSRoutingOrderV2>();
            // fetch results
            Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2SByCriteria
                    .getWsSearchCriteria());
            for (AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK : pks) {
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

    @Override
    public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            RoutingRulePOJOPK[] rules = ctrl.route(XConverter.WS2POJO(wsRouteItem.getWsItemPK()));
            ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
            if (rules == null || rules.length == 0) {
                return null;
            }
            for (RoutingRulePOJOPK rule : rules) {
                list.add(new WSRoutingRulePK(rule.getUniqueId()));
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

    @Override
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

    @Override
    public com.amalto.webapp.util.webservices.WSServiceGetDocument getServiceDocument(WSString serviceName)
            throws RemoteException {
        try {
            Object service = Util.retrieveComponent(null, "amalto/local/service/" + serviceName.getValue());//$NON-NLS-1$

            String desc = (String) Util.getMethod(service, "getDescription").invoke(service, new Object[] { "" });//$NON-NLS-1$ //$NON-NLS-2$
            String configuration = (String) Util.getMethod(service, "getConfiguration").invoke(service, new Object[] { "" }); //$NON-NLS-1$
            String doc = "";
            try {
                doc = (String) Util.getMethod(service, "getDocumentation").invoke(service, new Object[] { "" });//$NON-NLS-1$ //$NON-NLS-2$
            } catch (Exception e) {
                e.printStackTrace();
            }
            String schema = "";//$NON-NLS-1$
            schema = (String) Util.getMethod(service, "getConfigurationSchema").invoke(service, new Object[] {});//$NON-NLS-1$
            String defaultConf = "";//$NON-NLS-1$
            defaultConf = (String) Util.getMethod(service, "getDefaultConfiguration").invoke(service, new Object[] {});//$NON-NLS-1$
            return new com.amalto.webapp.util.webservices.WSServiceGetDocument(desc, configuration, doc, schema, defaultConf);
        } catch (XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSDroppedItemPK dropItem(WSDropItem wsDropItem) throws RemoteException {
        try {
            WSItemPK wsItemPK = wsDropItem.getWsItemPK();
            String partPath = wsDropItem.getPartPath();

            DroppedItemPOJOPK droppedItemPOJOPK = Util.getItemCtrl2Local().dropItem(XConverter.WS2POJO(wsItemPK), partPath,
                    wsDropItem.isOverride());

            return XConverter.POJO2WS(droppedItemPOJOPK);

        } catch (com.amalto.core.util.XtentisException e) {
            if (com.amalto.webapp.core.util.Util.causeIs(e, com.amalto.core.storage.exception.ConstraintViolationException.class)) {
                throw new RemoteException(
                        "", new WebCoreException(INTEGRITY_CONSTRAINT_CHECK_FAILED_MESSAGE, com.amalto.webapp.core.util.Util.cause(e, com.amalto.core.storage.exception.ConstraintViolationException.class))); //$NON-NLS-1$                     
            }
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
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

    @Override
    public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem) throws RemoteException {
        try {
            ItemPOJOPK itemPOJOPK = Util.getDroppedItemCtrlLocal().recoverDroppedItem(
                    XConverter.WS2POJO(wsRecoverDroppedItem.getWsDroppedItemPK()));
            return XConverter.POJO2WS(itemPOJOPK);
        } catch (XtentisException e) {
            throw new RemoteException(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
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

    @Override
    public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        WSPutItem wsPutItem = wsPutItemWithReport.getWsPutItem();
        WebSaver webSaver = new WebSaver(wsPutItem.getWsDataClusterPK().getPk(), wsPutItem.getWsDataModelPK().getPk(),
                SaverSession.newSession());
        return webSaver.saveItemWithReport(wsPutItemWithReport);
    }

    @Override
    public WSMDMConfig getMDMConfiguration() throws RemoteException {
        WSMDMConfig mdmConfig = new WSMDMConfig();
        Properties property = MDMConfiguration.getConfiguration();
        try {
            mdmConfig.setServerName(property.getProperty("xmldb.server.name"));//$NON-NLS-1$
            mdmConfig.setServerPort(property.getProperty("xmldb.server.port"));//$NON-NLS-1$
            mdmConfig.setUserName(property.getProperty("xmldb.administrator.username"));//$NON-NLS-1$
            mdmConfig.setPassword(property.getProperty("xmldb.administrator.password"));//$NON-NLS-1$
            mdmConfig.setXdbDriver(property.getProperty("xmldb.driver"));//$NON-NLS-1$
            mdmConfig.setXdbID(property.getProperty("xmldb.dbid"));//$NON-NLS-1$
            mdmConfig.setXdbUrl(property.getProperty("xmldb.dburl"));//$NON-NLS-1$
            mdmConfig.setIsupurl(property.getProperty("xmldb.isupurl"));//$NON-NLS-1$
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

        return mdmConfig;
    }

    @Override
    public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
        WSPutItem[] items = wsPutItemArray.getWsPutItem();
        try {
            List<WSItemPK> pks = new LinkedList<WSItemPK>();
            SaverSession session = SaverSession.newSession();
            for (WSPutItem item : items) {
                String dataClusterName = item.getWsDataClusterPK().getPk();
                String dataModelName = item.getWsDataModelPK().getPk();

                DocumentSaver saver = SaverHelper.saveItem(item.getXmlString(), session, !item.getIsUpdate(), dataClusterName,
                        dataModelName);
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

    @Override
    public WSItemPKArray putItemWithReportArray(WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException {
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
                    saver = SaverHelper.saveItemWithReport(wsPutItem.getXmlString(), session, !wsPutItem.getIsUpdate(),
                            dataClusterName, dataModelName, source, item.getInvokeBeforeSaving());
                    item.setSource(saver.getBeforeSavingMessage()); // TODO Expected (legacy) behavior: set the before
                                                                    // saving message as source.
                } catch (SaveException e) {
                    item.setSource(e.getBeforeSavingMessage()); // TODO Expected (legacy) behavior: set the before
                                                                // saving message as source.
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

    @Override
    public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
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

    @Override
    public WSItemPKsByCriteriaResponse getItemPKsByFullCriteria(WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria)
            throws RemoteException {
        return doGetItemPKsByCriteria(wsGetItemPKsByFullCriteria.getWsGetItemPKsByCriteria(),
                wsGetItemPKsByFullCriteria.isUseFTSearch());
    }

    @Override
    public WSCategoryData getMDMCategory(WSCategoryData wsCategoryDataRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FKIntegrityCheckResult checkFKIntegrity(WSDeleteItem item) throws RemoteException {
        try {
            WSItemPK wsItemPK = item.getWsItemPK();
            String dataClusterName = wsItemPK.getWsDataClusterPK().getPk();
            String conceptName = wsItemPK.getConceptName();
            String[] ids = wsItemPK.getIds();

            return Util.getItemCtrl2Local().checkFKIntegrity(dataClusterName, conceptName, ids);
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    @Override
    public WSItemPK putItemByOperatorType(WSPutItemByOperatorType putItemByOperatorType) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WSBoolean isItemModifiedByOther(WSIsItemModifiedByOther wsItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
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

    @Override
    public WSStringArray getItemsByCustomFKFilters(WSGetItemsByCustomFKFilters wsGetItemsByCustomFKFilters)
            throws RemoteException {
        try {
            ArrayList res = Util.getItemCtrl2Local().getItemsByCustomFKFilters(
                    new DataClusterPOJOPK(wsGetItemsByCustomFKFilters.getWsDataClusterPK().getPk()),
                    new ArrayList<String>(Arrays.asList(wsGetItemsByCustomFKFilters.getViewablePaths().getStrings())),
                    wsGetItemsByCustomFKFilters.getInjectedXpath(), XConverter.WS2VO(wsGetItemsByCustomFKFilters.getWhereItem()),
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

    private WSItemPKsByCriteriaResponse doGetItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria, boolean useFTSearch)
            throws RemoteException {
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
            for (Object element : results) {
                String result = (String) element;
                if (i == 0) {
                    res[i++] = new WSItemPKsByCriteriaResponseResults(System.currentTimeMillis(), new WSItemPK(
                            wsGetItemPKsByCriteria.getWsDataClusterPK(), result, null), ""); //$NON-NLS-1$
                    continue;
                }
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

    @Override
    public WSBoolean isPagingAccurate(WSInt currentTotalSize) throws RemoteException {
        List<String> noSupportAccurateDbs = Arrays.asList("qizx");//$NON-NLS-1$
        Properties props = MDMConfiguration.getConfiguration();
        String dbName = props.getProperty("xmldb.type");//$NON-NLS-1$
        WSBoolean result = new WSBoolean(true);
        if (noSupportAccurateDbs.contains(dbName)) {
            String ecountsamplesize = props.getProperty("xmldb.qizx.ecountsamplesize"); //$NON-NLS-1$
            if (ecountsamplesize != null && ecountsamplesize.trim().length() > 0) {
                int size = Integer.parseInt(ecountsamplesize);
                if (currentTotalSize.getValue() > size) {
                    result.set_true(false);
                }
            }
        }
        return result;
    }

    @Override
    public WSBoolean supportStaging() throws RemoteException {
        WSBoolean result = new WSBoolean(false);
        try {
            Configuration config = Configuration.getConfiguration();
            result.set_true(Util.getXmlServerCtrlLocal().supportStaging(config.getCluster()));
        } catch (com.amalto.core.util.XtentisException e) {
            throw (new RemoteException(e.getLocalizedMessage(), e));
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
        return result;
    }

    private RemoteException handleException(Throwable throwable) {
        WebCoreException webCoreException;
        if (WebCoreException.class.isInstance(throwable)) {
            webCoreException = (WebCoreException) throwable;
        } else if (FullTextQueryCompositeKeyException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(FULLTEXT_QUERY_COMPOSITEKEY_EXCEPTION_MESSAGE, throwable);
        } else {
            if (throwable.getCause() != null) {
                return handleException(throwable.getCause());
            } else {
                webCoreException = new WebCoreException(DEFAULT_REMOTE_ERROR_MESSAGE, throwable);
            }
        }
        return new RemoteException("", webCoreException); //$NON-NLS-1$       
    }
}
