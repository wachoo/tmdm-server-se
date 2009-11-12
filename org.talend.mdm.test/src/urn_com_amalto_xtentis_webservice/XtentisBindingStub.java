/**
 * XtentisBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package urn_com_amalto_xtentis_webservice;

public class XtentisBindingStub extends org.apache.axis.client.Stub implements urn_com_amalto_xtentis_webservice.XtentisPort {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[163];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
        _initOperationDesc6();
        _initOperationDesc7();
        _initOperationDesc8();
        _initOperationDesc9();
        _initOperationDesc10();
        _initOperationDesc11();
        _initOperationDesc12();
        _initOperationDesc13();
        _initOperationDesc14();
        _initOperationDesc15();
        _initOperationDesc16();
        _initOperationDesc17();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getComponentVersion");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetComponentVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetComponentVersion"), urn_com_amalto_xtentis_webservice.WSGetComponentVersion.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersion"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersion.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersion"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ping");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPing"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPing"), urn_com_amalto_xtentis_webservice.WSPing.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("logout");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLogout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLogout"), urn_com_amalto_xtentis_webservice.WSLogout.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("initMDM");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInitData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInitData"), urn_com_amalto_xtentis_webservice.WSInitData.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSInt.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataModelPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataModelPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataModelPKs"), urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataModelPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsDataModelPKs"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataModel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataModel"), urn_com_amalto_xtentis_webservice.WSGetDataModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModel"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataModel.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModel"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsDataModel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataModel"), urn_com_amalto_xtentis_webservice.WSExistsDataModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putDataModel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataModel"), urn_com_amalto_xtentis_webservice.WSPutDataModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataModelPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteDataModel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataModel"), urn_com_amalto_xtentis_webservice.WSDeleteDataModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataModelPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("checkSchema");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckSchema"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckSchema"), urn_com_amalto_xtentis_webservice.WSCheckSchema.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteBusinessConcept");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteBusinessConcept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteBusinessConcept"), urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBusinessConcepts");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConcepts"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConcepts"), urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putBusinessConcept");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConcept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConcept"), urn_com_amalto_xtentis_webservice.WSPutBusinessConcept.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putBusinessConceptSchema");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConceptSchema"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConceptSchema"), urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBusinessConceptKey");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptKey"), urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptKey"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSConceptKey.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptKey"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataClusterPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataClusterPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataClusterPKs"), urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataClusterPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsDataClusterPKs"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataCluster"), urn_com_amalto_xtentis_webservice.WSGetDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataCluster"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataCluster.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataCluster"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataCluster"), urn_com_amalto_xtentis_webservice.WSExistsDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsDBDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDBDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDBDataCluster"), urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataCluster"), urn_com_amalto_xtentis_webservice.WSPutDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataClusterPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putDBDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDBDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDBDataCluster"), urn_com_amalto_xtentis_webservice.WSPutDBDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataCluster"), urn_com_amalto_xtentis_webservice.WSDeleteDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDataClusterPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConceptsInDataCluster");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataCluster"), urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConceptsInDataClusterWithRevisions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataClusterWithRevisions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataClusterWithRevisions"), urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptRevisionMap"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptRevisionMap"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "mapEntry"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getViewPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetViewPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetViewPKs"), urn_com_amalto_xtentis_webservice.WSGetViewPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSViewPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsViewPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetView"), urn_com_amalto_xtentis_webservice.WSGetView.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSView"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSView.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSView"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsView"), urn_com_amalto_xtentis_webservice.WSExistsView.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutView"), urn_com_amalto_xtentis_webservice.WSPutView.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSViewPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteView"), urn_com_amalto_xtentis_webservice.WSDeleteView.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSViewPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBusinessConceptValue");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptValue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptValue"), urn_com_amalto_xtentis_webservice.WSGetBusinessConceptValue.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getFullPathValues");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetFullPathValues"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetFullPathValues"), urn_com_amalto_xtentis_webservice.WSGetFullPathValues.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItem"), urn_com_amalto_xtentis_webservice.WSGetItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItem"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItem.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItem"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsItem"), urn_com_amalto_xtentis_webservice.WSExistsItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getItems");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItems"), urn_com_amalto_xtentis_webservice.WSGetItems.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getItemPKsByCriteria");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByCriteria"), urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "results"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("viewSearch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewSearch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewSearch"), urn_com_amalto_xtentis_webservice.WSViewSearch.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("xPathsSearch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSXPathsSearch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSXPathsSearch"), urn_com_amalto_xtentis_webservice.WSXPathsSearch.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getItemsPivotIndex");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemsPivotIndex"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemsPivotIndex"), urn_com_amalto_xtentis_webservice.WSGetItemsPivotIndex.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("count");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCount"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCount"), urn_com_amalto_xtentis_webservice.WSCount.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("quickSearch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSQuickSearch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSQuickSearch"), urn_com_amalto_xtentis_webservice.WSQuickSearch.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItem"), urn_com_amalto_xtentis_webservice.WSPutItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putItemArray");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemArray"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemArray"), urn_com_amalto_xtentis_webservice.WSPutItem[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "wsPutItem"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putItemWithReportArray");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReportArray"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReportArray"), urn_com_amalto_xtentis_webservice.WSPutItemWithReport[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "wsPutItem"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[42] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putItemWithReport");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReport"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReport"), urn_com_amalto_xtentis_webservice.WSPutItemWithReport.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[43] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("extractUsingTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformer"), urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "typedContentEntry"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[44] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("extractUsingTransformerThruView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformerThruView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformerThruView"), urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "typedContentEntry"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[45] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItem"), urn_com_amalto_xtentis_webservice.WSDeleteItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[46] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteItems");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItems"), urn_com_amalto_xtentis_webservice.WSDeleteItems.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSInt.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[47] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("dropItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDropItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDropItem"), urn_com_amalto_xtentis_webservice.WSDropItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDroppedItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[48] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("runQuery");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRunQuery"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRunQuery"), urn_com_amalto_xtentis_webservice.WSRunQuery.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[49] = oper;

    }

    private static void _initOperationDesc6(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("connectorInteraction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteraction"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteraction"), urn_com_amalto_xtentis_webservice.WSConnectorInteraction.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteractionResponse"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteractionResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[50] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRoutingRulePKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRulePKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRulePKs"), urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingRulePK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsRoutingRulePKs"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[51] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRoutingRule");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRule"), urn_com_amalto_xtentis_webservice.WSGetRoutingRule.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRule"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingRule.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRule"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[52] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsRoutingRule");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingRule"), urn_com_amalto_xtentis_webservice.WSExistsRoutingRule.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[53] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putRoutingRule");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRoutingRule"), urn_com_amalto_xtentis_webservice.WSPutRoutingRule.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingRulePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[54] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteRoutingRule");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingRule"), urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingRulePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[55] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("serviceAction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceAction"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceAction"), urn_com_amalto_xtentis_webservice.WSServiceAction.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[56] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getServiceConfiguration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetConfiguration"), urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[57] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putServiceConfiguration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicePutConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicePutConfiguration"), urn_com_amalto_xtentis_webservice.WSServicePutConfiguration.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[58] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getServicesList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetServicesList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetServicesList"), urn_com_amalto_xtentis_webservice.WSGetServicesList.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicesList"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSServicesListItem[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicesList"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "Item"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[59] = oper;

    }

    private static void _initOperationDesc7(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getServiceDocument");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"), urn_com_amalto_xtentis_webservice.WSString.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetDocument"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSServiceGetDocument.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetDocument"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[60] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStoredProcedure");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetStoredProcedure"), urn_com_amalto_xtentis_webservice.WSGetStoredProcedure.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedure"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSStoredProcedure.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedure"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[61] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsStoredProcedure");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsStoredProcedure"), urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[62] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStoredProcedurePKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexStoredProcedure"), urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsStoredProcedurePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[63] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putStoredProcedure");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutStoredProcedure"), urn_com_amalto_xtentis_webservice.WSPutStoredProcedure.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSStoredProcedurePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[64] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteStoredProcedure");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteStoredProcedure"), urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSStoredProcedurePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[65] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("executeStoredProcedure");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteStoredProcedure"), urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[66] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformer"), urn_com_amalto_xtentis_webservice.WSGetTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformer"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformer.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformer"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[67] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformer"), urn_com_amalto_xtentis_webservice.WSExistsTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[68] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformerPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPKs"), urn_com_amalto_xtentis_webservice.WSGetTransformerPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsTransformerPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[69] = oper;

    }

    private static void _initOperationDesc8(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformer"), urn_com_amalto_xtentis_webservice.WSPutTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[70] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformer"), urn_com_amalto_xtentis_webservice.WSDeleteTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[71] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processBytesUsingTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformer"), urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "typedContentEntry"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[72] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processFileUsingTransformer");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformer"), urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "typedContentEntry"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[73] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processBytesUsingTransformerAsBackgroundJob");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformerAsBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformerAsBackgroundJob"), urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[74] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processFileUsingTransformerAsBackgroundJob");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformerAsBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformerAsBackgroundJob"), urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[75] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformerV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2"), urn_com_amalto_xtentis_webservice.WSGetTransformerV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerV2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[76] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsTransformerV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerV2"), urn_com_amalto_xtentis_webservice.WSExistsTransformerV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[77] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformerV2PKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2PKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2PKs"), urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerV2PK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsTransformerV2PK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[78] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putTransformerV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformerV2"), urn_com_amalto_xtentis_webservice.WSPutTransformerV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerV2PK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[79] = oper;

    }

    private static void _initOperationDesc9(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteTransformerV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformerV2"), urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerV2PK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[80] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("executeTransformerV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2"), urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerContext.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[81] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("executeTransformerV2AsJob");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2AsJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2AsJob"), urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[82] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("extractThroughTransformerV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractThroughTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractThroughTransformerV2"), urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerContext.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[83] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsTransformerPluginV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerPluginV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerPluginV2"), urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[84] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformerPluginV2Configuration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2GetConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2GetConfiguration"), urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[85] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putTransformerPluginV2Configuration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2PutConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2PutConfiguration"), urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[86] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformerPluginV2Details");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2Details"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2Details"), urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2Details"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2Details"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[87] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransformerPluginV2sList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2sList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2sList"), urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2sList"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2sList"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "Item"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[88] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRole");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRole"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRole"), urn_com_amalto_xtentis_webservice.WSGetRole.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRole"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRole.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRole"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[89] = oper;

    }

    private static void _initOperationDesc10(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsRole");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRole"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRole"), urn_com_amalto_xtentis_webservice.WSExistsRole.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[90] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRolePKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRolePKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRolePKs"), urn_com_amalto_xtentis_webservice.WSGetRolePKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRolePK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsRolePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[91] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putRole");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRole"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRole"), urn_com_amalto_xtentis_webservice.WSPutRole.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRolePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[92] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteRole");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRole"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRole"), urn_com_amalto_xtentis_webservice.WSDeleteRole.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRolePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[93] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getObjectsForRoles");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForRoles"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForRoles"), java.lang.String[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "regex"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[94] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMenu");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenu"), urn_com_amalto_xtentis_webservice.WSGetMenu.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenu"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSMenu.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenu"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[95] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsMenu");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsMenu"), urn_com_amalto_xtentis_webservice.WSExistsMenu.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[96] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMenuPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenuPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenuPKs"), urn_com_amalto_xtentis_webservice.WSGetMenuPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSMenuPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsMenuPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[97] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putMenu");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutMenu"), urn_com_amalto_xtentis_webservice.WSPutMenu.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSMenuPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[98] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteMenu");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteMenu"), urn_com_amalto_xtentis_webservice.WSDeleteMenu.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSMenuPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[99] = oper;

    }

    private static void _initOperationDesc11(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningCommitItems");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningCommitItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningCommitItems"), urn_com_amalto_xtentis_webservice.WSVersioningCommitItems.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[100] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningRestoreItemByRevision");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreItemByRevision"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreItemByRevision"), urn_com_amalto_xtentis_webservice.WSVersioningRestoreItemByRevision.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[101] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningGetItemHistory");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemHistory"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemHistory"), urn_com_amalto_xtentis_webservice.WSVersioningGetItemHistory.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningItemHistory"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersioningItemHistory.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningItemHistory"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[102] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningGetItemsVersions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemsVersions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemsVersions"), urn_com_amalto_xtentis_webservice.WSVersioningGetItemsVersions.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningItemsVersions"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningItemsVersions"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "items"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[103] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningGetItemContent");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemContent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemContent"), urn_com_amalto_xtentis_webservice.WSVersioningGetItemContent.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[104] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningGetObjectsVersions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetObjectsVersions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetObjectsVersions"), urn_com_amalto_xtentis_webservice.WSVersioningGetObjectsVersions.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningObjectsVersions"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningObjectsVersions"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "objects"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[105] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningGetUniverseVersions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetUniverseVersions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetUniverseVersions"), urn_com_amalto_xtentis_webservice.WSVersioningGetUniverseVersions.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningUniverseVersions"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningUniverseVersions"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "tagStructure"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[106] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVersioningSystemConfiguration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetVersioningSystemConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetVersioningSystemConfiguration"), urn_com_amalto_xtentis_webservice.WSGetVersioningSystemConfiguration.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningSystemConfiguration"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningSystemConfiguration"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[107] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putVersioningSystemConfiguration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutVersioningSystemConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutVersioningSystemConfiguration"), urn_com_amalto_xtentis_webservice.WSPutVersioningSystemConfiguration.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[108] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningGetInfo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetInfo"), urn_com_amalto_xtentis_webservice.WSVersioningGetInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningInfo"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSVersioningInfo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningInfo"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[109] = oper;

    }

    private static void _initOperationDesc12(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningTagObjects");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagObjects"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagObjects"), urn_com_amalto_xtentis_webservice.WSVersioningTagObjects.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[110] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningTagUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagUniverse"), urn_com_amalto_xtentis_webservice.WSVersioningTagUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[111] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningTagItems");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagItems"), urn_com_amalto_xtentis_webservice.WSVersioningTagItems.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[112] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningRestoreObjects");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreObjects"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreObjects"), urn_com_amalto_xtentis_webservice.WSVersioningRestoreObjects.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[113] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningRestoreUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreUniverse"), urn_com_amalto_xtentis_webservice.WSVersioningRestoreUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[114] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("versioningRestoreItems");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreItems"), urn_com_amalto_xtentis_webservice.WSVersioningRestoreItems.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[115] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findBackgroundJobPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindBackgroundJobPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindBackgroundJobPKs"), urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[116] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBackgroundJob");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBackgroundJob"), urn_com_amalto_xtentis_webservice.WSGetBackgroundJob.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJob"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJob.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJob"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[117] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putBackgroundJob");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBackgroundJob"), urn_com_amalto_xtentis_webservice.WSPutBackgroundJob.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[118] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRoutingOrderV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2"), urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingOrderV2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[119] = oper;

    }

    private static void _initOperationDesc13(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsRoutingOrderV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingOrderV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingOrderV2"), urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingOrderV2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[120] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteRoutingOrderV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingOrderV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingOrderV2"), urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[121] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("executeRoutingOrderV2Asynchronously");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Asynchronously"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Asynchronously"), urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[122] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("executeRoutingOrderV2Synchronously");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Synchronously"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Synchronously"), urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[123] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRoutingOrderV2PKsByCriteria");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2PKsByCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2PKsByCriteria"), urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsRoutingOrder"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[124] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRoutingOrderV2sByCriteria");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2sByCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2sByCriteria"), urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2Array"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2Array"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsRoutingOrder"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[125] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("routeItemV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRouteItemV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRouteItemV2"), urn_com_amalto_xtentis_webservice.WSRouteItemV2.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingRulePK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsRoutingRulePKs"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[126] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("routingEngineV2Action");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Action"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Action"), urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Status"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Status"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[127] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverse"), urn_com_amalto_xtentis_webservice.WSGetUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSUniverse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[128] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsUniverse"), urn_com_amalto_xtentis_webservice.WSExistsUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[129] = oper;

    }

    private static void _initOperationDesc14(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUniversePKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniversePKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniversePKs"), urn_com_amalto_xtentis_webservice.WSGetUniversePKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSUniversePK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsUniversePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[130] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUniverseByRevision");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverseByRevision"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverseByRevision"), urn_com_amalto_xtentis_webservice.WSGetUniverseByRevision.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSUniversePK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsUniversePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[131] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutUniverse"), urn_com_amalto_xtentis_webservice.WSPutUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSUniversePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[132] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteUniverse"), urn_com_amalto_xtentis_webservice.WSDeleteUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSUniversePK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[133] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getObjectsForUniverses");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForUniverses"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForUniverses"), java.lang.String[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "regex"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[134] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCurrentUniverse");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetCurrentUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetCurrentUniverse"), urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSUniverse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[135] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSynchronizationPlan");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlan"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlan"), urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlan.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlan"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationPlan.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlan"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[136] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsSynchronizationPlan");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsSynchronizationPlan"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsSynchronizationPlan"), urn_com_amalto_xtentis_webservice.WSExistsSynchronizationPlan.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[137] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSynchronizationPlanPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanPKs"), urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlanPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsSynchronizationPlanPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[138] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putSynchronizationPlan");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutSynchronizationPlan"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutSynchronizationPlan"), urn_com_amalto_xtentis_webservice.WSPutSynchronizationPlan.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[139] = oper;

    }

    private static void _initOperationDesc15(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteSynchronizationPlan");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteSynchronizationPlan"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteSynchronizationPlan"), urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationPlan.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[140] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getObjectsForSynchronizationPlans");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForSynchronizationPlans"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForSynchronizationPlans"), java.lang.String[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "regex"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[141] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSynchronizationPlanObjectsAlgorithms");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanObjectsAlgorithms"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanObjectsAlgorithms"), java.lang.String[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "algorithm"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[142] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSynchronizationPlanItemsAlgorithms");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanItemsAlgorithms"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanItemsAlgorithms"), java.lang.String[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("", "algorithm"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[143] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationPlanAction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanAction"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanAction"), urn_com_amalto_xtentis_webservice.WSSynchronizationPlanAction.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanStatus"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanStatus"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[144] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationGetUnsynchronizedObjectsIDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetUnsynchronizedObjectsIDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetUnsynchronizedObjectsIDs"), urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedObjectsIDs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "strings"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[145] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationGetObjectXML");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetObjectXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetObjectXML"), urn_com_amalto_xtentis_webservice.WSSynchronizationGetObjectXML.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[146] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationPutObjectXML");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPutObjectXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPutObjectXML"), urn_com_amalto_xtentis_webservice.WSSynchronizationPutObjectXML.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[147] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationGetUnsynchronizedItemPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetUnsynchronizedItemPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetUnsynchronizedItemPKs"), urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedItemPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[148] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationGetItemXML");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetItemXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetItemXML"), urn_com_amalto_xtentis_webservice.WSSynchronizationGetItemXML.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[149] = oper;

    }

    private static void _initOperationDesc16(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizationPutItemXML");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPutItemXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPutItemXML"), urn_com_amalto_xtentis_webservice.WSSynchronizationPutItemXML.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[150] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSynchronizationItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationItem"), urn_com_amalto_xtentis_webservice.WSGetSynchronizationItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItem"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationItem.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItem"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[151] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("existsSynchronizationItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsSynchronizationItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsSynchronizationItem"), urn_com_amalto_xtentis_webservice.WSExistsSynchronizationItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSBoolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[152] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSynchronizationItemPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationItemPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationItemPKs"), urn_com_amalto_xtentis_webservice.WSGetSynchronizationItemPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPKArray"));
        oper.setReturnClass(java.lang.String[][].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsSynchronizationItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[153] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("putSynchronizationItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutSynchronizationItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutSynchronizationItem"), urn_com_amalto_xtentis_webservice.WSPutSynchronizationItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPK"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPK"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "ids"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[154] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteSynchronizationItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteSynchronizationItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteSynchronizationItem"), urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPK"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPK"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "ids"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[155] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("resolveSynchronizationItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSResolveSynchronizationItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSResolveSynchronizationItem"), urn_com_amalto_xtentis_webservice.WSResolveSynchronizationItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItem"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSSynchronizationItem.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItem"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[156] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("recoverDroppedItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRecoverDroppedItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRecoverDroppedItem"), urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[157] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findAllDroppedItemsPKs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindAllDroppedItemsPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindAllDroppedItemsPKs"), urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPKArray"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDroppedItemPK[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPKArray"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "wsDroppedItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[158] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("loadDroppedItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLoadDroppedItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLoadDroppedItem"), urn_com_amalto_xtentis_webservice.WSLoadDroppedItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItem"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDroppedItem.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItem"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[159] = oper;

    }

    private static void _initOperationDesc17(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeDroppedItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRemoveDroppedItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRemoveDroppedItem"), urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSDroppedItemPK.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[160] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMDMConfiguration");
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMDMConfig"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSMDMConfig.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMDMConfig"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[161] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("checkServiceConfiguration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigRequest"), urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigResponse"));
        oper.setReturnClass(urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[162] = oper;

    }

    public XtentisBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public XtentisBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public XtentisBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
        addBindings2();
    }

    private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">>WSRole>specification>instance");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoleSpecificationInstance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">>WSSynchronizationPlan>xtentisObjectsSynchronizations>synchronizations");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">>WSTransformerContext>pipeline>pipelineItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerContextPipelinePipelineItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSConceptRevisionMap>mapEntry");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSItemPKsByCriteriaResponse>results");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSLinkedHashMap>typedContentEntry");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSLinkedHashMapTypedContentEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSMenuEntry>descriptions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSMenuEntryDescriptions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSOutputDecisionTable>decisions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSOutputDecisionTableDecisions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSPipeline>typedContentEntry");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSRole>specification");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoleSpecification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSServicesList>Item");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServicesListItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSSynchronizationItem>remoteInstances");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationItemRemoteInstances.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSSynchronizationPlan>itemsSynchronizations");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanItemsSynchronizations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSSynchronizationPlan>xtentisObjectsSynchronizations");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanXtentisObjectsSynchronizations.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSTransformerContext>pipeline");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerContextPipelinePipelineItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">>WSTransformerContext>pipeline>pipelineItem");
            qName2 = new javax.xml.namespace.QName("", "pipelineItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSTransformerContext>projectedItemPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSItemPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK");
            qName2 = new javax.xml.namespace.QName("", "wsItemPOJOPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSTransformerPluginV2sList>Item");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSUniverse>itemsRevisionIDs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSUniverseItemsRevisionIDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSUniverse>xtentisObjectsRevisionIDs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSUniverseXtentisObjectsRevisionIDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSVersioningItemsVersions>items");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSVersioningObjectsVersions>objects");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSVersioningUniverseVersions>tagStructure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "BackgroundJobStatusType");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.BackgroundJobStatusType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJob");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBackgroundJob.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK");
            qName2 = new javax.xml.namespace.QName("", "wsBackgroundJobPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBase64KeyValue");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBase64KeyValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBoolean.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBusinessConcept");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBusinessConcept.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBusinessConceptPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSBusinessConceptPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSByteArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSByteArray.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckSchema");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSCheckSchema.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigRequest");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigResponse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSComponent");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSComponent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptKey");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConceptKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptRevisionMap");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSConceptRevisionMap>mapEntry");
            qName2 = new javax.xml.namespace.QName("", "mapEntry");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorFunction");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConnectorFunction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteraction");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConnectorInteraction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteractionResponse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorResponseCode");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSConnectorResponseCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCount");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSCount.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDataClusterPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDataClusterPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK");
            qName2 = new javax.xml.namespace.QName("", "wsDataClusterPKs");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModel");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDataModel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDataModelPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDataModelPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK");
            qName2 = new javax.xml.namespace.QName("", "wsDataModelPKs");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteBusinessConcept");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataModel");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteDataModel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItems");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteItems.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteMenu");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteMenu.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRole");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingOrderV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingRule");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteSynchronizationItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteSynchronizationPlan");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationPlan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteView");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDeleteView.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDropItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDropItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDroppedItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDroppedItemPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSDroppedItemPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK");
            qName2 = new javax.xml.namespace.QName("", "wsDroppedItemPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Asynchronously");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Synchronously");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2AsJob");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataModel");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsDataModel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDBDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsMenu");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsMenu.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRole");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingOrderV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingRule");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsRoutingRule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsSynchronizationItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsSynchronizationItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsSynchronizationPlan");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsSynchronizationPlan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerPluginV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsView");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExistsView.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractedContent");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExtractedContent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractThroughTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformerThruView");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindAllDroppedItemsPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindBackgroundJobPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBackgroundJob");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetBackgroundJob.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptKey");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConcepts");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptValue");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetBusinessConceptValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetComponentVersion");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetComponentVersion.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataClusterWithRevisions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetCurrentUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataModel");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetDataModel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetFullPathValues");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetFullPathValues.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByCriteria");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItems");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetItems.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemsPivotIndex");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetItemsPivotIndex.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenu");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetMenu.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenuPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetMenuPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForRoles");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "regex");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForSynchronizationPlans");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "regex");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetObjectsForUniverses");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "regex");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRole");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRolePKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRolePKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2PKsByCriteria");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2sByCriteria");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRule");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRoutingRule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRulePKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetServicesList");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetServicesList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetSynchronizationItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationItemPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetSynchronizationItemPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlan");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanItemsAlgorithms");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "algorithm");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanObjectsAlgorithms");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "algorithm");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetSynchronizationPlanPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlanPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetTransformerPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2Details");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2sList");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2PKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverseByRevision");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetUniverseByRevision.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniverseByRevisionType");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetUniverseByRevisionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetUniversePKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetUniversePKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetVersioningSystemConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetVersioningSystemConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetView");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetView.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetViewPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSGetViewPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSI18nString");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSI18NString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInitData");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSInitData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSInt.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSItemPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSItemPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK");
            qName2 = new javax.xml.namespace.QName("", "wsItemPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSItemPKsByCriteriaResponse>results");
            qName2 = new javax.xml.namespace.QName("", "results");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSKey");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLanguage");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSLanguage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLinkedHashMap");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSLinkedHashMapTypedContentEntry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSLinkedHashMap>typedContentEntry");
            qName2 = new javax.xml.namespace.QName("", "typedContentEntry");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLoadDroppedItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSLoadDroppedItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLogout");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSLogout.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMDMConfig");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSMDMConfig.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenu");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSMenu.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuEntry");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSMenuEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSMenuPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSMenuPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK");
            qName2 = new javax.xml.namespace.QName("", "wsMenuPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSOutputDecisionTable");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSOutputDecisionTableDecisions[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSOutputDecisionTable>decisions");
            qName2 = new javax.xml.namespace.QName("", "decisions");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPing");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPing.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSPipeline>typedContentEntry");
            qName2 = new javax.xml.namespace.QName("", "typedContentEntry");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformerAsBackgroundJob");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformerAsBackgroundJob");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBackgroundJob");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutBackgroundJob.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConcept");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutBusinessConcept.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConceptSchema");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataModel");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutDataModel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDBDataCluster");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutDBDataCluster.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItem");
            qName2 = new javax.xml.namespace.QName("", "wsPutItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReport");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutItemWithReport.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReportArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutItemWithReport[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReport");
            qName2 = new javax.xml.namespace.QName("", "wsPutItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutMenu");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutMenu.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRole");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRoutingRule");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutRoutingRule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutSynchronizationItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutSynchronizationItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutSynchronizationPlan");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutSynchronizationPlan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutVersioningSystemConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutVersioningSystemConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutView");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSPutView.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSQuickSearch");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSQuickSearch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRecoverDroppedItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataClusterPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataModelPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRemoveDroppedItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSResolveSynchronizationItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSResolveSynchronizationItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRole");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRolePK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRolePK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRolePK");
            qName2 = new javax.xml.namespace.QName("", "wsRolePK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRouteItemV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRouteItemV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings2() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Action");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2ActionCode");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingEngineV2ActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Status");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingOrderV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2Array");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2");
            qName2 = new javax.xml.namespace.QName("", "wsRoutingOrder");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK");
            qName2 = new javax.xml.namespace.QName("", "wsRoutingOrder");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2SearchCriteria");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingOrderV2SearchCriteria.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2Status");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingOrderV2Status.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRule");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingRule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRuleExpression");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingRuleExpression.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRuleOperator");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingRuleOperator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingRulePK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRoutingRulePK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK");
            qName2 = new javax.xml.namespace.QName("", "wsRoutingRulePKs");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRunQuery");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSRunQuery.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceAction");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServiceAction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceActionCode");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServiceActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetDocument");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServiceGetDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicePutConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServicePutConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicesList");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSServicesListItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSServicesList>Item");
            qName2 = new javax.xml.namespace.QName("", "Item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedure");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSStoredProcedure.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSStoredProcedurePK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK");
            qName2 = new javax.xml.namespace.QName("", "wsStoredProcedurePK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "strings");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringPredicate");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSStringPredicate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetItemXML");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationGetItemXML.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetObjectXML");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationGetObjectXML.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetUnsynchronizedItemPKs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedItemPKs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationGetUnsynchronizedObjectsIDs");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedObjectsIDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPK");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "ids");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPKArray");
            cachedSerQNames.add(qName);
            cls = java.lang.String[][].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemPK");
            qName2 = new javax.xml.namespace.QName("", "wsSynchronizationItemPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationItemStatus");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationItemStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlan");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanAction");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanAction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanActionCode");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanActionCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanPK");
            qName2 = new javax.xml.namespace.QName("", "wsSynchronizationPlanPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanStatus");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPlanStatusCode");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatusCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPutItemXML");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPutItemXML.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSSynchronizationPutObjectXML");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSSynchronizationPutObjectXML.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformer");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerContext.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK");
            qName2 = new javax.xml.namespace.QName("", "wsTransformerPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginSpec");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginSpec.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2Details");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2GetConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2PutConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2sList");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSTransformerPluginV2sList>Item");
            qName2 = new javax.xml.namespace.QName("", "Item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2VariableDescriptor");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerPluginV2VariableDescriptor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerProcessStep");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerProcessStep.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerV2PK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerV2PK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK");
            qName2 = new javax.xml.namespace.QName("", "wsTransformerV2PK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerVariablesMapping");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTransformerVariablesMapping.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTypedContent");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSTypedContent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSUniversePK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSUniversePK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniversePK");
            qName2 = new javax.xml.namespace.QName("", "wsUniversePK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersion");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersion.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningCommitItems");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningCommitItems.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetInfo");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningGetInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemContent");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningGetItemContent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemHistory");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningGetItemHistory.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetItemsVersions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningGetItemsVersions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetObjectsVersions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningGetObjectsVersions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningGetUniverseVersions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningGetUniverseVersions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningHistoryEntry");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningHistoryEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningInfo");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningItemHistory");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningItemHistory.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningItemsVersions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSVersioningItemsVersions>items");
            qName2 = new javax.xml.namespace.QName("", "items");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningObjectsVersions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSVersioningObjectsVersions>objects");
            qName2 = new javax.xml.namespace.QName("", "objects");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreItemByRevision");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningRestoreItemByRevision.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreItems");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningRestoreItems.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreObjects");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningRestoreObjects.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningRestoreUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningRestoreUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningSystemConfiguration");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagItems");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningTagItems.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagObjects");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningTagObjects.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningTagUniverse");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningTagUniverse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersioningUniverseVersions");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSVersioningUniverseVersions>tagStructure");
            qName2 = new javax.xml.namespace.QName("", "tagStructure");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSView");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSView.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSViewPK.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPKArray");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSViewPK[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK");
            qName2 = new javax.xml.namespace.QName("", "wsViewPK");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewSearch");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSViewSearch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereAnd");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSWhereItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereItem");
            qName2 = new javax.xml.namespace.QName("", "whereItems");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereCondition");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSWhereCondition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereItem");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSWhereItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereOperator");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSWhereOperator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereOr");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSWhereItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereItem");
            qName2 = new javax.xml.namespace.QName("", "whereItems");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSXPathsSearch");
            cachedSerQNames.add(qName);
            cls = urn_com_amalto_xtentis_webservice.WSXPathsSearch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public urn_com_amalto_xtentis_webservice.WSVersion getComponentVersion(urn_com_amalto_xtentis_webservice.WSGetComponentVersion wsGetComponentVersion) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getComponentVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetComponentVersion});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersion) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersion) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersion.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString ping(urn_com_amalto_xtentis_webservice.WSPing wsPing) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "ping"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPing});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString logout(urn_com_amalto_xtentis_webservice.WSLogout wsLogout) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "logout"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsLogout});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSInt initMDM(urn_com_amalto_xtentis_webservice.WSInitData initData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "initMDM"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {initData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSInt) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSInt) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSInt.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataModelPK[] getDataModelPKs(urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs regexp) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getDataModelPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regexp});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataModelPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataModelPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataModelPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataModel getDataModel(urn_com_amalto_xtentis_webservice.WSGetDataModel wsDataModelget) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getDataModel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDataModelget});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataModel) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataModel) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataModel.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsDataModel(urn_com_amalto_xtentis_webservice.WSExistsDataModel wsDataModelExists) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsDataModel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDataModelExists});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataModelPK putDataModel(urn_com_amalto_xtentis_webservice.WSPutDataModel wsDataModel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putDataModel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDataModel});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataModelPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataModelPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataModelPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataModelPK deleteDataModel(urn_com_amalto_xtentis_webservice.WSDeleteDataModel wsDeleteDataModel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteDataModel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteDataModel});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataModelPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataModelPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataModelPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString checkSchema(urn_com_amalto_xtentis_webservice.WSCheckSchema wsSchema) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "checkSchema"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSchema});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString deleteBusinessConcept(urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept wsDeleteBusinessConcept) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteBusinessConcept"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteBusinessConcept});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getBusinessConcepts(urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts wsGetBusinessConcepts) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getBusinessConcepts"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetBusinessConcepts});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString putBusinessConcept(urn_com_amalto_xtentis_webservice.WSPutBusinessConcept wsPutBusinessConcept) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putBusinessConcept"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutBusinessConcept});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString putBusinessConceptSchema(urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putBusinessConceptSchema"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutBusinessConceptSchema});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSConceptKey getBusinessConceptKey(urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey wsGetBusinessConceptKey) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getBusinessConceptKey"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetBusinessConceptKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSConceptKey) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSConceptKey) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSConceptKey.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataClusterPK[] getDataClusterPKs(urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs regexp) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getDataClusterPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regexp});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataClusterPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataClusterPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataClusterPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataCluster getDataCluster(urn_com_amalto_xtentis_webservice.WSGetDataCluster wsDataClusterPK) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDataClusterPK});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataCluster) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataCluster) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataCluster.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsDataCluster(urn_com_amalto_xtentis_webservice.WSExistsDataCluster wsExistsDataCluster) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsDataCluster});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsDBDataCluster(urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster wsExistsDBDataCluster) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsDBDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsDBDataCluster});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataClusterPK putDataCluster(urn_com_amalto_xtentis_webservice.WSPutDataCluster wsDataCluster) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDataCluster});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataClusterPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataClusterPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataClusterPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean putDBDataCluster(urn_com_amalto_xtentis_webservice.WSPutDBDataCluster wsDataCluster) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putDBDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDataCluster});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDataClusterPK deleteDataCluster(urn_com_amalto_xtentis_webservice.WSDeleteDataCluster wsDeleteDataCluster) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteDataCluster});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDataClusterPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDataClusterPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDataClusterPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getConceptsInDataCluster(urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getConceptsInDataCluster"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetConceptsInDataCluster});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[] getConceptsInDataClusterWithRevisions(urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getConceptsInDataClusterWithRevisions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetConceptsInDataClusterWithRevisions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSViewPK[] getViewPKs(urn_com_amalto_xtentis_webservice.WSGetViewPKs regexp) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getViewPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regexp});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSViewPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSViewPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSViewPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSView getView(urn_com_amalto_xtentis_webservice.WSGetView wsViewPK) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsViewPK});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSView) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSView) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSView.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsView(urn_com_amalto_xtentis_webservice.WSExistsView wsViewPK) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsViewPK});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSViewPK putView(urn_com_amalto_xtentis_webservice.WSPutView wsView) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsView});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSViewPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSViewPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSViewPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSViewPK deleteView(urn_com_amalto_xtentis_webservice.WSDeleteView wsViewDel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsViewDel});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSViewPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSViewPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSViewPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString getBusinessConceptValue(urn_com_amalto_xtentis_webservice.WSGetBusinessConceptValue wsGetBusinessConceptValue) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getBusinessConceptValue"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetBusinessConceptValue});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getFullPathValues(urn_com_amalto_xtentis_webservice.WSGetFullPathValues wsGetFullPathValues) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getFullPathValues"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetFullPathValues});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItem getItem(urn_com_amalto_xtentis_webservice.WSGetItem wsGetItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItem) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItem) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItem.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsItem(urn_com_amalto_xtentis_webservice.WSExistsItem wsExistsItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getItems(urn_com_amalto_xtentis_webservice.WSGetItems wsGetItems) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getItems"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetItems});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[] getItemPKsByCriteria(urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getItemPKsByCriteria"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetItemPKsByCriteria});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] viewSearch(urn_com_amalto_xtentis_webservice.WSViewSearch wsViewSearch) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "viewSearch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsViewSearch});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] xPathsSearch(urn_com_amalto_xtentis_webservice.WSXPathsSearch wsXPathsSearch) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "xPathsSearch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsXPathsSearch});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getItemsPivotIndex(urn_com_amalto_xtentis_webservice.WSGetItemsPivotIndex wsGetItemsPivotIndex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getItemsPivotIndex"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetItemsPivotIndex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString count(urn_com_amalto_xtentis_webservice.WSCount wsCount) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "count"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsCount});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] quickSearch(urn_com_amalto_xtentis_webservice.WSQuickSearch wsQuickSearch) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "quickSearch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsQuickSearch});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK putItem(urn_com_amalto_xtentis_webservice.WSPutItem wsPutItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK[] putItemArray(urn_com_amalto_xtentis_webservice.WSPutItem[] wsPutItemArray) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putItemArray"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutItemArray});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK[] putItemWithReportArray(urn_com_amalto_xtentis_webservice.WSPutItemWithReport[] wsPutItemWithReportArray) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putItemWithReportArray"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutItemWithReportArray});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK putItemWithReport(urn_com_amalto_xtentis_webservice.WSPutItemWithReport wsPutItemWithReport) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[43]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putItemWithReport"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutItemWithReport});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] extractUsingTransformer(urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer wsExtractUsingTransformer) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[44]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "extractUsingTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExtractUsingTransformer});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] extractUsingTransformerThruView(urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[45]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "extractUsingTransformerThruView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExtractUsingTransformerThruView});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK deleteItem(urn_com_amalto_xtentis_webservice.WSDeleteItem wsDeleteItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[46]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSInt deleteItems(urn_com_amalto_xtentis_webservice.WSDeleteItems wsDeleteItems) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[47]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteItems"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteItems});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSInt) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSInt) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSInt.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItemPK dropItem(urn_com_amalto_xtentis_webservice.WSDropItem wsDropItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[48]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "dropItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDropItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDroppedItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] runQuery(urn_com_amalto_xtentis_webservice.WSRunQuery wsRunQuery) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[49]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "runQuery"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRunQuery});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse connectorInteraction(urn_com_amalto_xtentis_webservice.WSConnectorInteraction wsConnectorInteraction) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[50]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "connectorInteraction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsConnectorInteraction});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] getRoutingRulePKs(urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs regexp) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[51]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRoutingRulePKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regexp});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingRulePK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRule getRoutingRule(urn_com_amalto_xtentis_webservice.WSGetRoutingRule wsRoutingRulePK) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[52]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRoutingRule"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRoutingRulePK});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRule) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRule) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingRule.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsRoutingRule(urn_com_amalto_xtentis_webservice.WSExistsRoutingRule wsExistsRoutingRule) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[53]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsRoutingRule"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsRoutingRule});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK putRoutingRule(urn_com_amalto_xtentis_webservice.WSPutRoutingRule wsRoutingRule) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[54]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putRoutingRule"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRoutingRule});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingRulePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK deleteRoutingRule(urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule wsRoutingRuleDel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[55]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteRoutingRule"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRoutingRuleDel});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingRulePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString serviceAction(urn_com_amalto_xtentis_webservice.WSServiceAction wsServiceAction) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[56]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "serviceAction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsServiceAction});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString getServiceConfiguration(urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration wsGetConfiguration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[57]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getServiceConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetConfiguration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString putServiceConfiguration(urn_com_amalto_xtentis_webservice.WSServicePutConfiguration wsPutConfiguration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[58]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putServiceConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutConfiguration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSServicesListItem[] getServicesList(urn_com_amalto_xtentis_webservice.WSGetServicesList wsGetServicesList) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[59]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getServicesList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetServicesList});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSServicesListItem[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSServicesListItem[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSServicesListItem[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSServiceGetDocument getServiceDocument(urn_com_amalto_xtentis_webservice.WSString serviceName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[60]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getServiceDocument"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSServiceGetDocument) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSServiceGetDocument) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSServiceGetDocument.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedure getStoredProcedure(urn_com_amalto_xtentis_webservice.WSGetStoredProcedure wsGetStoredProcedure) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[61]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getStoredProcedure"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetStoredProcedure});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedure) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedure) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSStoredProcedure.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsStoredProcedure(urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure wsExistsStoredProcedure) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[62]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsStoredProcedure"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsStoredProcedure});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[] getStoredProcedurePKs(urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[63]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getStoredProcedurePKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedurePK putStoredProcedure(urn_com_amalto_xtentis_webservice.WSPutStoredProcedure wsStoredProcedure) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[64]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putStoredProcedure"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsStoredProcedure});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedurePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedurePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSStoredProcedurePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedurePK deleteStoredProcedure(urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure wsStoredProcedureDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[65]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteStoredProcedure"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsStoredProcedureDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedurePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSStoredProcedurePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSStoredProcedurePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] executeStoredProcedure(urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure wsExecuteStoredProcedure) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[66]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "executeStoredProcedure"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExecuteStoredProcedure});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformer getTransformer(urn_com_amalto_xtentis_webservice.WSGetTransformer wsGetTransformer) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[67]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetTransformer});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformer) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformer) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformer.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsTransformer(urn_com_amalto_xtentis_webservice.WSExistsTransformer wsExistsTransformer) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[68]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsTransformer});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPK[] getTransformerPKs(urn_com_amalto_xtentis_webservice.WSGetTransformerPKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[69]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformerPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPK putTransformer(urn_com_amalto_xtentis_webservice.WSPutTransformer wsTransformer) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[70]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsTransformer});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPK deleteTransformer(urn_com_amalto_xtentis_webservice.WSDeleteTransformer wsTransformerDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[71]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsTransformerDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] processBytesUsingTransformer(urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer wsProcessBytesUsingTransformer) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[72]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "processBytesUsingTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsProcessBytesUsingTransformer});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] processFileUsingTransformer(urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer wsProcessFileUsingTransformer) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[73]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "processFileUsingTransformer"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsProcessFileUsingTransformer});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[74]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "processBytesUsingTransformerAsBackgroundJob"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsProcessBytesUsingTransformerAsBackgroundJob});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[75]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "processFileUsingTransformerAsBackgroundJob"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsProcessFileUsingTransformerAsBackgroundJob});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2 getTransformerV2(urn_com_amalto_xtentis_webservice.WSGetTransformerV2 wsGetTransformerV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[76]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformerV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetTransformerV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerV2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsTransformerV2(urn_com_amalto_xtentis_webservice.WSExistsTransformerV2 wsExistsTransformerV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[77]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsTransformerV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsTransformerV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2PK[] getTransformerV2PKs(urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[78]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformerV2PKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2PK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2PK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerV2PK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2PK putTransformerV2(urn_com_amalto_xtentis_webservice.WSPutTransformerV2 wsTransformerV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[79]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putTransformerV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsTransformerV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2PK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2PK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerV2PK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2PK deleteTransformerV2(urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2 wsDeleteTransformerV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[80]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteTransformerV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteTransformerV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2PK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerV2PK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerV2PK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerContext executeTransformerV2(urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2 wsExecuteTransformerV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[81]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "executeTransformerV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExecuteTransformerV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerContext) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerContext) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerContext.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK executeTransformerV2AsJob(urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[82]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "executeTransformerV2AsJob"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExecuteTransformerV2AsJob});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerContext extractThroughTransformerV2(urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[83]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "extractThroughTransformerV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExtractThroughTransformerV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerContext) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerContext) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerContext.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsTransformerPluginV2(urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2 wsExistsTransformerPluginV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[84]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsTransformerPluginV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsTransformerPluginV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString getTransformerPluginV2Configuration(urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[85]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformerPluginV2Configuration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetConfiguration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString putTransformerPluginV2Configuration(urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[86]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putTransformerPluginV2Configuration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutConfiguration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details getTransformerPluginV2Details(urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details wsGetTransformerPluginV2Details) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[87]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformerPluginV2Details"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetTransformerPluginV2Details});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[] getTransformerPluginV2SList(urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList wsGetTransformerPluginV2SList) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[88]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getTransformerPluginV2sList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetTransformerPluginV2SList});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRole getRole(urn_com_amalto_xtentis_webservice.WSGetRole wsGetRole) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[89]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRole"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetRole});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRole) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRole) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRole.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsRole(urn_com_amalto_xtentis_webservice.WSExistsRole wsExistsRole) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[90]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsRole"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsRole});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRolePK[] getRolePKs(urn_com_amalto_xtentis_webservice.WSGetRolePKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[91]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRolePKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRolePK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRolePK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRolePK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRolePK putRole(urn_com_amalto_xtentis_webservice.WSPutRole wsRole) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[92]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putRole"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRole});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRolePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRolePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRolePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRolePK deleteRole(urn_com_amalto_xtentis_webservice.WSDeleteRole wsRoleDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[93]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteRole"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRoleDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRolePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRolePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRolePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getObjectsForRoles(java.lang.String[] wsRoleDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[94]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getObjectsForRoles"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRoleDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSMenu getMenu(urn_com_amalto_xtentis_webservice.WSGetMenu wsGetMenu) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[95]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getMenu"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetMenu});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSMenu) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSMenu) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSMenu.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsMenu(urn_com_amalto_xtentis_webservice.WSExistsMenu wsExistsMenu) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[96]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsMenu"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsMenu});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSMenuPK[] getMenuPKs(urn_com_amalto_xtentis_webservice.WSGetMenuPKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[97]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getMenuPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSMenuPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSMenuPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSMenuPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSMenuPK putMenu(urn_com_amalto_xtentis_webservice.WSPutMenu wsMenu) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[98]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putMenu"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsMenu});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSMenuPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSMenuPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSMenuPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSMenuPK deleteMenu(urn_com_amalto_xtentis_webservice.WSDeleteMenu wsMenuDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[99]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteMenu"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsMenuDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSMenuPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSMenuPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSMenuPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningCommitItems(urn_com_amalto_xtentis_webservice.WSVersioningCommitItems wsVersioningCommitItems) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[100]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningCommitItems"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningCommitItems});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean versioningRestoreItemByRevision(urn_com_amalto_xtentis_webservice.WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[101]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningRestoreItemByRevision"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningRestoreItemByRevision});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSVersioningItemHistory versioningGetItemHistory(urn_com_amalto_xtentis_webservice.WSVersioningGetItemHistory wsVersioningGetItemHistory) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[102]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningGetItemHistory"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningGetItemHistory});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersioningItemHistory) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersioningItemHistory) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersioningItemHistory.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[] versioningGetItemsVersions(urn_com_amalto_xtentis_webservice.WSVersioningGetItemsVersions wsVersioningGetItemsVersions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[103]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningGetItemsVersions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningGetItemsVersions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString versioningGetItemContent(urn_com_amalto_xtentis_webservice.WSVersioningGetItemContent wsVersioningGetItemContent) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[104]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningGetItemContent"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningGetItemContent});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[] versioningGetObjectsVersions(urn_com_amalto_xtentis_webservice.WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[105]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningGetObjectsVersions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningGetObjectsVersions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[] versioningGetUniverseVersions(urn_com_amalto_xtentis_webservice.WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[106]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningGetUniverseVersions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningGetUniverseVersions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration getVersioningSystemConfiguration(urn_com_amalto_xtentis_webservice.WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[107]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getVersioningSystemConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetVersioningSystemConfiguration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString putVersioningSystemConfiguration(urn_com_amalto_xtentis_webservice.WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[108]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putVersioningSystemConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutVersioningSystemConfiguration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSVersioningInfo versioningGetInfo(urn_com_amalto_xtentis_webservice.WSVersioningGetInfo wsVersioningGetInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[109]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningGetInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningGetInfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSVersioningInfo) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSVersioningInfo) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSVersioningInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningTagObjects(urn_com_amalto_xtentis_webservice.WSVersioningTagObjects wsVersioningTagObjects) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[110]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningTagObjects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningTagObjects});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningTagUniverse(urn_com_amalto_xtentis_webservice.WSVersioningTagUniverse wsVersioningTagUniverse) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[111]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningTagUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningTagUniverse});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningTagItems(urn_com_amalto_xtentis_webservice.WSVersioningTagItems wsVersioningTagItems) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[112]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningTagItems"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningTagItems});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningRestoreObjects(urn_com_amalto_xtentis_webservice.WSVersioningRestoreObjects wsVersioningRestoreObjects) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[113]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningRestoreObjects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningRestoreObjects});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningRestoreUniverse(urn_com_amalto_xtentis_webservice.WSVersioningRestoreUniverse wsVersioningRestoreUniverse) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[114]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningRestoreUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningRestoreUniverse});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK versioningRestoreItems(urn_com_amalto_xtentis_webservice.WSVersioningRestoreItems wsVersioningRestoreItems) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[115]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "versioningRestoreItems"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsVersioningRestoreItems});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[] findBackgroundJobPKs(urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs status) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[116]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "findBackgroundJobPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {status});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJob getBackgroundJob(urn_com_amalto_xtentis_webservice.WSGetBackgroundJob wsGetBackgroundJob) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[117]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getBackgroundJob"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetBackgroundJob});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJob) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJob) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJob.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK putBackgroundJob(urn_com_amalto_xtentis_webservice.WSPutBackgroundJob wsPutBackgroundJob) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[118]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putBackgroundJob"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsPutBackgroundJob});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBackgroundJobPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBackgroundJobPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 getRoutingOrderV2(urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2 wsGetRoutingOrderV2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[119]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRoutingOrderV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetRoutingOrderV2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingOrderV2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 existsRoutingOrderV2(urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[120]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsRoutingOrderV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsRoutingOrder});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingOrderV2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK deleteRoutingOrderV2(urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[121]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteRoutingOrderV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsDeleteRoutingOrder});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[122]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "executeRoutingOrderV2Asynchronously"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExecuteRoutingOrderAsynchronously});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString executeRoutingOrderV2Synchronously(urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[123]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "executeRoutingOrderV2Synchronously"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExecuteRoutingOrderSynchronously});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[] getRoutingOrderV2PKsByCriteria(urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[124]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRoutingOrderV2PKsByCriteria"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetRoutingOrderV2PKsByCriteria});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[] getRoutingOrderV2SByCriteria(urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[125]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRoutingOrderV2sByCriteria"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetRoutingOrderV2SByCriteria});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] routeItemV2(urn_com_amalto_xtentis_webservice.WSRouteItemV2 wsRouteItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[126]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "routeItemV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRouteItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingRulePK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingRulePK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status routingEngineV2Action(urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action wsRoutingEngineAction) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[127]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "routingEngineV2Action"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRoutingEngineAction});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSUniverse getUniverse(urn_com_amalto_xtentis_webservice.WSGetUniverse wsGetUniverse) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[128]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetUniverse});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSUniverse) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSUniverse) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSUniverse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsUniverse(urn_com_amalto_xtentis_webservice.WSExistsUniverse wsExistsUniverse) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[129]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsUniverse});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSUniversePK[] getUniversePKs(urn_com_amalto_xtentis_webservice.WSGetUniversePKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[130]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getUniversePKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSUniversePK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSUniversePK[] getUniverseByRevision(urn_com_amalto_xtentis_webservice.WSGetUniverseByRevision wsUniverseByRevision) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[131]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getUniverseByRevision"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsUniverseByRevision});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSUniversePK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSUniversePK putUniverse(urn_com_amalto_xtentis_webservice.WSPutUniverse wsUniverse) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[132]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsUniverse});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSUniversePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSUniversePK deleteUniverse(urn_com_amalto_xtentis_webservice.WSDeleteUniverse wsUniverseDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[133]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsUniverseDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSUniversePK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSUniversePK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getObjectsForUniverses(java.lang.String[] regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[134]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getObjectsForUniverses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSUniverse getCurrentUniverse(urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse wsGetCurrentUniverse) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[135]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getCurrentUniverse"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetCurrentUniverse});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSUniverse) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSUniverse) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSUniverse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationPlan getSynchronizationPlan(urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlan wsGetSynchronizationPlan) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[136]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSynchronizationPlan"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetSynchronizationPlan});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlan) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlan) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationPlan.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsSynchronizationPlan(urn_com_amalto_xtentis_webservice.WSExistsSynchronizationPlan wsExistsSynchronizationPlan) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[137]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsSynchronizationPlan"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsSynchronizationPlan});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[] getSynchronizationPlanPKs(urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlanPKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[138]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSynchronizationPlanPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK putSynchronizationPlan(urn_com_amalto_xtentis_webservice.WSPutSynchronizationPlan wsSynchronizationPlan) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[139]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putSynchronizationPlan"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationPlan});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK deleteSynchronizationPlan(urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationPlan wsSynchronizationPlanDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[140]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteSynchronizationPlan"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationPlanDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getObjectsForSynchronizationPlans(java.lang.String[] regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[141]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getObjectsForSynchronizationPlans"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getSynchronizationPlanObjectsAlgorithms(java.lang.String[] regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[142]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSynchronizationPlanObjectsAlgorithms"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] getSynchronizationPlanItemsAlgorithms(java.lang.String[] regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[143]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSynchronizationPlanItemsAlgorithms"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus synchronizationPlanAction(urn_com_amalto_xtentis_webservice.WSSynchronizationPlanAction wsSynchronizationPlanAction) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[144]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationPlanAction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationPlanAction});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] synchronizationGetUnsynchronizedObjectsIDs(urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[145]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationGetUnsynchronizedObjectsIDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationGetUnsynchronizedObjectsIDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString synchronizationGetObjectXML(urn_com_amalto_xtentis_webservice.WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[146]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationGetObjectXML"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationGetObjectXML});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString synchronizationPutObjectXML(urn_com_amalto_xtentis_webservice.WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[147]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationPutObjectXML"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationPutObjectXML});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK[] synchronizationGetUnsynchronizedItemPKs(urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[148]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationGetUnsynchronizedItemPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationGetUnsynchronizedItemPKs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSString synchronizationGetItemXML(urn_com_amalto_xtentis_webservice.WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[149]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationGetItemXML"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationGetItemXML});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSString) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSString) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK synchronizationPutItemXML(urn_com_amalto_xtentis_webservice.WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[150]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizationPutItemXML"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationPutItemXML});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationItem getSynchronizationItem(urn_com_amalto_xtentis_webservice.WSGetSynchronizationItem wsGetSynchronizationItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[151]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSynchronizationItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsGetSynchronizationItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationItem) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationItem) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationItem.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsSynchronizationItem(urn_com_amalto_xtentis_webservice.WSExistsSynchronizationItem wsExistsSynchronizationItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[152]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "existsSynchronizationItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsExistsSynchronizationItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSBoolean) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSBoolean.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[][] getSynchronizationItemPKs(urn_com_amalto_xtentis_webservice.WSGetSynchronizationItemPKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[153]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSynchronizationItemPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[][]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[][]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[][].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] putSynchronizationItem(urn_com_amalto_xtentis_webservice.WSPutSynchronizationItem wsSynchronizationItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[154]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "putSynchronizationItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] deleteSynchronizationItem(urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationItem wsSynchronizationItemDelete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[155]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteSynchronizationItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsSynchronizationItemDelete});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSSynchronizationItem resolveSynchronizationItem(urn_com_amalto_xtentis_webservice.WSResolveSynchronizationItem wsResolveSynchronizationItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[156]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "resolveSynchronizationItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsResolveSynchronizationItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationItem) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSSynchronizationItem) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSSynchronizationItem.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK recoverDroppedItem(urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem wsRecoverDroppedItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[157]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "recoverDroppedItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRecoverDroppedItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItemPK[] findAllDroppedItemsPKs(urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs regex) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[158]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "findAllDroppedItemsPKs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {regex});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItemPK[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItemPK[]) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDroppedItemPK[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItem loadDroppedItem(urn_com_amalto_xtentis_webservice.WSLoadDroppedItem wsLoadDroppedItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[159]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "loadDroppedItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsLoadDroppedItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItem) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItem) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDroppedItem.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItemPK removeDroppedItem(urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem wsRemoveDroppedItem) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[160]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "removeDroppedItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {wsRemoveDroppedItem});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItemPK) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSDroppedItemPK) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSDroppedItemPK.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSMDMConfig getMDMConfiguration() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[161]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getMDMConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSMDMConfig) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSMDMConfig) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSMDMConfig.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse checkServiceConfiguration(urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest serviceName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[162]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "checkServiceConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {serviceName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
