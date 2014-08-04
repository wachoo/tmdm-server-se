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
package org.talend.mdm.webapp.browserecords.server.actions;

import java.io.Serializable;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.save.DOMDocument;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.talend.mdm.commmon.metadata.*;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.ReusableType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.BasePagingLoadConfigImpl;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.util.MultilanguageMessageParser;
import org.talend.mdm.webapp.base.server.BaseConfiguration;
import org.talend.mdm.webapp.base.server.ForeignKeyHelper;
import org.talend.mdm.webapp.base.server.exception.WebBaseException;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.shared.ComplexTypeModel;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsService;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.RecordsPagingConfig;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.client.model.SearchTemplate;
import org.talend.mdm.webapp.browserecords.client.model.UpdateItemModel;
import org.talend.mdm.webapp.browserecords.client.util.StagingConstant;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ItemHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.RoleHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.TypeModelNotFoundException;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.browserecords.server.provider.DefaultSmartViewProvider;
import org.talend.mdm.webapp.browserecords.server.provider.SmartViewProvider;
import org.talend.mdm.webapp.browserecords.server.ruleengine.DisplayRuleEngine;
import org.talend.mdm.webapp.browserecords.server.util.BrowseRecordsConfiguration;
import org.talend.mdm.webapp.browserecords.server.util.DynamicLabelUtil;
import org.talend.mdm.webapp.browserecords.server.util.SmartViewUtil;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;
import org.talend.mdm.webapp.browserecords.shared.SmartViewDescriptions;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.integrity.FKIntegrityCheckResult;
import com.amalto.core.objects.customform.ejb.CustomFormPOJO;
import com.amalto.core.objects.customform.ejb.CustomFormPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.storage.task.StagingConstants;
import com.amalto.core.util.EntityNotFoundException;
import com.amalto.core.util.FieldNotFoundException;
import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.DataModelAccessor;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.WebCoreException;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.core.util.XmlUtil;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSByteArray;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSDropItem;
import com.amalto.webapp.util.webservices.WSExecuteTransformerV2;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetTransformer;
import com.amalto.webapp.util.webservices.WSGetTransformerPKs;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSInt;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSTransformer;
import com.amalto.webapp.util.webservices.WSTransformerContext;
import com.amalto.webapp.util.webservices.WSTransformerContextPipelinePipelineItem;
import com.amalto.webapp.util.webservices.WSTransformerPK;
import com.amalto.webapp.util.webservices.WSTransformerV2PK;
import com.amalto.webapp.util.webservices.WSTypedContent;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.extjs.gxt.ui.client.Style.SortDir;

public class BrowseRecordsAction implements BrowseRecordsService {

    private final Logger LOG = Logger.getLogger(BrowseRecordsAction.class);

    private final Messages BASEMESSAGE = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.base.client.i18n.BaseMessages", this.getClass().getClassLoader()); //$NON-NLS-1$

    protected final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages", this.getClass().getClassLoader()); //$NON-NLS-1$

    private final List<String> dateTypeNames = Arrays.asList(Types.DATES);

    private final List<String> numberTypeNames = Arrays.asList(Types.NUMBERS);

    private final DocumentBuilder documentBuilder;

    public BrowseRecordsAction() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            documentBuilder = new SkipAttributeDocumentBuilder(builder, false);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to create DOM parser.", e);
        }
    }

    @Override
    public List<String> deleteItemBeans(List<ItemBean> items, boolean override, String language) throws ServiceException {
        List<String> itemResults = new ArrayList<String>();
        MetadataRepository repository = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin().get(getCurrentDataModel());
        for (ItemBean item : items) {
            Locale locale = new Locale(language);
            try {
                String dataClusterPK = getCurrentDataCluster();
                String concept = item.getConcept();
                String[] ids = getItemId(repository, item, concept);
                if (ids != null && !item.isReadOnly()) {
                    WSItemPK itemPK = new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids);
                    WSDeleteItem wsDeleteItem = new WSDeleteItem(itemPK, override);
                    WSItemPK wsItem = CommonUtil.getPort().deleteItem(wsDeleteItem);
                    if (wsItem == null) {
                        throw new ServiceException(MESSAGES.getMessage("delete_record_failure", locale)); //$NON-NLS-1$
                    }
                    itemResults.add(MESSAGES.getMessage("delete_process_validation_success", locale)); //$NON-NLS-1$
                } else {
                    itemResults.add(MESSAGES.getMessage("delete_item_record_successNoupdate", locale)); //$NON-NLS-1$
                }
            } catch (ServiceException e) {
                LOG.error(e.getMessage(), e);
                throw e;
            } catch (WebBaseException e) {
                throw new ServiceException(BASEMESSAGE.getMessage(locale, e.getMessage(), e.getArgs()));
            } catch (Exception exception) {
                String errorMessage;
                if (WebCoreException.class.isInstance(exception.getCause())) {
                    errorMessage = getErrorMessageFromWebCoreException(((WebCoreException) exception.getCause()),
                            item.getConcept(), item.getIds(), locale);
                } else {
                    errorMessage = exception.getMessage();
                }
                LOG.error(errorMessage, exception);
                throw new ServiceException(errorMessage);
            }
        }
        return itemResults;
    }

    private String[] getItemId(MetadataRepository repository, ItemBean item, String concept) throws WebBaseException {
        ComplexTypeMetadata itemType = repository.getComplexType(concept);
        String[] keyPaths = new String[itemType.getKeyFields().size()];
        int i = 0;
        for (FieldMetadata keyField : itemType.getKeyFields()) {
            keyPaths[i++] = keyField.getPath();
        }
        return CommonUtil.extractIdWithDots(keyPaths, item.getIds());
    }

    @Override
    public Map<ItemBean, FKIntegrityResult> checkFKIntegrity(List<ItemBean> selectedItems) throws ServiceException {
        try {
            Map<ItemBean, FKIntegrityResult> itemBeanToResult = new HashMap<ItemBean, FKIntegrityResult>(selectedItems.size());
            Server server = ServerContext.INSTANCE.get();
            MetadataRepository repository = server.getMetadataRepositoryAdmin().get(getCurrentDataCluster());
            for (ItemBean selectedItem : selectedItems) {
                String concept = selectedItem.getConcept();
                String[] ids = getItemId(repository, selectedItem, concept);
                WSItemPK wsItemPK = new WSItemPK(new WSDataClusterPK(getCurrentDataCluster()), concept, ids);
                WSDeleteItem deleteItem = new WSDeleteItem(wsItemPK, false);
                FKIntegrityCheckResult result = CommonUtil.getPort().checkFKIntegrity(deleteItem);
                switch (result) {
                case FORBIDDEN:
                    itemBeanToResult.put(selectedItem, FKIntegrityResult.FORBIDDEN);
                    break;
                case FORBIDDEN_OVERRIDE_ALLOWED:
                    itemBeanToResult.put(selectedItem, FKIntegrityResult.FORBIDDEN_OVERRIDE_ALLOWED);
                    break;
                case ALLOWED:
                    itemBeanToResult.put(selectedItem, FKIntegrityResult.ALLOWED);
                    break;
                default:
                    throw new ServiceException(MESSAGES.getMessage("fk_integrity", result)); //$NON-NLS-1$
                }
            }
            return itemBeanToResult;
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(e.getMessage(), e.getArgs()));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(BasePagingLoadConfigImpl config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value, String language) throws ServiceException {
        try {
            String foreignKeyConcept = model.getForeignkey().split("/")[0]; //$NON-NLS-1$
            return ForeignKeyHelper.getForeignKeyList(config, model, getEntityModel(foreignKeyConcept, language), dataClusterPK,
                    ifFKFilter, value);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws ServiceException {
        try {
            String fkEntityType;
            ReusableType entityReusableType = null;
            List<Restriction> ret = new ArrayList<Restriction>();
            if (xpathForeignKey != null && xpathForeignKey.length() > 0) {
                if (xpathForeignKey.startsWith("/")) { //$NON-NLS-1$
                    xpathForeignKey = xpathForeignKey.substring(1);
                }
                String fkEntity;
                if (xpathForeignKey.contains("/")) {//$NON-NLS-1$
                    fkEntity = xpathForeignKey.substring(0, xpathForeignKey.indexOf("/"));//$NON-NLS-1$
                } else {
                    fkEntity = xpathForeignKey;
                }
                fkEntityType = SchemaWebAgent.getInstance().getBusinessConcept(fkEntity).getCorrespondTypeName();
                if (fkEntityType != null) {
                    entityReusableType = SchemaWebAgent.getInstance().getReusableType(fkEntityType);
                }
                if (entityReusableType != null) {
                    entityReusableType.load();
                }
                List<ReusableType> subtypes = SchemaWebAgent.getInstance().getMySubtypes(fkEntityType, true);
                if (fkEntityType != null && entityReusableType != null && !entityReusableType.isAbstract()) {
                    subtypes.add(0, entityReusableType);
                }
                List<BusinessConcept> list = SchemaWebAgent.getInstance().getAllBusinessConcepts();
                LinkedHashMap<String, String> businessConceptMap = new LinkedHashMap<String, String>();
                if (list != null) {
                    for (BusinessConcept businessConcept : list) {
                        if (businessConcept.getCorrespondTypeName() != null
                                && businessConcept.getCorrespondTypeName().trim().length() > 0) {
                            businessConceptMap.put(businessConcept.getCorrespondTypeName(), businessConcept.getName());
                        }
                    }
                }
                for (ReusableType reusableType : subtypes) {
                    if (businessConceptMap.containsKey(reusableType.getName())) {
                        Restriction re = new Restriction();
                        EntityModel entityModel = getEntityModel(businessConceptMap.get(reusableType.getName()), language);
                        re.setName(entityModel.getConceptLabel(language));
                        re.setValue(entityModel.getConceptName());
                        ret.add(re);
                    }
                }
            }
            return ret;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public ItemBean getItem(ItemBean itemBean, String viewPK, EntityModel entityModel, boolean isStaging, String language)
            throws ServiceException {
        try {
            String dataCluster = getCurrentDataCluster(isStaging);
            String dataModel = getCurrentDataModel();
            String concept = itemBean.getConcept();
            MetadataRepository repository = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin().get(dataModel);
            ComplexTypeMetadata type = repository.getComplexType(concept);
            // get item
            WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(dataCluster);
            String[] ids = getItemId(repository, itemBean, concept);
            WSGetItem wsGetItem = new WSGetItem(new WSItemPK(wsDataClusterPK, itemBean.getConcept(), ids));
            WSItem wsItem = CommonUtil.getPort().getItem(wsGetItem);
            extractUsingTransformerThroughView(viewPK, type, wsItem);
            itemBean.setItemXml(wsItem.getContent());
            itemBean.set("time", wsItem.getInsertionTime()); //$NON-NLS-1$
            if (wsItem.getTaskId() != null && !"".equals(wsItem.getTaskId()) && !"null".equals(wsItem.getTaskId())) { //$NON-NLS-1$ //$NON-NLS-2$
                itemBean.setTaskId(wsItem.getTaskId());
            }
            /*
            SimpleDateFormat sdf;
            String dateFormat = "yyyy-MM-dd"; //$NON-NLS-1$
            String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"; //$NON-NLS-1$
            org.dom4j.Document doc = org.talend.mdm.webapp.base.server.util.XmlUtil.parseText(itemBean.getItemXml());

            Map<String, String[]> formatMap = checkDisplayFormat(entityModel, language);
            Set<String> keySet = formatMap.keySet();
            for (String key : keySet) {
                String[] value = formatMap.get(key);

                TypeModel tm = entityModel.getMetaDataTypes().get(key);
                String xpath = tm.getXpath();
                org.dom4j.Node node = null;
                if (!key.equals(xpath)) {
                    Namespace namespace = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$
                    List<?> nodeList = doc.selectNodes(xpath);
                    if (nodeList != null && nodeList.size() > 0) {
                        for (Object item : nodeList) {
                            org.dom4j.Element current = (org.dom4j.Element) item;
                            String realType = current.getParent().attributeValue(new QName("type", namespace, "xsi:type")); //$NON-NLS-1$ //$NON-NLS-2$
                            if (key.replaceAll(":" + realType, "").equals(xpath)) { //$NON-NLS-1$//$NON-NLS-2$
                                node = current;
                                break;
                            }
                        }
                    }
                } else {
                    node = doc.selectSingleNode(key);
                }

                if (node != null && itemBean.getOriginalMap() != null) {
                    String dataText = node.getText();
                    if (dataText != null) {
                        if (dataText.trim().length() != 0) {
                            if (dateTypeNames.contains(tm.getType().getTypeName())) {
                                if (value[1].equalsIgnoreCase("DATE")) { //$NON-NLS-1$
                                    sdf = new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH);
                                } else if (value[1].equalsIgnoreCase("DATETIME")) { //$NON-NLS-1$
                                    sdf = new SimpleDateFormat(dateTimeFormat, java.util.Locale.ENGLISH);
                                } else {
                                    sdf = new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH);
                                }
                                try {
                                    Date date = sdf.parse(dataText);
                                    itemBean.getOriginalMap().put(key, date);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    String formatValue = sdf.format(date);
                                    itemBean.getFormateMap().put(key, formatValue);
                                } catch (Exception e) {
                                    itemBean.getOriginalMap().remove(key);
                                    itemBean.getFormateMap().remove(key);
                                }
                            } else if (numberTypeNames.contains(tm.getType().getTypeName())) {
                                try {
                                    NumberFormat nf = NumberFormat.getInstance();
                                    Number num = nf.parse(dataText.trim());
                                    itemBean.getOriginalMap().put(key, num);
                                    String formatValue = String.format(value[0], num);
                                    itemBean.getFormateMap().put(key, formatValue);
                                } catch (Exception e) {
                                    itemBean.getOriginalMap().remove(key);
                                    itemBean.getFormateMap().remove(key);
                                }
                            }
                        }
                    }
                }
            }*/
            // dynamic Assemble
            dynamicAssemble(itemBean, type, language);
            return itemBean;
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(new Locale(language), e.getMessage(), e.getArgs()));
        } catch (Exception exception) {
            String errorMessage;
            if (WebCoreException.class.isInstance(exception.getCause())) {
                WebCoreException webCoreException = (WebCoreException) exception.getCause();
                errorMessage = getErrorMessageFromWebCoreException(webCoreException, itemBean.getConcept(), itemBean.getIds(),
                        new Locale(language));
            } else {
                errorMessage = exception.getLocalizedMessage();
            }
            LOG.error(errorMessage, exception);
            throw new ServiceException(errorMessage);
        }
    }

    protected void dynamicAssemble(final ItemBean itemBean, ComplexTypeMetadata type, final String language) throws Exception {
        if (itemBean.getItemXml() != null) {
            Document docXml;
            synchronized (documentBuilder) {
                docXml = documentBuilder.parse(new InputSource(new StringReader(itemBean.getItemXml())));
            }
            final DOMDocument domDocument = new DOMDocument(docXml, type, null, getCurrentDataCluster(), getCurrentDataModel());
            type.accept(new DefaultMetadataVisitor<Void>() {
                @Override
                public Void visit(SimpleTypeFieldMetadata simpleField) {
                    String path = simpleField.getPath();
                    Accessor accessor = domDocument.createAccessor(path);
                    if (!accessor.exist()) {
                        itemBean.set(path, StringUtils.EMPTY);
                    } else {
                        if (simpleField.isMany()) {
                            List<Serializable> list = new ArrayList<Serializable>();
                            int size = accessor.size();
                            for (int i = 0; i < size; i++) {
                                Accessor occurrenceAccessor = domDocument.createAccessor(path + '[' + i + ']');
                                if (occurrenceAccessor.exist()) {
                                    list.add(occurrenceAccessor.get());
                                }
                            }
                            itemBean.set(path, list);
                        } else {
                            itemBean.set(path, accessor.get());
                        }
                    }
                    return null;
                }

                @Override
                public Void visit(ReferenceFieldMetadata referenceField) {
                    String path = referenceField.getPath();
                    Accessor accessor = domDocument.createAccessor(path);
                    if (!accessor.exist()) {
                        itemBean.set(path, StringUtils.EMPTY);
                    } else {
                        String modelType = accessor.getActualType();
                        String value = accessor.get();
                        itemBean.set(path, path + '-' + value);
                        if (!referenceField.getForeignKeyInfoFields().isEmpty()) {
                            try {
                                itemBean.setForeignkeyDesc(path + '-' + value,
                                        org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyDesc(
                                                null, // TODO
                                                value, false, modelType,
                                                getEntityModel(referenceField.getReferencedType().getName(), language),
                                                isStaging(), language)); //$NON-NLS-1$
                            } catch (Exception e) {
                                throw new RuntimeException("Unable to get foreign key information", e);
                            }
                        }
                    }
                    return null;
                }
            });
            // set primary key information
            List<FieldMetadata> primaryKeyInfoFields = type.getPrimaryKeyInfo();
            List<String> primaryKeyInfoPaths = new LinkedList<String>();
            for (FieldMetadata primaryKeyInfoField : primaryKeyInfoFields) {
                primaryKeyInfoPaths.add(primaryKeyInfoField.getPath());
            }
            itemBean.setPkInfoList(primaryKeyInfoPaths);
            itemBean.setLabel(type.getName(new Locale(language)));
            itemBean.setDisplayPKInfo(org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getPKInfos(primaryKeyInfoPaths));
            // ... and description for entity
            itemBean.setDescription(StringUtils.EMPTY); // TODO
        }
    }

    protected boolean isStaging() throws ServiceException {
        return false;
    }

    public void dynamicAssembleByResultOrder(ItemBean itemBean, ViewBean viewBean, EntityModel entityModel,
            Map<String, EntityModel> map, String language) throws Exception {
        List<String> viewableXPaths = new ArrayList<String>(viewBean.getViewableXpaths());
        org.talend.mdm.webapp.browserecords.server.util.CommonUtil.dynamicAssembleByResultOrder(itemBean, viewableXPaths,
                entityModel, map, language, isStaging());
    }

    @Override
    public EntityModel getEntityModel(String concept, String language) throws ServiceException {
        try {
            // bind entity model
            String model = getCurrentDataModel();
            EntityModel entityModel = new EntityModel();
            DataModelHelper.parseSchema(model, concept, entityModel, RoleHelper.getUserRoles());
            return entityModel;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Locale locale = new Locale(language);
            throw new ServiceException(MESSAGES.getMessage(locale, "parse_model_error")); //$NON-NLS-1$
        }
    }

    @Override
    public ViewBean getView(String viewPk, String language) throws ServiceException {
        String model = getCurrentDataModel();
        String concept = ViewHelper.getConceptFromDefaultViewName(viewPk);
        if (concept != null) {
            try {
                if (Webapp.INSTANCE.isEnterpriseVersion() && !DataModelAccessor.getInstance().checkReadAccess(model, concept)) {
                    Locale locale = new Locale(language);
                    throw new ServiceException(MESSAGES.getMessage(locale, "entity_no_access")); //$NON-NLS-1$
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        WSView wsView;
        ViewBean vb = new ViewBean();
        vb.setViewPK(viewPk);
        try {
            // get WSView
            wsView = CommonUtil.getPort().getView(new WSGetView(new WSViewPK(viewPk)));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Locale locale = new Locale(language);
            throw new ServiceException(MESSAGES.getMessage(locale, "find_view_error", viewPk)); //$NON-NLS-1$
        }
        EntityModel entityModel;
        try {
            // bind entity model
            entityModel = new EntityModel();
            DataModelHelper.parseSchema(model, concept, entityModel, RoleHelper.getUserRoles());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Locale locale = new Locale(language);
            throw new ServiceException(MESSAGES.getMessage(locale, "parse_model_error")); //$NON-NLS-1$
        }
        SimpleTypeModel stagingTaskIdType = new SimpleTypeModel(StagingConstant.STAGING_TASKID, DataTypeConstants.STRING);
        stagingTaskIdType.setXpath(concept + StagingConstant.STAGING_TASKID);
        entityModel.getMetaDataTypes().put(concept + StagingConstant.STAGING_TASKID, stagingTaskIdType);
        vb.setBindingEntityModel(entityModel);
        // viewables
        String[] viewables = ViewHelper.getViewables(wsView);
        // FIXME remove viewableXpath
        if (viewables != null) {
            for (String viewable : viewables) {
                vb.addViewableXpath(viewable);
            }
        }
        vb.setViewables(viewables);
        // searchables
        vb.setSearchables(ViewHelper.getSearchables(wsView, model, language, entityModel));
        // bind layout model
        vb.setColumnLayoutModel(getColumnTreeLayout(concept));
        return vb;
    }

    @Override
    public void logicalDeleteItem(ItemBean item, String path, boolean override) throws ServiceException {
        try {
            String dataClusterPK = getCurrentDataCluster();
            String concept = item.getConcept();
            WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                    new WSGetBusinessConceptKey(new WSDataModelPK(getCurrentDataModel()), concept));
            String[] ids = CommonUtil.extractIdWithDots(key.getFields(), item.getIds());
            WSItemPK wsItemPK = new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids);
            CommonUtil.getPort().dropItem(new WSDropItem(wsItemPK, path, override)); // TODO Adapt code
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(e.getMessage(), e.getArgs()));
        } catch (Exception exception) {
            String errorMessage;
            if (WebCoreException.class.isInstance(exception.getCause())) {
                errorMessage = getErrorMessageFromWebCoreException(((WebCoreException) exception.getCause()), item.getConcept(),
                        item.getIds(), null);
            } else {
                errorMessage = exception.getMessage();
            }
            LOG.error(errorMessage, exception);
            throw new ServiceException(errorMessage);
        }
    }

    @Override
    public void logicalDeleteItems(List<ItemBean> items, String path, boolean override) throws ServiceException {
        for (ItemBean item : items) {
            logicalDeleteItem(item, path, override);
        }
    }

    @Override
    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config, String language) throws ServiceException {
        try {
            RecordsPagingConfig pagingLoad = config.getPagingLoadConfig();
            String sortDir = null;
            if (SortDir.ASC.equals(SortDir.findDir(pagingLoad.getSortDir()))) {
                sortDir = ItemHelper.SEARCH_DIRECTION_ASC;
            }
            if (SortDir.DESC.equals(SortDir.findDir(pagingLoad.getSortDir()))) {
                sortDir = ItemHelper.SEARCH_DIRECTION_DESC;
            }
            Map<String, TypeModel> types = config.getModel().getMetaDataTypes();
            TypeModel typeModel = types.get(pagingLoad.getSortField());

            if (typeModel != null) {
                if (DataTypeConstants.INTEGER.getTypeName().equals(typeModel.getType().getBaseTypeName())
                        || DataTypeConstants.INT.getTypeName().equals(typeModel.getType().getBaseTypeName())
                        || DataTypeConstants.LONG.getTypeName().equals(typeModel.getType().getBaseTypeName())
                        || DataTypeConstants.DECIMAL.getTypeName().equals(typeModel.getType().getBaseTypeName())
                        || DataTypeConstants.FLOAT.getTypeName().equals(typeModel.getType().getBaseTypeName())
                        || DataTypeConstants.DOUBLE.getTypeName().equals(typeModel.getType().getBaseTypeName())) {
                    sortDir = "NUMBER:" + sortDir; //$NON-NLS-1$
                }
            }
            Object[] result = getItemBeans(config.getDataClusterPK(), config.getView(), config.getModel(), config.getCriteria(),
                    pagingLoad.getOffset(), pagingLoad.getLimit(), sortDir, pagingLoad.getSortField(), config.getLanguage());
            int totalSize = (Integer) result[1];
            // if total < offset, total is exact value and navigate to real last page , recalculate offset value
            if (totalSize < pagingLoad.getOffset()) {
                int remainder = totalSize % pagingLoad.getLimit();
                pagingLoad.setOffset(remainder == 0 ? totalSize - pagingLoad.getLimit() : totalSize - remainder);
                result = getItemBeans(config.getDataClusterPK(), config.getView(), config.getModel(), config.getCriteria(),
                        pagingLoad.getOffset(), pagingLoad.getLimit(), sortDir, pagingLoad.getSortField(), config.getLanguage());
            }
            @SuppressWarnings("unchecked")
            List<ItemBean> itemBeans = (List<ItemBean>) result[0];
            boolean isPagingAccurate = CommonUtil.getPort().isPagingAccurate(new WSInt(totalSize)).is_true();
            return new ItemBasePageLoadResult<ItemBean>(itemBeans, pagingLoad.getOffset(), totalSize, isPagingAccurate);
        } catch (Exception exception) {
            Throwable cause = exception.getCause();
            if (cause != null && FieldNotFoundException.class.isInstance(cause.getCause())) {
                throw new ServiceException(cause.getCause().getLocalizedMessage());
            }
            String errorMessage;
            if (WebCoreException.class.isInstance(exception.getCause())) {
                WebCoreException webCoreException = (WebCoreException) exception.getCause();
                errorMessage = getErrorMessageFromWebCoreException(webCoreException, "", null, new Locale(language)); //$NON-NLS-1$
            } else {
                errorMessage = exception.getLocalizedMessage();
            }
            LOG.error(exception.getMessage(), exception);
            throw new ServiceException(errorMessage);
        }
    }

    @Override
    public ItemBean queryItemBeanById(String dataClusterPK, ViewBean viewBean, EntityModel entityModel, String ids,
            String language) throws ServiceException {
        try {
            String[] idArr = StringUtils.splitPreserveAllTokens(ids, '.'); // String.split() omits the last '' if ends
            // with delimiter
            String criteria = CommonUtil.buildCriteriaByIds(entityModel.getKeys(), idArr);
            Object[] result = getItemBeans(dataClusterPK, viewBean, entityModel, criteria, -1, 20,
                    ItemHelper.SEARCH_DIRECTION_ASC, null, language);
            @SuppressWarnings("unchecked")
            List<ItemBean> itemBeans = (List<ItemBean>) result[0];
            if (itemBeans.size() > 0) {
                return itemBeans.get(0);
            } else {
                return null;
            }
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(new Locale(language), e.getMessage(), e.getArgs()));
        } catch (Exception exception) {
            String errorMessage;
            if (WebCoreException.class.isInstance(exception.getCause())) {
                WebCoreException webCoreException = (WebCoreException) exception.getCause();
                errorMessage = getErrorMessageFromWebCoreException(webCoreException, "", null, new Locale(language)); //$NON-NLS-1$
            } else {
                errorMessage = exception.getLocalizedMessage();
            }
            LOG.error(exception.getMessage(), exception);
            throw new ServiceException(errorMessage);
        }
    }

    private Object[] getItemBeans(String dataClusterPK, ViewBean viewBean, EntityModel entityModel, String criteria, int skip,
            int max, String sortDir, String sortCol, String language) throws Exception {
        int totalSize = 0;
        String dateFormat = "yyyy-MM-dd"; //$NON-NLS-1$
        String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"; //$NON-NLS-1$

        List<ItemBean> itemBeans = new ArrayList<ItemBean>();
        String concept = ViewHelper.getConceptFromDefaultViewName(viewBean.getViewPK());
        Map<String, String[]> formatMap = this.checkDisplayFormat(entityModel, language);

        WSWhereItem wi = null;
        if (criteria != null) {
            wi = CommonUtil.buildWhereItems(criteria);
        }
        String[] results = CommonUtil
                .getPort()
                .viewSearch(
                        new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewBean.getViewPK()), wi, -1, skip,
                                max, sortCol, sortDir)).getStrings();
        // set foreignKey's EntityModel
        Map<String, EntityModel> map = new HashMap<String, EntityModel>();
        if (results.length > 0 && viewBean.getViewableXpaths() != null) {
            for (String xpath : viewBean.getViewableXpaths()) {
                TypeModel typeModel = entityModel.getMetaDataTypes().get(xpath);
                if (typeModel != null && typeModel.getForeignkey() != null) {
                    map.put(xpath, getEntityModel(typeModel.getForeignkey().split("/")[0], language)); //$NON-NLS-1$
                }
            }
        }
        // TODO change ids to array?
        List<String> idsArray = new ArrayList<String>();
        for (int i = 0; i < results.length; i++) {

            if (i == 0) {
                try {
                    // Qizx doesn't wrap the count in a XML element, so try to parse it
                    totalSize = Integer.parseInt(results[i]);
                } catch (NumberFormatException e) {
                    totalSize = Integer.parseInt(com.amalto.webapp.core.util.Util.parse(results[i]).getDocumentElement()
                            .getTextContent());
                }
                continue;
            }
            Document doc = parseResultDocument(results[i], "result"); //$NON-NLS-1$
            idsArray.clear();
            for (String key : entityModel.getKeys()) {
                String id = Util.getFirstTextNode(doc.getDocumentElement(), "." + key.substring(key.lastIndexOf('/'))); //$NON-NLS-1$
                if (id != null) {
                    idsArray.add(id);
                }
            }
            Set<String> keySet = formatMap.keySet();
            Map<String, Object> originalMap = new HashMap<String, Object>();
            Map<String, String> formatedValueMap = new HashMap<String, String>();
            for (String key : keySet) {
                String[] value = formatMap.get(key);
                TypeModel tm = entityModel.getMetaDataTypes().get(key);
                String xpath = tm.getXpath();
                String dataText = null;
                if (!key.equals(xpath)) {
                    NodeList list = Util.getNodeList(doc.getDocumentElement(), xpath.replaceFirst(concept + "/", "./")); //$NON-NLS-1$//$NON-NLS-2$
                    if (list != null) {
                        for (int k = 0; k < list.getLength(); k++) {
                            Node node = list.item(k);
                            String realType = ((Element) node.getParentNode()).getAttribute("xsi:type"); //$NON-NLS-1$
                            if (key.replaceAll(":" + realType, "").equals(xpath)) { //$NON-NLS-1$//$NON-NLS-2$
                                dataText = node.getTextContent();
                                break;
                            }
                        }
                    }
                } else {
                    dataText = Util.getFirstTextNode(doc.getDocumentElement(), key.replaceFirst(concept + "/", "./")); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (dataText != null) {
                    if (dataText.trim().length() != 0) {
                        if (dateTypeNames.contains(tm.getType().getTypeName())) {
                            SimpleDateFormat sdf = null;
                            if (value[1].equalsIgnoreCase("DATE")) { //$NON-NLS-1$
                                sdf = new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH);
                            } else if (value[1].equalsIgnoreCase("DATETIME")) { //$NON-NLS-1$
                                sdf = new SimpleDateFormat(dateTimeFormat, java.util.Locale.ENGLISH);
                            }

                            try {
                                Date date = sdf.parse(dataText.trim());
                                originalMap.put(key, date);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                String formatValue = com.amalto.webapp.core.util.Util.formatDate(value[0], calendar);
                                formatedValueMap.put(key, formatValue);
                                Util.getNodeList(doc.getDocumentElement(), key.replaceFirst(concept + "/", "./")).item(0).setTextContent(formatValue); //$NON-NLS-1$ //$NON-NLS-2$
                            } catch (Exception e) {
                                originalMap.remove(key);
                                formatedValueMap.remove(key);
                            }
                        } else if (numberTypeNames.contains(tm.getType().getTypeName())) {
                            try {
                                NumberFormat nf = NumberFormat.getInstance();
                                Number num = nf.parse(dataText.trim());
                                originalMap.put(key, num);
                                String formatValue = String.format(value[0], num);
                                formatedValueMap.put(key, formatValue);
                                Util.getNodeList(doc.getDocumentElement(), key.replaceFirst(concept + "/", "./")).item(0).setTextContent(formatValue); //$NON-NLS-1$ //$NON-NLS-2$
                            } catch (Exception e) {
                                originalMap.remove(key);
                                formatedValueMap.remove(key);
                            }
                        }
                    }
                }
            }
            ItemBean itemBean = new ItemBean(concept,
                    CommonUtil.joinStrings(idsArray, "."), Util.nodeToString(doc.getDocumentElement()));//$NON-NLS-1$
            itemBean.setOriginalMap(originalMap);
            itemBean.setFormateMap(formatedValueMap);
            if (checkSmartViewExistsByLang(concept, language)) {
                itemBean.setSmartViewMode(ItemBean.SMARTMODE);
            } else if (checkSmartViewExistsByOpt(concept, language)) {
                itemBean.setSmartViewMode(ItemBean.PERSOMODE);
            }
            dynamicAssembleByResultOrder(itemBean, viewBean, entityModel, map, language);
            itemBeans.add(itemBean);
        }
        return new Object[] { itemBeans, totalSize };
    }

    private Map<String, String[]> checkDisplayFormat(EntityModel entityModel, String language) {
        Map<String, TypeModel> metaData = entityModel.getMetaDataTypes();
        Map<String, String[]> formatMap = new HashMap<String, String[]>();
        String languageStr = "format_" + language.toLowerCase(); //$NON-NLS-1$
        if (metaData == null) {
            return formatMap;
        }

        Set<String> keySet = metaData.keySet();
        for (String key : keySet) {
            TypeModel typeModel = metaData.get(key);
            if (dateTypeNames.contains(typeModel.getType().getTypeName())
                    || numberTypeNames.contains(typeModel.getType().getTypeName())) {
                if (typeModel.getDisplayFomats() != null && typeModel.getDisplayFomats().size() > 0) {
                    if (typeModel.getDisplayFomats().containsKey(languageStr)) {
                        formatMap.put(key, new String[] { typeModel.getDisplayFomats().get(languageStr),
                                typeModel.getType().getTypeName() });
                    }
                }
            }

        }
        return formatMap;
    }

    protected Document parseResultDocument(String result, String expectedRootElement) throws Exception {
        Document doc = Util.parse(result);
        Element rootElement = doc.getDocumentElement();
        if (!rootElement.getNodeName().equals(expectedRootElement)) {
            // When there is a null value in fields, the viewable fields sequence is not enclosed by expected element
            // FIXME Better to find out a solution at the underlying stage
            doc.removeChild(rootElement);
            Element resultElement = doc.createElement(expectedRootElement);
            resultElement.appendChild(rootElement);
        }
        return doc;
    }

    @Override
    public ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws ServiceException {
        try {
            ForeignKeyDrawer fkDrawer = new ForeignKeyDrawer();
            BusinessConcept businessConcept = SchemaWebAgent.getInstance().getFirstBusinessConceptFromRootType(targetEntityType);
            if (businessConcept == null) {
                return null;
            }
            String targetEntity = businessConcept.getName();
            if (xpathForeignKey != null && xpathForeignKey.length() > 0) {
                xpathForeignKey = replaceXpathRoot(targetEntity, xpathForeignKey);
            }
            if (xpathInfoForeignKey != null && xpathInfoForeignKey.length() > 0) {
                String[] fkInfoPaths = xpathInfoForeignKey.split(","); //$NON-NLS-1$
                xpathInfoForeignKey = ""; //$NON-NLS-1$
                for (String fkInfoPath : fkInfoPaths) {
                    String replacedFkInfoPath = replaceXpathRoot(targetEntity, fkInfoPath);
                    if (replacedFkInfoPath != null && replacedFkInfoPath.length() > 0) {
                        if (xpathInfoForeignKey.length() > 0) {
                            xpathInfoForeignKey += ",";//$NON-NLS-1$
                        }
                        xpathInfoForeignKey += replacedFkInfoPath;
                    }
                }
            }
            fkDrawer.setXpathForeignKey(xpathForeignKey);
            fkDrawer.setXpathInfoForeignKey(xpathInfoForeignKey);
            return fkDrawer;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private String replaceXpathRoot(String targetEntity, String xpath) {
        if (xpath.contains("/")) { //$NON-NLS-1$
            xpath = targetEntity + xpath.substring(xpath.indexOf("/"));//$NON-NLS-1$
        } else {
            xpath = targetEntity;
        }
        return xpath;
    }

    @Override
    public String getCriteriaByBookmark(String bookmark) throws ServiceException {
        try {
            String criteria = "";//$NON-NLS-1$
            String result = CommonUtil
                    .getPort()
                    .getItem(
                            new WSGetItem(new WSItemPK(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()),
                                    "BrowseItem",//$NON-NLS-1$
                                    new String[] { bookmark }))).getContent().trim();
            if (result.contains("<SearchCriteria>")) { //$NON-NLS-1$
                criteria = result.substring(result.indexOf("<SearchCriteria>") + 16, result.indexOf("</SearchCriteria>"));//$NON-NLS-1$ //$NON-NLS-2$
                if (criteria.contains("&amp;")) { //$NON-NLS-1$
                    criteria = criteria.replace("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            return criteria;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<ItemBaseModel> getUserCriterias(String view) throws ServiceException {
        try {
            String[] results = getSearchTemplateNames(view, false, 0, 0);
            List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();

            for (String result : results) {
                ItemBaseModel bm = new ItemBaseModel();

                org.w3c.dom.Node resultNode = com.amalto.webapp.core.util.Util.parse(result).getFirstChild();
                for (int i = 0; i < resultNode.getChildNodes().getLength(); i++) {
                    if (resultNode.getChildNodes().item(i) instanceof org.w3c.dom.Element) {
                        if (resultNode.getChildNodes().item(i).getNodeName().equals("CriteriaName")) { //$NON-NLS-1$
                            bm.set("name", resultNode.getChildNodes().item(i).getFirstChild().getTextContent());//$NON-NLS-1$
                            bm.set("value", resultNode.getChildNodes().item(i).getFirstChild().getTextContent());//$NON-NLS-1$
                        } else if (resultNode.getChildNodes().item(i).getNodeName().equals("Shared")) { //$NON-NLS-1$
                            bm.set("shared", resultNode.getChildNodes().item(i).getFirstChild().getTextContent()); //$NON-NLS-1$
                        }
                    }
                }
                list.add(bm);

            }
            return list;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private String[] getSearchTemplateNames(String view, boolean isShared, int start, int limit) throws Exception {
        int localStart;
        int localLimit;
        if (start == limit && limit == 0) {
            localStart = 0;
            localLimit = Integer.MAX_VALUE;
        } else {
            localStart = start;
            localLimit = limit;

        }
        WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,//$NON-NLS-1$
                WSStringPredicate.NONE, false);
        WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,//$NON-NLS-1$
                RoleHelper.getCurrentUserName(), WSStringPredicate.OR, false);
        WSWhereCondition wc4;
        WSWhereOr or;
        if (isShared) {
            wc4 = new WSWhereCondition("BrowseItem/Shared", WSWhereOperator.EQUALS, "true", WSStringPredicate.NONE, false);//$NON-NLS-1$ //$NON-NLS-2$

            or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null), new WSWhereItem(wc4, null, null) });
        } else {
            or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });
        }
        WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null), new WSWhereItem(null, null, or) });
        WSWhereItem wi = new WSWhereItem(null, and, null);
        return CommonUtil
                .getPort()
                .xPathsSearch(
                        new WSXPathsSearch(
                                new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()),
                                null,// pivot
                                new WSStringArray(new String[] { "BrowseItem/CriteriaName", "BrowseItem/Shared" }), wi, -1, localStart, localLimit, null, // order //$NON-NLS-1$ //$NON-NLS-2$
                                // by
                                null, // direction
                                false)).getStrings();
    }

    @Override
    public List<ItemBaseModel> getViewsList(String language) throws ServiceException {
        try {
            String model = getCurrentDataModel();
            String[] businessConcept = CommonUtil.getPort()
                    .getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
            ArrayList<String> bc = new ArrayList<String>();
            Collections.addAll(bc, businessConcept);
            WSViewPK[] wsViewsPK = CommonUtil.getPort()
                    .getViewPKs(new WSGetViewPKs(ViewHelper.DEFAULT_VIEW_PREFIX + ".*")).getWsViewPK();//$NON-NLS-1$

            // Filter view list according to current datamodel
            TreeMap<String, String> views = new TreeMap<String, String>();
            for (WSViewPK aWsViewsPK : wsViewsPK) {
                WSView wsview = CommonUtil.getPort().getView(new WSGetView(aWsViewsPK));// FIXME: Do we need get each
                // view entity here?
                String concept = ViewHelper.getConceptFromDefaultViewName(wsview.getName());
                if (bc.contains(concept)) {
                    String viewDesc = ViewHelper.getViewLabel(language, wsview);
                    views.put(wsview.getName(), viewDesc);
                }
            }
            Map<String, String> viewMap = getMapSortedByValue(views);

            Util.filterAuthViews(viewMap);

            List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
            for (String key : viewMap.keySet()) {
                ItemBaseModel bm = new ItemBaseModel();
                bm.set("name", viewMap.get(key));//$NON-NLS-1$
                bm.set("value", key);//$NON-NLS-1$
                list.add(bm);
            }
            return list;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private static Map<String, String> getMapSortedByValue(Map<String, String> map) {
        TreeSet<Map.Entry<String, String>> set = new TreeSet<Map.Entry<String, String>>(
                new Comparator<Map.Entry<String, String>>() {

                    @Override
                    public int compare(Map.Entry<String, String> obj1, Map.Entry<String, String> obj2) {
                        String obj1Value = obj1.getValue();
                        if (obj1Value != null) {
                            return obj1Value.compareTo(obj2.getValue());
                        } else { // obj1Value == null
                            return obj2.getValue() == null ? 0 : 1;
                        }
                    }
                });
        set.addAll(map.entrySet());
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : set) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    public AppHeader getAppHeader() throws ServiceException {
        try {
            AppHeader header = new AppHeader();
            header.setDatacluster(getCurrentDataCluster());
            header.setMasterDataCluster(getCurrentDataCluster(false));
            header.setStagingDataCluster(getCurrentDataCluster(true));
            header.setDatamodel(getCurrentDataModel());
            header.setStandAloneMode(BaseConfiguration.isStandalone());
            header.setAutoTextAreaLength(BrowseRecordsConfiguration.getAutoTextAreaLength());
            header.setAutoValidate(BrowseRecordsConfiguration.isAutoValidate());
            header.setDataMigrationMultiLingualFieldAuto(BrowseRecordsConfiguration.dataMigrationMultiLingualFieldAuto());
            header.setUseRelations(BrowseRecordsConfiguration.IsUseRelations());
            header.setEnterprise(Webapp.INSTANCE.isEnterpriseVersion());
            return header;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean isExistCriteria(String dataObjectLabel, String id) throws ServiceException {
        try {
            WSItemPK wsItemPK = new WSItemPK();
            wsItemPK.setConceptName("BrowseItem");//$NON-NLS-1$

            WSDataClusterPK wsDataClusterPK = new WSDataClusterPK();
            wsDataClusterPK.setPk(XSystemObjects.DC_SEARCHTEMPLATE.getName());
            wsItemPK.setWsDataClusterPK(wsDataClusterPK);

            String[] ids = new String[1];
            ids[0] = id;
            wsItemPK.setIds(ids);

            WSExistsItem wsExistsItem = new WSExistsItem(wsItemPK);
            WSBoolean wsBoolean = CommonUtil.getPort().existsItem(wsExistsItem);
            return wsBoolean.is_true();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public void saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString) throws ServiceException {
        try {
            String owner = com.amalto.webapp.core.util.Util.getLoginUserName();
            SearchTemplate searchTemplate = new SearchTemplate();
            searchTemplate.setViewPK(viewPK);
            searchTemplate.setCriteriaName(templateName);
            searchTemplate.setShared(isShared);
            searchTemplate.setOwner(owner);
            searchTemplate.setCriteria(criteriaString);

            WSItemPK pk = CommonUtil.getPort().putItem(
                    new WSPutItem(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), searchTemplate
                            .marshal2String(), new WSDataModelPK(XSystemObjects.DM_SEARCHTEMPLATE.getName()), false));

            if (pk == null) {
                throw new ServiceException();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public ItemBasePageLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, BasePagingLoadConfigImpl load)
            throws ServiceException {
        try {
            List<String> results = Arrays.asList(getSearchTemplateNames(view, isShared, load.getOffset(), load.getLimit()));
            List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
            for (String result : results) {
                ItemBaseModel bm = new ItemBaseModel();
                org.w3c.dom.Node resultNode = com.amalto.webapp.core.util.Util.parse(result).getFirstChild();
                for (int i = 0; i < resultNode.getChildNodes().getLength(); i++) {
                    if (resultNode.getChildNodes().item(i) instanceof org.w3c.dom.Element) {
                        if (resultNode.getChildNodes().item(i).getNodeName().equals("CriteriaName")) { //$NON-NLS-1$
                            bm.set("name", resultNode.getChildNodes().item(i).getFirstChild().getTextContent());//$NON-NLS-1$
                            bm.set("value", resultNode.getChildNodes().item(i).getFirstChild().getTextContent());//$NON-NLS-1$
                        } else if (resultNode.getChildNodes().item(i).getNodeName().equals("Shared")) { //$NON-NLS-1$
                            bm.set("shared", resultNode.getChildNodes().item(i).getFirstChild().getTextContent()); //$NON-NLS-1$
                        }
                    }
                }
                list.add(bm);
            }
            int totalSize = results.size();
            return new ItemBasePageLoadResult<ItemBaseModel>(list, load.getOffset(), totalSize);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public void deleteSearchTemplate(String id) throws ServiceException {
        try {
            String[] ids = { id };
            String concept = "BrowseItem";//$NON-NLS-1$
            String dataClusterPK = XSystemObjects.DC_SEARCHTEMPLATE.getName();
            if (ids != null) {
                WSItemPK wsItem = CommonUtil.getPort().deleteItem(
                        new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids), false));

                if (wsItem == null) {
                    throw new ServiceException(MESSAGES.getMessage("label_error_delete_template_null")); //$NON-NLS-1$
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public String getCurrentDataModel() throws ServiceException {
        try {
            return org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getCurrentDataModel();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public String getCurrentDataCluster() throws ServiceException {
        return getCurrentDataCluster(false);
    }

    @Override
    public String getCurrentDataCluster(boolean isStaging) throws ServiceException {
        try {
            return org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getCurrentDataCluster(isStaging);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public Map<ViewBean, Map<String, List<String>>> getForeignKeyValues(String concept, String[] ids, String language)
            throws ServiceException {
        try {
            Map<ViewBean, Map<String, List<String>>> map = new HashMap<ViewBean, Map<String, List<String>>>();
            // 1. getView
            ViewBean viewBean = getView("Browse_items_" + concept, language);//$NON-NLS-1$
            Map<String, List<String>> fkValues = new HashMap<String, List<String>>();
            // 2. getItem
            WSItem wsItem = CommonUtil.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(this.getCurrentDataCluster()), concept, ids)));
            org.dom4j.Document doc = org.talend.mdm.webapp.base.server.util.XmlUtil.parseText(wsItem.getContent());
            EntityModel entityModel = viewBean.getBindingEntityModel();
            Map<String, TypeModel> metaData = entityModel.getMetaDataTypes();
            // 3. getAllFKValues
            for (String key : metaData.keySet()) {
                TypeModel typeModel = metaData.get(key);
                if (typeModel.getForeignkey() != null && typeModel.getForeignkey().trim().length() > 0) {
                    fkValues.put(typeModel.getXpath(), new ArrayList<String>());
                    List<?> nodeList = doc.selectNodes(typeModel.getXpath());
                    if (nodeList != null && nodeList.size() > 0) {
                        for (Object item : nodeList) {
                            org.dom4j.Element current = (org.dom4j.Element) item;
                            fkValues.get(typeModel.getXpath()).add(current.getText());
                        }
                    }
                }
            }
            // 4. construct map
            map.put(viewBean, fkValues);
            return map;
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public ItemNodeModel getItemNodeModel(ItemBean item, EntityModel entity, boolean isStaging, String language)
            throws ServiceException {
        try {
            if (item.get("isRefresh") != null && (!"".equals(item.getIds()) && item.getIds() != null)) { //$NON-NLS-1$ //$NON-NLS-2$ 
                item = getItem(item, "Browse_items_" + item.getConcept(), entity, isStaging, language); // itemBean need to be get from server when refresh tree. //$NON-NLS-1$
            }
            String xml = item.getItemXml();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource inputSource = new InputSource(sr);
            Document doc = builder.parse(inputSource);
            Element root = doc.getDocumentElement();

            Map<String, TypeModel> metaDataTypes = entity.getMetaDataTypes();
            Map<String, Integer> multiNodeIndex = new HashMap<String, Integer>();
            StringBuffer foreignKeyDeleteMessage = new StringBuffer();
            ItemNodeModel itemModel = builderNode(multiNodeIndex, root, entity,
                    "", "", true, foreignKeyDeleteMessage, false, isStaging, language); //$NON-NLS-1$ //$NON-NLS-2$
            DynamicLabelUtil.getDynamicLabel(XmlUtil.parseDocument(doc), "", itemModel, metaDataTypes, language); //$NON-NLS-1$
            itemModel.set("time", item.get("time")); //$NON-NLS-1$ //$NON-NLS-2$
            itemModel.set("foreignKeyDeleteMessage", foreignKeyDeleteMessage.toString()); //$NON-NLS-1$
            return itemModel;
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public ItemNodeModel createDefaultItemNodeModel(ViewBean viewBean, Map<String, List<String>> initDataMap, String language)
            throws ServiceException {
        String concept = viewBean.getBindingEntityModel().getConceptName();

        EntityModel entity = viewBean.getBindingEntityModel();
        Map<String, TypeModel> metaDataTypes = entity.getMetaDataTypes();
        ItemNodeModel itemModel = null;
        try {
            DisplayRuleEngine ruleEngine = new DisplayRuleEngine(metaDataTypes, concept);

            TypeModel typeModel = metaDataTypes.get(concept);
            Document doc = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getSubXML(typeModel, null, initDataMap,
                    language);

            org.dom4j.Document doc4j = XmlUtil.parseDocument(doc);

            ruleEngine.execDefaultValueRule(doc4j);

            if (initDataMap != null) {
                Set<String> paths = initDataMap.keySet();
                for (String path : paths) {
                    List<?> nodeList = doc4j.selectNodes(path);
                    List<String> values = initDataMap.get(path);
                    if (nodeList != null && nodeList.size() > 0 && values != null && values.size() > 0) {
                        for (int i = 0; i < nodeList.size(); i++) {
                            org.dom4j.Element current = (org.dom4j.Element) nodeList.get(i);
                            if (current != null) {
                                current.setText(values.get(i));
                            }
                        }

                    }
                }
            }

            Document resultDoc = org.talend.mdm.webapp.base.server.util.XmlUtil.parseDocument(doc4j);
            Map<String, Integer> multiNodeIndex = new HashMap<String, Integer>();
            StringBuffer foreignKeyDeleteMessage = new StringBuffer();
            Element root = resultDoc.getDocumentElement();
            itemModel = builderNode(multiNodeIndex, root, entity, "", "", false, foreignKeyDeleteMessage, true, false, language); //$NON-NLS-1$ //$NON-NLS-2$
            DynamicLabelUtil.getDynamicLabel(doc4j, "", itemModel, metaDataTypes, language); //$NON-NLS-1$
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        return itemModel;
    }

    @Override
    public ItemNodeModel createSubItemNodeModel(ViewBean viewBean, String xml, String typePath, String contextPath,
            String realType, boolean isStaging, String language) throws ServiceException {
        EntityModel entity = viewBean.getBindingEntityModel();
        String concept = entity.getConceptName();
        Map<String, TypeModel> metaDataTypes = entity.getMetaDataTypes();
        ItemNodeModel itemModel = null;
        try {
            DisplayRuleEngine ruleEngine = new DisplayRuleEngine(metaDataTypes, concept);
            TypeModel typeModel = metaDataTypes.get(typePath);
            org.dom4j.Document mainDoc = DocumentHelper.parseText(xml);

            org.dom4j.Document subDoc = XmlUtil.parseDocument(org.talend.mdm.webapp.browserecords.server.util.CommonUtil
                    .getSubXML(typeModel, realType, null, language));

            org.dom4j.Document doc4j = org.talend.mdm.webapp.base.server.util.XmlUtil.mergeDoc(mainDoc, subDoc, contextPath);

            ruleEngine.execDefaultValueRule(doc4j);

            Document resultDoc = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getSubDoc(doc4j, contextPath);
            Map<String, Integer> multiNodeIndex = new HashMap<String, Integer>();
            StringBuffer foreignKeyDeleteMessage = new StringBuffer();
            Element root = resultDoc.getDocumentElement();
            String baseXpath = contextPath.substring(0, contextPath.lastIndexOf('/'));
            String baseXpathWithInheritance = baseXpath;
            if (typePath.substring(0, typePath.lastIndexOf("/")).contains(":")) { //$NON-NLS-1$ //$NON-NLS-2$
                baseXpathWithInheritance = typePath.substring(0, typePath.lastIndexOf("/")); //$NON-NLS-1$
            }
            itemModel = builderNode(multiNodeIndex, root, entity, baseXpathWithInheritance,
                    "", true, foreignKeyDeleteMessage, true, isStaging, language); //$NON-NLS-1$
            DynamicLabelUtil.getDynamicLabel(doc4j, baseXpath, itemModel, metaDataTypes, language);
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        return itemModel;
    }

    private ItemNodeModel builderNode(Map<String, Integer> multiNodeIndex, Element el, EntityModel entity, String baseXpath,
            String xpath, boolean isPolyType, StringBuffer foreignKeyDeleteMessage, boolean isCreate, boolean isStaging,
            String language) throws Exception {
        Map<String, TypeModel> metaDataTypes = entity.getMetaDataTypes();
        String realType = el.getAttribute("xsi:type"); //$NON-NLS-1$
        if (isPolyType) {
            xpath += ("".equals(xpath) ? el.getNodeName() : "/" + el.getNodeName()); //$NON-NLS-1$//$NON-NLS-2$
            if (realType != null && realType.trim().length() > 0) {
                xpath += ":" + realType; //$NON-NLS-1$
            }
        } else {
            xpath += ("".equals(xpath) ? el.getNodeName() : "/" + el.getNodeName()); //$NON-NLS-1$//$NON-NLS-2$
        }
        String typePath;
        if ("".equals(baseXpath)) { //$NON-NLS-1$
            typePath = xpath.replaceAll("\\[\\d+\\]", ""); //$NON-NLS-1$//$NON-NLS-2$
        } else {
            typePath = (baseXpath + "/" + xpath).replaceAll("\\[\\d+\\]", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        }
        typePath = typePath.replaceAll(":" + realType + "$", ""); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        ItemNodeModel nodeModel = new ItemNodeModel(el.getNodeName());

        TypeModel model = findTypeModelByTypePath(metaDataTypes, typePath, language);
        nodeModel.setTypePath(model.getTypePath());
        nodeModel.setHasVisiblueRule(model.isHasVisibleRule());
        String realXPath = xpath;
        if (isPolyType) {
            realXPath = realXPath.replaceAll(":\\w+", ""); //$NON-NLS-1$//$NON-NLS-2$
        }

        if (model.getMaxOccurs() > 1 || model.getMaxOccurs() == -1) {

            Integer index = multiNodeIndex.get(realXPath);
            if (index == null) {
                nodeModel.setIndex(1);
                multiNodeIndex.put(realXPath, new Integer(1));
            } else {
                nodeModel.setIndex(index + 1);
                multiNodeIndex.put(realXPath, nodeModel.getIndex());
            }
        }

        if (realType != null && realType.trim().length() > 0) {
            nodeModel.setRealType(el.getAttribute("xsi:type")); //$NON-NLS-1$
        }
        nodeModel.setLabel(model.getLabel(language));
        nodeModel.setDescription(model.getDescriptionMap().get(language));
        nodeModel.setName(el.getNodeName());
        if (model.getMinOccurs() == 1 && model.getMaxOccurs() == 1) {
            nodeModel.setMandatory(true);
        }
        String foreignKey = findTypeModelByTypePath(metaDataTypes, typePath, language).getForeignkey();
        if (foreignKey != null && foreignKey.trim().length() > 0) {
            // set foreignKeyBean
            model.setRetrieveFKinfos(true);
            String modelType = el.getAttribute("tmdm:type"); //$NON-NLS-1$
            if (modelType != null && modelType.trim().length() > 0) {
                nodeModel.setTypeName(modelType);
            }
            ForeignKeyBean fkBean = org.talend.mdm.webapp.browserecords.server.util.CommonUtil
                    .getForeignKeyDesc(model, el.getTextContent(), true, modelType,
                            getEntityModel(foreignKey.split("/")[0], language), isStaging, language); //$NON-NLS-1$
            if (fkBean != null) {
                String fkNotFoundMessage = fkBean.get("foreignKeyDeleteMessage"); //$NON-NLS-1$
                if (fkNotFoundMessage != null) {// fix bug TMDM-2757
                    if (foreignKeyDeleteMessage.indexOf(fkNotFoundMessage) == -1) {
                        foreignKeyDeleteMessage.append(fkNotFoundMessage + "\r\n"); //$NON-NLS-1$
                    }
                    return nodeModel;
                }
                nodeModel.setObjectValue(fkBean);
            }
        } else if (model.isSimpleType()) {
            nodeModel.setObjectValue(el.getTextContent());
            org.talend.mdm.webapp.browserecords.server.util.CommonUtil.migrationMultiLingualFieldValue(null, model, el, typePath,
                    false, nodeModel);
        }
        if (isCreate && model.getDefaultValueExpression() != null && model.getDefaultValueExpression().trim().length() > 0) {
            nodeModel.setChangeValue(true);
        }

        NodeList children = el.getChildNodes();
        if (children != null && !model.isSimpleType()) {
            List<TypeModel> childModels = null;
            if (nodeModel.getRealType() != null && nodeModel.getRealType().trim().length() > 0) {
                childModels = ((ComplexTypeModel) model).getRealType(nodeModel.getRealType()).getSubTypes();
            } else {
                if (!model.isAbstract()) {
                    childModels = ((ComplexTypeModel) model).getSubTypes();
                } else {
                    childModels = org.talend.mdm.webapp.browserecords.shared.ReusableType.getDefaultReusableTypeChildren(
                            (ComplexTypeModel) model, nodeModel);
                }
            }
            for (TypeModel typeModel : childModels) { // display tree node according to the studio default configuration
                boolean existNodeFlag = false;
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        String tem_typePath;
                        if (realType != null && realType.trim().length() > 0) {
                            tem_typePath = typePath + ":" + realType + "/" + child.getNodeName(); //$NON-NLS-1$ //$NON-NLS-2$
                        } else {
                            tem_typePath = typePath + "/" + child.getNodeName(); //$NON-NLS-1$
                        }

                        if (typeModel.getTypePath().equals(tem_typePath)
                                || (typeModel.getTypePathObject() != null
                                        && typeModel.getTypePathObject().getAllAliasXpaths() != null && typeModel
                                        .getTypePathObject().getAllAliasXpaths().contains(tem_typePath))) {
                            ItemNodeModel childNode = builderNode(multiNodeIndex, (Element) child, entity, baseXpath, xpath,
                                    isPolyType, foreignKeyDeleteMessage, isCreate, isStaging, language);
                            nodeModel.add(childNode);
                            existNodeFlag = true;
                            if (typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                }
                if (!existNodeFlag) { // add default tree node when the node has not been saved in DB.
                    nodeModel.add(org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getDefaultTreeModel(typeModel,
                            isCreate, language).get(0));
                }
            }

        }
        for (String key : entity.getKeys()) {
            if (key.equals(realXPath)) {
                nodeModel.setKey(true);
            }
        }
        return nodeModel;

    }

    @Override
    public List<String> getMandatoryFieldList(String tableName) throws ServiceException {
        try {
            MetadataRepository repository = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin().get(getCurrentDataModel());
            ComplexTypeMetadata complexType = repository.getComplexType(tableName);
            return complexType.accept(new DefaultMetadataVisitor<List<String>>() {
                List<String> mandatoryFields = new ArrayList<String>();

                @Override
                public List<String> visit(ComplexTypeMetadata complexType) {
                    super.visit(complexType);
                    return mandatoryFields;
                }

                @Override
                public List<String> visit(SimpleTypeFieldMetadata simpleField) {
                    if (simpleField.isMandatory()) {
                        mandatoryFields.add(simpleField.getPath());
                    }
                    return mandatoryFields;
                }

                @Override
                public List<String> visit(ReferenceFieldMetadata referenceField) {
                    if (referenceField.isMandatory()) {
                        mandatoryFields.add(referenceField.getPath());
                    }
                    return mandatoryFields;
                }

                @Override
                public List<String> visit(ContainedTypeFieldMetadata containedField) {
                    if (containedField.isMandatory()) {
                        mandatoryFields.add(containedField.getPath());
                    }
                    return mandatoryFields;
                }

                @Override
                public List<String> visit(EnumerationFieldMetadata enumField) {
                    if (enumField.isMandatory()) {
                        mandatoryFields.add(enumField.getPath());
                    }
                    return mandatoryFields;
                }
            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public String saveItemBean(ItemBean item, String language) throws ServiceException {
        return saveItem(item.getConcept(), item.getIds(), item.getItemXml(), true, language).getDescription();
    }

    @Override
    public ItemResult saveItem(String concept, String ids, String xml, boolean isCreate, String language) throws ServiceException {
        Locale locale = new Locale(language);

        boolean hasBeforeSavingProcess = Util.isTransformerExist("beforeSaving_" + concept); //$NON-NLS-1$

        if (LOG.isDebugEnabled()) {
            LOG.debug("To-Update-Xml: " + xml); //$NON-NLS-1$
        }

        try {
            // TODO (1) if update, check the item is modified by others?
            // TODO (2) if create, check if the item has not been created by someone else?
            WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport(new WSPutItem(new WSDataClusterPK(
                    getCurrentDataCluster()), xml, new WSDataModelPK(getCurrentDataModel()), !isCreate), "genericUI", true); //$NON-NLS-1$
            int status = ItemResult.SUCCESS;
            WSItemPK wsi = CommonUtil.getPort().putItemWithReport(wsPutItemWithReport);
            String message = wsPutItemWithReport.getSource(); // putItemWithReport is expected to put
            // beforeSavingMessage in getSource().

            if (hasBeforeSavingProcess) {
                // No message from beforeSaving process,
                if (message == null || message.length() == 0) {
                    message = MESSAGES.getMessage(locale, "save_process_validation_success"); //$NON-NLS-1$
                }
            } else {
                message = MESSAGES.getMessage(locale, "save_record_success"); //$NON-NLS-1$
            }
            if (wsi == null) {
                return new ItemResult(status, message, ids);
            } else {
                String[] pk = wsi.getIds();
                if (pk == null || pk.length == 0) {
                    WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                            new WSGetBusinessConceptKey(new WSDataModelPK(getCurrentDataModel()), concept));
                    pk = CommonUtil.extractIdWithDots(key.getFields(), ids);
                }
                WSItem wsItem = CommonUtil.getPort().getItem(
                        new WSGetItem(new WSItemPK(new WSDataClusterPK(getCurrentDataCluster()), concept, pk)));
                return new ItemResult(status, message, Util.joinStrings(pk, "."), wsItem.getInsertionTime()); //$NON-NLS-1$
            }
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(new Locale(language), e.getMessage(), e.getArgs()));
        } catch (Exception exception) {
            String errorMessage;
            if (WebCoreException.class.isInstance(exception.getCause())) {
                WebCoreException webCoreException = (WebCoreException) exception.getCause();
                errorMessage = getErrorMessageFromWebCoreException(webCoreException, concept, ids, locale);
                if (webCoreException.isClient()) {
                    throw new ServiceException(errorMessage);
                }
                if (webCoreException.getLevel() == WebCoreException.INFO) {
                    LOG.info(errorMessage);
                } else {
                    LOG.error(errorMessage, exception);
                }
            } else {
                errorMessage = exception.getLocalizedMessage();
                LOG.error(errorMessage, exception);
            }
            return new ItemResult(ItemResult.FAILURE, errorMessage);
        }
    }

    @Override
    public ItemResult saveItem(ViewBean viewBean, String ids, String xml, boolean isCreate, String language)
            throws ServiceException {
        EntityModel entityModel = viewBean.getBindingEntityModel();
        String concept = entityModel.getConceptName();
        return saveItem(concept, ids, xml, isCreate, language);
    }

    @Override
    public ItemResult updateItem(String concept, String ids, Map<String, String> changedNodes, String xml, String language)
            throws ServiceException {
        try {
            org.dom4j.Document doc;
            if (xml == null || xml.trim().length() == 0) {
                String dataCluster = getCurrentDataCluster();
                // get item
                WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(dataCluster);
                WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                        new WSGetBusinessConceptKey(new WSDataModelPK(getCurrentDataModel()), concept));
                String[] idArray = CommonUtil.extractIdWithDots(key.getFields(), ids);

                WSItem wsItem = CommonUtil.getPort().getItem(new WSGetItem(new WSItemPK(wsDataClusterPK, concept, idArray)));
                doc = org.talend.mdm.webapp.base.server.util.XmlUtil.parseText(wsItem.getContent());
            } else {
                doc = org.talend.mdm.webapp.base.server.util.XmlUtil.parseText(xml);
            }

            for (String xpath : changedNodes.keySet()) {
                String value = changedNodes.get(xpath);
                if (doc.selectSingleNode(xpath) == null) {
                    org.talend.mdm.webapp.base.server.util.XmlUtil.completeXMLByXPath(doc, xpath);
                }
                doc.selectSingleNode(xpath).setText(value);
            }

            return saveItem(concept, ids, doc.asXML(), false, language);
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(new Locale(language), e.getMessage(), e.getArgs()));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<ItemResult> updateItems(List<UpdateItemModel> updateItems, String language) throws ServiceException {
        List<ItemResult> resultes = new ArrayList<ItemResult>();
        for (UpdateItemModel item : updateItems) {
            try {
                resultes.add(updateItem(item.getConcept(), item.getIds(), item.getChangedNodes(), null, language));
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return resultes;
    }

    @Override
    public ColumnTreeLayoutModel getColumnTreeLayout(String concept) throws ServiceException {
        try {
            CustomFormPOJOPK pk = new CustomFormPOJOPK(getCurrentDataModel(), concept);
            CustomFormPOJO customForm = com.amalto.core.util.Util.getCustomFormCtrlLocal().getUserCustomForm(pk);
            if (customForm == null) {
                return null;
            }
            String xml = customForm.getXml();
            Document doc = Util.parse(xml);
            Element root = doc.getDocumentElement();
            return ViewHelper.builderLayout(root);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean isItemModifiedByOthers(ItemBean itemBean) throws ServiceException {
        try {
            WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                    new WSGetBusinessConceptKey(new WSDataModelPK(getCurrentDataModel()), itemBean.getConcept()));
            ItemPOJOPK itempk = new ItemPOJOPK(new DataClusterPOJOPK(getCurrentDataCluster()), itemBean.getConcept(),
                    CommonUtil.extractIdWithDots(key.getFields(), itemBean.getIds()));
            boolean isModified = com.amalto.core.util.Util.getItemCtrl2Local().isItemModifiedByOther(itempk,
                    itemBean.getLastUpdateTime());
            return isModified;
        } catch (WebBaseException e) {
            throw new ServiceException(BASEMESSAGE.getMessage(e.getMessage(), e.getArgs()));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public ForeignKeyModel getForeignKeyModel(String concept, String ids, boolean isStaging, String language)
            throws ServiceException {
        try {
            String viewPk = "Browse_items_" + concept; //$NON-NLS-1$
            ViewBean viewBean = getView(viewPk, language);

            ItemBean itemBean = new ItemBean(concept, ids, null);
            itemBean = getItem(itemBean, viewPk, viewBean.getBindingEntityModel(), isStaging, language);
            if (checkSmartViewExistsByLang(concept, language)) {
                itemBean.setSmartViewMode(ItemBean.SMARTMODE);
            } else if (checkSmartViewExistsByOpt(concept, language)) {
                itemBean.setSmartViewMode(ItemBean.PERSOMODE);
            }
            ItemNodeModel nodeModel = getItemNodeModel(itemBean, viewBean.getBindingEntityModel(), isStaging, language);
            return new ForeignKeyModel(viewBean, itemBean, nodeModel);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<ItemBaseModel> getRunnableProcessList(String businessConcept, String language) throws ServiceException {
        List<ItemBaseModel> processList = new ArrayList<ItemBaseModel>();
        if (businessConcept == null || language == null) {
            return processList;
        }
        try {
            String model = this.getCurrentDataModel();
            String[] businessConcepts = Util.getPort().getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(model)))
                    .getStrings();
            WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK();//$NON-NLS-1$
            for (WSTransformerPK transformerPK : wst) {
                if (isMyRunnableProcess(transformerPK.getPk(), businessConcept, businessConcepts)) {
                    WSTransformer trans = Util.getPort().getTransformer(new WSGetTransformer(transformerPK));
                    String description = trans.getDescription();
                    String name = MultilanguageMessageParser.pickOutISOMessage(description, language);
                    if ("".equals(name)) {//$NON-NLS-1$
                        String action = MESSAGES.getMessage("default_action"); //$NON-NLS-1$
                        if (action != null && action.trim().length() > 0) {
                            name = action;
                        } else {
                            name = description;
                        }
                    }
                    ItemBaseModel itemBaseModel = new ItemBaseModel();
                    itemBaseModel.set("key", transformerPK.getPk()); //$NON-NLS-1$
                    itemBaseModel.set("value", name); //$NON-NLS-1$
                    processList.add(itemBaseModel);
                }
            }
            return processList;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private boolean isMyRunnableProcess(String transformerName, String ownerConcept, String[] businessConcepts) {

        String possibleConcept = "";//$NON-NLS-1$
        if (businessConcepts != null) {
            for (String businessConcept : businessConcepts) {
                if (transformerName.startsWith("Runnable_" + businessConcept)) {//$NON-NLS-1$
                    if (businessConcept.length() > possibleConcept.length()) {
                        possibleConcept = businessConcept;
                    }
                }
            }
        }

        if (ownerConcept != null && ownerConcept.equals(possibleConcept)) {
            return true;
        }

        return false;
    }

    @Override
    public String processItem(String concept, String[] ids, String transformerPK) throws ServiceException {
        return null;
    }

    @Override
    public List<String> getLineageEntity(String concept) throws ServiceException {
        try {
            return SchemaWebAgent.getInstance().getReferenceEntities(concept);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    /**
     ********** Smart View**********
     **/
    private boolean checkSmartViewExistsByLang(String concept, String language) throws Exception {
        return SmartViewUtil.checkSmartViewExistsByLangAndOptName(concept, language, null, true);
    }

    private boolean checkSmartViewExistsByOpt(String concept, String language) throws Exception {
        SmartViewProvider provider = new DefaultSmartViewProvider();
        SmartViewDescriptions smDescs = SmartViewUtil.build(provider, concept, language);

        Set<SmartViewDescriptions.SmartViewDescription> smDescSet = smDescs.get(language);

        // Add the no language Smart Views too
        smDescSet.addAll(smDescs.get(null));

        if (!smDescSet.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ItemBaseModel> getSmartViewList(String regex) throws ServiceException {
        try {
            List<ItemBaseModel> smartViewList = new ArrayList<ItemBaseModel>();
            if (regex == null || regex.length() == 0) {
                return smartViewList;
            }

            String[] inputParams = regex.split("&");//$NON-NLS-1$
            String concept = inputParams[0];
            String language = inputParams[1];

            // Get SmartViews from processes
            SmartViewProvider provider = new DefaultSmartViewProvider();
            SmartViewDescriptions smDescs = SmartViewUtil.build(provider, concept, language);

            // Get the lang Smart Views first : Smart_view_<entity>_<ISO> and Smart_view_<entity>_<ISO>#<option>
            Set<SmartViewDescriptions.SmartViewDescription> smDescSet = smDescs.get(language);
            // Add the fallback noLang Smart Views too : Smart_view_<entity> and Smart_view_<entity>#<option>
            smDescSet.addAll(smDescs.get(null));

            for (SmartViewDescriptions.SmartViewDescription smDesc : smDescSet) {
                String value = URLEncoder.encode(smDesc.getName(), "UTF-8"); //$NON-NLS-1$
                ItemBaseModel itemBaseModel = new ItemBaseModel();
                itemBaseModel.set("key", value); //$NON-NLS-1$
                itemBaseModel.set("value", smDesc.getDisplayName()); //$NON-NLS-1$
                smartViewList.add(itemBaseModel);
            }
            return smartViewList;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(MESSAGES.getMessage("unable_getsmart_viewlist")); //$NON-NLS-1$
        }
    }

    /**
     * 1.see if there is a job in the view 2.invoke the job. 3.convert the job's return value into xml doc, 4.convert
     * the wsItem's xml String value into xml doc, 5.cover wsItem's xml with job's xml value. step 6 and 7 must do
     * first. 6.add properties into ViewPOJO. 7.add properties into webservice parameter.
     */
    private void extractUsingTransformerThroughView(String viewName, ComplexTypeMetadata type, WSItem wsItem) throws Exception {
        if (viewName == null || viewName.length() == 0) {
            return;
        }
        List<String> lookupFieldsForWSItemDoc = new ArrayList<String>();
        List<FieldMetadata> lookupFields = type.getLookupFields();
        for (FieldMetadata lookupField : lookupFields) {
            lookupFieldsForWSItemDoc.add(lookupField.getPath());
        }
        if (lookupFields.isEmpty()) {
            return; // Shortcut: no lookup field, no need to go further.
        }
        WSView view = Util.getPort().getView(new WSGetView(new WSViewPK(viewName)));
        if ((null != view.getTransformerPK() && view.getTransformerPK().length() != 0) && view.getIsTransformerActive().is_true()) {
            String transformerPK = view.getTransformerPK();
            // FIXME: consider about revision
            String passToProcessContent = wsItem.getContent();
            WSTypedContent typedContent = new WSTypedContent(null, new WSByteArray(passToProcessContent.getBytes("UTF-8")), //$NON-NLS-1$
                    "text/xml; charset=UTF-8"); //$NON-NLS-1$
            WSTransformerContext wsTransformerContext = new WSTransformerContext(new WSTransformerV2PK(transformerPK), null, null);
            WSExecuteTransformerV2 wsExecuteTransformerV2 = new WSExecuteTransformerV2(wsTransformerContext, typedContent);
            // check binding transformer
            // we can leverage the exception mechanism also
            boolean isATransformerExist = false;
            WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK(); //$NON-NLS-1$
            for (WSTransformerPK element : wst) {
                if (element.getPk().equals(transformerPK)) {
                    isATransformerExist = true;
                    break;
                }
            }
            // execute
            WSTransformer wsTransformer = Util.getPort().getTransformer(new WSGetTransformer(new WSTransformerPK(transformerPK)));
            if (wsTransformer.getPluginSpecs() == null || wsTransformer.getPluginSpecs().length == 0) {
                throw new ServiceException(MESSAGES.getMessage("plugin_specs_undefined")); //$NON-NLS-1$
            }
            WSTransformerContextPipelinePipelineItem[] entries;
            if (isATransformerExist) {
                entries = Util.getPort().executeTransformerV2(wsExecuteTransformerV2).getPipeline().getPipelineItem();
            } else {
                throw new ServiceException(MESSAGES.getMessage("process_not_found")); //$NON-NLS-1$
            }
            WSTransformerContextPipelinePipelineItem entrie = null;
            boolean found = false;
            // FIXME:use 'output' as spec.
            for (WSTransformerContextPipelinePipelineItem entrie2 : entries) {
                if ("output".equals(entrie2.getVariable())) { //$NON-NLS-1$
                    entrie = entrie2;
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (WSTransformerContextPipelinePipelineItem entrie2 : entries) {
                    if ("_DEFAULT_".equals(entrie2.getVariable())) { //$NON-NLS-1$
                        entrie = entrie2;
                        break;
                    }
                }
            }
            String xmlStringFromProcess;
            if (entrie != null && entrie.getWsTypedContent().getWsBytes().getBytes() != null
                    && entrie.getWsTypedContent().getWsBytes().getBytes().length != 0) {
                xmlStringFromProcess = new String(entrie.getWsTypedContent().getWsBytes().getBytes(), "UTF-8"); //$NON-NLS-1$
            } else {
                xmlStringFromProcess = null;
            }
            Document wsItemDoc = Util.parse(wsItem.getContent());
            Document jobDoc = Util.parse(xmlStringFromProcess);
            String searchPrefix;
            NodeList attrNodeList = Util.getNodeList(jobDoc, "/results/item/attr"); //$NON-NLS-1$
            if (attrNodeList != null && attrNodeList.getLength() > 0) {
                searchPrefix = "/results/item/attr/"; //$NON-NLS-1$
            } else {
                searchPrefix = ""; //$NON-NLS-1$
            }
            for (String xpath : lookupFieldsForWSItemDoc) {
                String firstValue = Util.getFirstTextNode(jobDoc, searchPrefix + xpath);// FIXME:use first node
                if (null != firstValue && firstValue.length() != 0) {
                    NodeList list = Util.getNodeList(wsItemDoc, "/" + xpath); //$NON-NLS-1$
                    if (list != null && list.getLength() > 0) {
                        list.item(0).setTextContent(firstValue);
                    }
                }
            }
            wsItem.setContent(Util.nodeToString(wsItemDoc));
        }
    }

    @Override
    public ItemBean getItemBeanById(String concept, String[] ids, String language) throws ServiceException {
        try {
            WSItem wsItem = CommonUtil.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(this.getCurrentDataCluster()), concept, ids)));
            String[] idsArr = wsItem.getIds();
            StringBuilder sb = new StringBuilder();
            for (String str : idsArr) {
                sb.append(str).append("."); //$NON-NLS-1$
            }
            String idsStr = sb.substring(0, sb.length() - 1);
            ItemBean itemBean = new ItemBean(concept, idsStr, wsItem.getContent());
            if (wsItem.getTaskId() != null && !"".equals(wsItem.getTaskId()) && !"null".equals(wsItem.getTaskId())) { //$NON-NLS-1$ //$NON-NLS-2$
                itemBean.setTaskId(wsItem.getTaskId());
            }
            itemBean.set("time", wsItem.getInsertionTime()); //$NON-NLS-1$
            String model = getCurrentDataModel();
            MetadataRepository repository = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin().get(model);
            ComplexTypeMetadata type = repository.getComplexType(concept);
            dynamicAssemble(itemBean, type, language);
            return itemBean;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Locale locale = new Locale(language);
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof WebCoreException) {
                cause = cause.getCause();
                if (cause != null && cause instanceof EntityNotFoundException) {
                    throw new ServiceException(MESSAGES.getMessage(locale, "record_not_found_msg")); //$NON-NLS-1$
                }
            }
            throw new ServiceException(MESSAGES.getMessage(locale, "parse_model_error")); //$NON-NLS-1$ 
        }
    }

    @Override
    public boolean isExistId(String concept, String[] ids, String language) throws ServiceException {
        try {
            WSBoolean wsBoolean = CommonUtil.getPort().existsItem(
                    new WSExistsItem(new WSItemPK(new WSDataClusterPK(this.getCurrentDataCluster()), concept, ids)));
            return wsBoolean.is_true();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<VisibleRuleResult> executeVisibleRule(ViewBean viewBean, String xml) throws ServiceException {
        try {
            String concept = viewBean.getBindingEntityModel().getConceptName();
            EntityModel entity = viewBean.getBindingEntityModel();
            Map<String, TypeModel> metaDataTypes = entity.getMetaDataTypes();
            DisplayRuleEngine ruleEngine = new DisplayRuleEngine(metaDataTypes, concept);
            org.dom4j.Document doc = org.talend.mdm.webapp.base.server.util.XmlUtil.parseText(xml);
            return ruleEngine.execVisibleRule(doc);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public String formatValue(FormatModel model) throws ServiceException {
        Locale locale = new Locale(model.getLanguage());
        try {
            return String.format(new Locale(model.getLanguage()), model.getFormat(), model.getObject());
        } catch (IllegalArgumentException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServiceException(MESSAGES.getMessage(locale,
                    "format_exception_failure", model.getFormat(), model.getObject().toString())); //$NON-NLS-1$
        }
    }

    @Override
    public String getGoldenRecordIdByGroupId(String dataClusterPK, String viewPK, String concept, String[] keys, String groupId)
            throws ServiceException {

        WSWhereCondition whereCondition_Status_SUCCESS_VALIDATE = new WSWhereCondition(concept + StagingConstant.STAGING_STATUS,
                WSWhereOperator.EQUALS, StagingConstants.SUCCESS_VALIDATE, WSStringPredicate.NONE, false);
        WSWhereItem whereItem_Status_SUCCESS_VALIDATE = new WSWhereItem(whereCondition_Status_SUCCESS_VALIDATE, null, null);

        WSWhereCondition whereCondition_TaskID = new WSWhereCondition(StagingConstant.STAGING_TASKID.substring(1),
                WSWhereOperator.EQUALS, groupId, WSStringPredicate.NONE, false);
        WSWhereItem whereItem_TaskID = new WSWhereItem(whereCondition_TaskID, null, null);

        WSWhereItem[] whereItem_Array = { whereItem_TaskID, whereItem_Status_SUCCESS_VALIDATE };
        WSWhereAnd whereAnd = new WSWhereAnd(whereItem_Array);

        WSWhereItem whereItem = new WSWhereItem(null, whereAnd, null);
        try {
            StringBuilder ids = new StringBuilder();
            String[] results = CommonUtil
                    .getPort()
                    .viewSearch(
                            new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewPK), whereItem, -1, 0, 5, null,
                                    null)).getStrings();
            if (results.length == 2) {
                Document doc = parseResultDocument(results[1], "result"); //$NON-NLS-1$

                for (String key : keys) {
                    String id = Util.getFirstTextNode(doc.getDocumentElement(), "." + key.substring(key.lastIndexOf('/'))); //$NON-NLS-1$
                    if (id != null) {
                        if (ids.length() != 0) {
                            ids.append("."); //$NON-NLS-1$
                        }
                        ids.append(id);
                    }
                }
            }
            return ids.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<ItemBean> getRecords(String concept, List<String> idsList) throws ServiceException {
        List<ItemBean> records = new ArrayList<ItemBean>();
        ItemBean itemBean;
        try {
            for (int i = 0; i < idsList.size(); i++) {
                String[] ids = StringUtils.splitPreserveAllTokens(idsList.get(i), '.');
                WSItem wsItem = CommonUtil.getPort().getItem(
                        new WSGetItem(new WSItemPK(new WSDataClusterPK(this.getCurrentDataCluster()), concept, ids)));
                itemBean = new ItemBean();
                itemBean.setItemXml(wsItem.getContent());
                itemBean.setTaskId(wsItem.getTaskId());
                records.add(itemBean);
            }
            return records;
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new ServiceException(exception.getLocalizedMessage());
        }
    }

    private TypeModel findTypeModelByTypePath(Map<String, TypeModel> metaDataTypes, String typePath, String language)
            throws ServiceException {
        try {
            return DataModelHelper.findTypeModelByTypePath(metaDataTypes, typePath);
        } catch (TypeModelNotFoundException e) {
            throw new ServiceException(MESSAGES.getMessage(new Locale(language), "typemodel_notfound", e.getXpathNotFound())); //$NON-NLS-1$
        }
    }

    private String getErrorMessageFromWebCoreException(WebCoreException webCoreException, String concept, String ids,
            Locale locale) {
        String localizedMessage = ""; //$NON-NLS-1$
        if (webCoreException.getCause() != null && webCoreException.getCause().getLocalizedMessage() != null) {
            localizedMessage = webCoreException.getCause().getLocalizedMessage();
        } else {
            localizedMessage = webCoreException.getLocalizedMessage();
        }
        String errorMessage = MESSAGES.getMessage(locale, webCoreException.getTitle(), concept
                + ((ids != null && !"".equals(ids)) ? "." + ids : ""), localizedMessage); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return errorMessage;
    }

    @Override
    public List<ForeignKeyBean> getForeignKeySuggestion(BasePagingLoadConfigImpl config, String foregnKey,
            List<String> foregnKeyInfo, String dataClusterPK, boolean ifFKFilter, String input, String language)
            throws ServiceException {
        try {
            String keyWords = input;
            String pattern = "[^a-zA-Z0-9\\s\\@\\.\\-\\_\\'\"]"; //$NON-NLS-1$
            String foregnKeyConcept = foregnKey.split("/")[0]; //$NON-NLS-1$
            EntityModel entityModel = getEntityModel(foregnKeyConcept, language);

            TypeModel typeModel = entityModel.getTypeModel(foregnKeyConcept);
            typeModel.setForeignkey(foregnKey);
            typeModel.setForeignKeyInfo(foregnKeyInfo);
            typeModel.setRetrieveFKinfos(true);

            if (input.contains(":") && input.indexOf(":") > 0) { //$NON-NLS-1$ //$NON-NLS-2$
                String entityName = input.split(":")[0]; //$NON-NLS-1$

                entityModel = getForeignKeyEntityModel(foregnKey, entityName, language);
                if (entityModel != null) {
                    if (input.indexOf(":") < input.length() - 1) { //$NON-NLS-1$
                        keyWords = input.split(":")[1]; //$NON-NLS-1$
                    }
                    typeModel = replaceForeignKeyTypeModel(typeModel, entityName);
                }
            }

            keyWords = keyWords.replaceAll(pattern, ""); //$NON-NLS-1$
            ItemBasePageLoadResult<ForeignKeyBean> loadResult = ForeignKeyHelper.getForeignKeyList(config, typeModel,
                    entityModel, dataClusterPK, ifFKFilter, keyWords);

            return loadResult.getData();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private EntityModel getForeignKeyEntityModel(String foregnKey, String entityName, String language) throws ServiceException {
        List<Restriction> foregnKeyEntityList = getForeignKeyPolymTypeList(foregnKey, language);

        for (Restriction re : foregnKeyEntityList) {
            if (entityName.equals(re.getValue())) {
                return getEntityModel(entityName, language);
            }
        }
        return null;
    }

    private TypeModel replaceForeignKeyTypeModel(TypeModel typeModel, String entityName) throws ServiceException {
        TypeModel newTypeModel = typeModel;
        List<String> newFKInfoList = new ArrayList<String>();
        String foregnKeyConcept = typeModel.getForeignkey().split("/")[0]; //$NON-NLS-1$
        newTypeModel.setForeignkey(typeModel.getForeignkey().replace(foregnKeyConcept, entityName));
        for (String foreignKeyInfo : typeModel.getForeignKeyInfo()) {
            newFKInfoList.add(foreignKeyInfo.replace(foregnKeyConcept, entityName));
        }
        newTypeModel.setForeignKeyInfo(newFKInfoList);

        return newTypeModel;
    }
}
