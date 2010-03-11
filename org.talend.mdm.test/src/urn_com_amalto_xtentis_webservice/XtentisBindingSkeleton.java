/**
 * XtentisBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package urn_com_amalto_xtentis_webservice;

public class XtentisBindingSkeleton implements urn_com_amalto_xtentis_webservice.XtentisPort, org.apache.axis.wsdl.Skeleton {
    private urn_com_amalto_xtentis_webservice.XtentisPort impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetComponentVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetComponentVersion"), urn_com_amalto_xtentis_webservice.WSGetComponentVersion.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getComponentVersion", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersion"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSVersion"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getComponentVersion"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getComponentVersion") == null) {
            _myOperations.put("getComponentVersion", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getComponentVersion")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPing"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPing"), urn_com_amalto_xtentis_webservice.WSPing.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("ping", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ping"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("ping") == null) {
            _myOperations.put("ping", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("ping")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLogout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLogout"), urn_com_amalto_xtentis_webservice.WSLogout.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("logout", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "logout"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("logout") == null) {
            _myOperations.put("logout", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("logout")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInitData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInitData"), urn_com_amalto_xtentis_webservice.WSInitData.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("initMDM", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "initMDM"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("initMDM") == null) {
            _myOperations.put("initMDM", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("initMDM")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataModelPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataModelPKs"), urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDataModelPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getDataModelPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDataModelPKs") == null) {
            _myOperations.put("getDataModelPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDataModelPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataModel"), urn_com_amalto_xtentis_webservice.WSGetDataModel.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDataModel", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModel"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModel"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getDataModel"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDataModel") == null) {
            _myOperations.put("getDataModel", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDataModel")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataModel"), urn_com_amalto_xtentis_webservice.WSExistsDataModel.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsDataModel", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsDataModel"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsDataModel") == null) {
            _myOperations.put("existsDataModel", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsDataModel")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataModel"), urn_com_amalto_xtentis_webservice.WSPutDataModel.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putDataModel", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putDataModel"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putDataModel") == null) {
            _myOperations.put("putDataModel", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putDataModel")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataModel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataModel"), urn_com_amalto_xtentis_webservice.WSDeleteDataModel.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteDataModel", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataModelPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteDataModel"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteDataModel") == null) {
            _myOperations.put("deleteDataModel", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteDataModel")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckSchema"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckSchema"), urn_com_amalto_xtentis_webservice.WSCheckSchema.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkSchema", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "checkSchema"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkSchema") == null) {
            _myOperations.put("checkSchema", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkSchema")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteBusinessConcept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteBusinessConcept"), urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteBusinessConcept", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteBusinessConcept"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteBusinessConcept") == null) {
            _myOperations.put("deleteBusinessConcept", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteBusinessConcept")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConcepts"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConcepts"), urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBusinessConcepts", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getBusinessConcepts"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBusinessConcepts") == null) {
            _myOperations.put("getBusinessConcepts", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBusinessConcepts")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConcept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConcept"), urn_com_amalto_xtentis_webservice.WSPutBusinessConcept.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putBusinessConcept", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putBusinessConcept"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putBusinessConcept") == null) {
            _myOperations.put("putBusinessConcept", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putBusinessConcept")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConceptSchema"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBusinessConceptSchema"), urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putBusinessConceptSchema", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putBusinessConceptSchema"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putBusinessConceptSchema") == null) {
            _myOperations.put("putBusinessConceptSchema", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putBusinessConceptSchema")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptKey"), urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBusinessConceptKey", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptKey"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptKey"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getBusinessConceptKey"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBusinessConceptKey") == null) {
            _myOperations.put("getBusinessConceptKey", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBusinessConceptKey")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataClusterPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexDataClusterPKs"), urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDataClusterPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getDataClusterPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDataClusterPKs") == null) {
            _myOperations.put("getDataClusterPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDataClusterPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetDataCluster"), urn_com_amalto_xtentis_webservice.WSGetDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataCluster"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataCluster"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDataCluster") == null) {
            _myOperations.put("getDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDataCluster"), urn_com_amalto_xtentis_webservice.WSExistsDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsDataCluster") == null) {
            _myOperations.put("existsDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDBDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsDBDataCluster"), urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsDBDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsDBDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsDBDataCluster") == null) {
            _myOperations.put("existsDBDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsDBDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDataCluster"), urn_com_amalto_xtentis_webservice.WSPutDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putDataCluster") == null) {
            _myOperations.put("putDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDBDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutDBDataCluster"), urn_com_amalto_xtentis_webservice.WSPutDBDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putDBDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putDBDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putDBDataCluster") == null) {
            _myOperations.put("putDBDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putDBDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteDataCluster"), urn_com_amalto_xtentis_webservice.WSDeleteDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteDataCluster") == null) {
            _myOperations.put("deleteDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataCluster"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataCluster"), urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getConceptsInDataCluster", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getConceptsInDataCluster"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getConceptsInDataCluster") == null) {
            _myOperations.put("getConceptsInDataCluster", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getConceptsInDataCluster")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataClusterWithRevisions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetConceptsInDataClusterWithRevisions"), urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getConceptsInDataClusterWithRevisions", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptRevisionMap"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConceptRevisionMap"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getConceptsInDataClusterWithRevisions"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getConceptsInDataClusterWithRevisions") == null) {
            _myOperations.put("getConceptsInDataClusterWithRevisions", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getConceptsInDataClusterWithRevisions")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetViewPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetViewPKs"), urn_com_amalto_xtentis_webservice.WSGetViewPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getViewPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getViewPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getViewPKs") == null) {
            _myOperations.put("getViewPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getViewPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetView"), urn_com_amalto_xtentis_webservice.WSGetView.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getView", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSView"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSView"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getView"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getView") == null) {
            _myOperations.put("getView", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getView")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsView"), urn_com_amalto_xtentis_webservice.WSExistsView.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsView", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsView"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsView") == null) {
            _myOperations.put("existsView", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsView")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutView"), urn_com_amalto_xtentis_webservice.WSPutView.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putView", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putView"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putView") == null) {
            _myOperations.put("putView", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putView")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteView"), urn_com_amalto_xtentis_webservice.WSDeleteView.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteView", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteView"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteView") == null) {
            _myOperations.put("deleteView", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteView")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptValue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBusinessConceptValue"), urn_com_amalto_xtentis_webservice.WSGetBusinessConceptValue.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBusinessConceptValue", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getBusinessConceptValue"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBusinessConceptValue") == null) {
            _myOperations.put("getBusinessConceptValue", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBusinessConceptValue")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetFullPathValues"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetFullPathValues"), urn_com_amalto_xtentis_webservice.WSGetFullPathValues.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getFullPathValues", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getFullPathValues"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getFullPathValues") == null) {
            _myOperations.put("getFullPathValues", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getFullPathValues")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItem"), urn_com_amalto_xtentis_webservice.WSGetItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItem"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItem"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getItem") == null) {
            _myOperations.put("getItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsItem"), urn_com_amalto_xtentis_webservice.WSExistsItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsItem") == null) {
            _myOperations.put("existsItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItems"), urn_com_amalto_xtentis_webservice.WSGetItems.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getItems", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getItems"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getItems") == null) {
            _myOperations.put("getItems", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getItems")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByCriteria"), urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getItemPKsByCriteria", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getItemPKsByCriteria"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getItemPKsByCriteria") == null) {
            _myOperations.put("getItemPKsByCriteria", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getItemPKsByCriteria")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByFullCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemPKsByFullCriteria"), urn_com_amalto_xtentis_webservice.WSGetItemPKsByFullCriteria.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getItemPKsByFullCriteria", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKsByCriteriaResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getItemPKsByFullCriteria"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getItemPKsByFullCriteria") == null) {
            _myOperations.put("getItemPKsByFullCriteria", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getItemPKsByFullCriteria")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewSearch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSViewSearch"), urn_com_amalto_xtentis_webservice.WSViewSearch.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("viewSearch", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "viewSearch"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("viewSearch") == null) {
            _myOperations.put("viewSearch", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("viewSearch")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSXPathsSearch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSXPathsSearch"), urn_com_amalto_xtentis_webservice.WSXPathsSearch.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("xPathsSearch", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "xPathsSearch"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("xPathsSearch") == null) {
            _myOperations.put("xPathsSearch", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("xPathsSearch")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemsPivotIndex"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetItemsPivotIndex"), urn_com_amalto_xtentis_webservice.WSGetItemsPivotIndex.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getItemsPivotIndex", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getItemsPivotIndex"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getItemsPivotIndex") == null) {
            _myOperations.put("getItemsPivotIndex", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getItemsPivotIndex")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetChildrenItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetChildrenItems"), urn_com_amalto_xtentis_webservice.WSGetChildrenItems.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getChildrenItems", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getChildrenItems"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getChildrenItems") == null) {
            _myOperations.put("getChildrenItems", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getChildrenItems")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCount"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCount"), urn_com_amalto_xtentis_webservice.WSCount.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("count", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "count"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("count") == null) {
            _myOperations.put("count", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("count")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSQuickSearch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSQuickSearch"), urn_com_amalto_xtentis_webservice.WSQuickSearch.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("quickSearch", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "quickSearch"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("quickSearch") == null) {
            _myOperations.put("quickSearch", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("quickSearch")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItem"), urn_com_amalto_xtentis_webservice.WSPutItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putItem") == null) {
            _myOperations.put("putItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemArray"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemArray"), urn_com_amalto_xtentis_webservice.WSPutItem[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putItemArray", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putItemArray"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putItemArray") == null) {
            _myOperations.put("putItemArray", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putItemArray")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReportArray"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReportArray"), urn_com_amalto_xtentis_webservice.WSPutItemWithReport[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putItemWithReportArray", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putItemWithReportArray"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putItemWithReportArray") == null) {
            _myOperations.put("putItemWithReportArray", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putItemWithReportArray")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReport"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithReport"), urn_com_amalto_xtentis_webservice.WSPutItemWithReport.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putItemWithReport", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putItemWithReport"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putItemWithReport") == null) {
            _myOperations.put("putItemWithReport", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putItemWithReport")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithCustomReport"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutItemWithCustomReport"), urn_com_amalto_xtentis_webservice.WSPutItemWithCustomReport.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putItemWithCustomReport", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putItemWithCustomReport"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putItemWithCustomReport") == null) {
            _myOperations.put("putItemWithCustomReport", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putItemWithCustomReport")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformer"), urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("extractUsingTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "extractUsingTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("extractUsingTransformer") == null) {
            _myOperations.put("extractUsingTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("extractUsingTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformerThruView"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractUsingTransformerThruView"), urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("extractUsingTransformerThruView", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "extractUsingTransformerThruView"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("extractUsingTransformerThruView") == null) {
            _myOperations.put("extractUsingTransformerThruView", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("extractUsingTransformerThruView")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItem"), urn_com_amalto_xtentis_webservice.WSDeleteItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteItem") == null) {
            _myOperations.put("deleteItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItems"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItems"), urn_com_amalto_xtentis_webservice.WSDeleteItems.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteItems", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSInt"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteItems"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteItems") == null) {
            _myOperations.put("deleteItems", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteItems")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDropItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDropItem"), urn_com_amalto_xtentis_webservice.WSDropItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("dropItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "dropItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("dropItem") == null) {
            _myOperations.put("dropItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("dropItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRunQuery"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRunQuery"), urn_com_amalto_xtentis_webservice.WSRunQuery.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("runQuery", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "runQuery"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("runQuery") == null) {
            _myOperations.put("runQuery", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("runQuery")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteraction"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteraction"), urn_com_amalto_xtentis_webservice.WSConnectorInteraction.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("connectorInteraction", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteractionResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSConnectorInteractionResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "connectorInteraction"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("connectorInteraction") == null) {
            _myOperations.put("connectorInteraction", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("connectorInteraction")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceAction"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceAction"), urn_com_amalto_xtentis_webservice.WSServiceAction.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("serviceAction", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "serviceAction"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("serviceAction") == null) {
            _myOperations.put("serviceAction", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("serviceAction")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetConfiguration"), urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceConfiguration", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getServiceConfiguration"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceConfiguration") == null) {
            _myOperations.put("getServiceConfiguration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceConfiguration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicePutConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicePutConfiguration"), urn_com_amalto_xtentis_webservice.WSServicePutConfiguration.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putServiceConfiguration", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putServiceConfiguration"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putServiceConfiguration") == null) {
            _myOperations.put("putServiceConfiguration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putServiceConfiguration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetServicesList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetServicesList"), urn_com_amalto_xtentis_webservice.WSGetServicesList.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServicesList", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicesList"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServicesList"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getServicesList"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServicesList") == null) {
            _myOperations.put("getServicesList", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServicesList")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"), urn_com_amalto_xtentis_webservice.WSString.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getServiceDocument", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetDocument"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSServiceGetDocument"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getServiceDocument"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getServiceDocument") == null) {
            _myOperations.put("getServiceDocument", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getServiceDocument")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetStoredProcedure"), urn_com_amalto_xtentis_webservice.WSGetStoredProcedure.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStoredProcedure", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedure"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedure"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getStoredProcedure"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStoredProcedure") == null) {
            _myOperations.put("getStoredProcedure", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStoredProcedure")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsStoredProcedure"), urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsStoredProcedure", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsStoredProcedure"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsStoredProcedure") == null) {
            _myOperations.put("existsStoredProcedure", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsStoredProcedure")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRegexStoredProcedure"), urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStoredProcedurePKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getStoredProcedurePKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStoredProcedurePKs") == null) {
            _myOperations.put("getStoredProcedurePKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStoredProcedurePKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutStoredProcedure"), urn_com_amalto_xtentis_webservice.WSPutStoredProcedure.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putStoredProcedure", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putStoredProcedure"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putStoredProcedure") == null) {
            _myOperations.put("putStoredProcedure", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putStoredProcedure")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteStoredProcedure"), urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteStoredProcedure", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStoredProcedurePK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteStoredProcedure"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteStoredProcedure") == null) {
            _myOperations.put("deleteStoredProcedure", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteStoredProcedure")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteStoredProcedure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteStoredProcedure"), urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("executeStoredProcedure", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSStringArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "executeStoredProcedure"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("executeStoredProcedure") == null) {
            _myOperations.put("executeStoredProcedure", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("executeStoredProcedure")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformer"), urn_com_amalto_xtentis_webservice.WSGetTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformer"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformer"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformer") == null) {
            _myOperations.put("getTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformer"), urn_com_amalto_xtentis_webservice.WSExistsTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsTransformer") == null) {
            _myOperations.put("existsTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPKs"), urn_com_amalto_xtentis_webservice.WSGetTransformerPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformerPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformerPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformerPKs") == null) {
            _myOperations.put("getTransformerPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformerPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformer"), urn_com_amalto_xtentis_webservice.WSPutTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putTransformer") == null) {
            _myOperations.put("putTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformer"), urn_com_amalto_xtentis_webservice.WSDeleteTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteTransformer") == null) {
            _myOperations.put("deleteTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformer"), urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("processBytesUsingTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "processBytesUsingTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("processBytesUsingTransformer") == null) {
            _myOperations.put("processBytesUsingTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("processBytesUsingTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformer"), urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("processFileUsingTransformer", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPipeline"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "processFileUsingTransformer"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("processFileUsingTransformer") == null) {
            _myOperations.put("processFileUsingTransformer", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("processFileUsingTransformer")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformerAsBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessBytesUsingTransformerAsBackgroundJob"), urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("processBytesUsingTransformerAsBackgroundJob", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "processBytesUsingTransformerAsBackgroundJob"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("processBytesUsingTransformerAsBackgroundJob") == null) {
            _myOperations.put("processBytesUsingTransformerAsBackgroundJob", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("processBytesUsingTransformerAsBackgroundJob")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformerAsBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSProcessFileUsingTransformerAsBackgroundJob"), urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("processFileUsingTransformerAsBackgroundJob", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "processFileUsingTransformerAsBackgroundJob"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("processFileUsingTransformerAsBackgroundJob") == null) {
            _myOperations.put("processFileUsingTransformerAsBackgroundJob", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("processFileUsingTransformerAsBackgroundJob")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenu"), urn_com_amalto_xtentis_webservice.WSGetMenu.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMenu", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenu"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenu"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getMenu"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMenu") == null) {
            _myOperations.put("getMenu", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMenu")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsMenu"), urn_com_amalto_xtentis_webservice.WSExistsMenu.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsMenu", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsMenu"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsMenu") == null) {
            _myOperations.put("existsMenu", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsMenu")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenuPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetMenuPKs"), urn_com_amalto_xtentis_webservice.WSGetMenuPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMenuPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getMenuPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMenuPKs") == null) {
            _myOperations.put("getMenuPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMenuPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutMenu"), urn_com_amalto_xtentis_webservice.WSPutMenu.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putMenu", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putMenu"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putMenu") == null) {
            _myOperations.put("putMenu", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putMenu")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteMenu"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteMenu"), urn_com_amalto_xtentis_webservice.WSDeleteMenu.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteMenu", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMenuPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteMenu"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteMenu") == null) {
            _myOperations.put("deleteMenu", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteMenu")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindBackgroundJobPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindBackgroundJobPKs"), urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("findBackgroundJobPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "findBackgroundJobPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("findBackgroundJobPKs") == null) {
            _myOperations.put("findBackgroundJobPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("findBackgroundJobPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetBackgroundJob"), urn_com_amalto_xtentis_webservice.WSGetBackgroundJob.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBackgroundJob", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJob"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJob"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getBackgroundJob"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBackgroundJob") == null) {
            _myOperations.put("getBackgroundJob", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBackgroundJob")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBackgroundJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutBackgroundJob"), urn_com_amalto_xtentis_webservice.WSPutBackgroundJob.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putBackgroundJob", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putBackgroundJob"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putBackgroundJob") == null) {
            _myOperations.put("putBackgroundJob", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putBackgroundJob")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetCurrentUniverse"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetCurrentUniverse"), urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCurrentUniverse", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSUniverse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getCurrentUniverse"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCurrentUniverse") == null) {
            _myOperations.put("getCurrentUniverse", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCurrentUniverse")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRecoverDroppedItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRecoverDroppedItem"), urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("recoverDroppedItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "recoverDroppedItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("recoverDroppedItem") == null) {
            _myOperations.put("recoverDroppedItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("recoverDroppedItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindAllDroppedItemsPKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSFindAllDroppedItemsPKs"), urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("findAllDroppedItemsPKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "findAllDroppedItemsPKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("findAllDroppedItemsPKs") == null) {
            _myOperations.put("findAllDroppedItemsPKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("findAllDroppedItemsPKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLoadDroppedItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSLoadDroppedItem"), urn_com_amalto_xtentis_webservice.WSLoadDroppedItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("loadDroppedItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItem"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItem"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "loadDroppedItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("loadDroppedItem") == null) {
            _myOperations.put("loadDroppedItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("loadDroppedItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRemoveDroppedItem"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRemoveDroppedItem"), urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("removeDroppedItem", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDroppedItemPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "removeDroppedItem"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("removeDroppedItem") == null) {
            _myOperations.put("removeDroppedItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("removeDroppedItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getMDMConfiguration", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMDMConfig"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSMDMConfig"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getMDMConfiguration"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMDMConfiguration") == null) {
            _myOperations.put("getMDMConfiguration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMDMConfiguration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigRequest"), urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkServiceConfiguration", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCheckServiceConfigResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "checkServiceConfiguration"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkServiceConfiguration") == null) {
            _myOperations.put("checkServiceConfiguration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkServiceConfiguration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRulePKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRulePKs"), urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getRoutingRulePKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getRoutingRulePKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getRoutingRulePKs") == null) {
            _myOperations.put("getRoutingRulePKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getRoutingRulePKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingRule"), urn_com_amalto_xtentis_webservice.WSGetRoutingRule.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getRoutingRule", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRule"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRule"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getRoutingRule"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getRoutingRule") == null) {
            _myOperations.put("getRoutingRule", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getRoutingRule")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingRule"), urn_com_amalto_xtentis_webservice.WSExistsRoutingRule.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsRoutingRule", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsRoutingRule"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsRoutingRule") == null) {
            _myOperations.put("existsRoutingRule", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsRoutingRule")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutRoutingRule"), urn_com_amalto_xtentis_webservice.WSPutRoutingRule.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putRoutingRule", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putRoutingRule"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putRoutingRule") == null) {
            _myOperations.put("putRoutingRule", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putRoutingRule")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingRule"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingRule"), urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteRoutingRule", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteRoutingRule"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteRoutingRule") == null) {
            _myOperations.put("deleteRoutingRule", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteRoutingRule")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2"), urn_com_amalto_xtentis_webservice.WSGetTransformerV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformerV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformerV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformerV2") == null) {
            _myOperations.put("getTransformerV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformerV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerV2"), urn_com_amalto_xtentis_webservice.WSExistsTransformerV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsTransformerV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsTransformerV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsTransformerV2") == null) {
            _myOperations.put("existsTransformerV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsTransformerV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2PKs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerV2PKs"), urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformerV2PKs", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformerV2PKs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformerV2PKs") == null) {
            _myOperations.put("getTransformerV2PKs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformerV2PKs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSPutTransformerV2"), urn_com_amalto_xtentis_webservice.WSPutTransformerV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putTransformerV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putTransformerV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putTransformerV2") == null) {
            _myOperations.put("putTransformerV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putTransformerV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteTransformerV2"), urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteTransformerV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerV2PK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteTransformerV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteTransformerV2") == null) {
            _myOperations.put("deleteTransformerV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteTransformerV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2"), urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("executeTransformerV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "executeTransformerV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("executeTransformerV2") == null) {
            _myOperations.put("executeTransformerV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("executeTransformerV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2AsJob"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteTransformerV2AsJob"), urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("executeTransformerV2AsJob", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBackgroundJobPK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "executeTransformerV2AsJob"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("executeTransformerV2AsJob") == null) {
            _myOperations.put("executeTransformerV2AsJob", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("executeTransformerV2AsJob")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractThroughTransformerV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExtractThroughTransformerV2"), urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("extractThroughTransformerV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerContext"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "extractThroughTransformerV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("extractThroughTransformerV2") == null) {
            _myOperations.put("extractThroughTransformerV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("extractThroughTransformerV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerPluginV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsTransformerPluginV2"), urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsTransformerPluginV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSBoolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsTransformerPluginV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsTransformerPluginV2") == null) {
            _myOperations.put("existsTransformerPluginV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsTransformerPluginV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2GetConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2GetConfiguration"), urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformerPluginV2Configuration", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformerPluginV2Configuration"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformerPluginV2Configuration") == null) {
            _myOperations.put("getTransformerPluginV2Configuration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformerPluginV2Configuration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2PutConfiguration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2PutConfiguration"), urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("putTransformerPluginV2Configuration", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "putTransformerPluginV2Configuration"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("putTransformerPluginV2Configuration") == null) {
            _myOperations.put("putTransformerPluginV2Configuration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("putTransformerPluginV2Configuration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2Details"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2Details"), urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformerPluginV2Details", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2Details"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2Details"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformerPluginV2Details"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformerPluginV2Details") == null) {
            _myOperations.put("getTransformerPluginV2Details", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformerPluginV2Details")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2sList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetTransformerPluginV2sList"), urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTransformerPluginV2SList", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2sList"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSTransformerPluginV2sList"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getTransformerPluginV2sList"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTransformerPluginV2SList") == null) {
            _myOperations.put("getTransformerPluginV2SList", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTransformerPluginV2SList")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2"), urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getRoutingOrderV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getRoutingOrderV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getRoutingOrderV2") == null) {
            _myOperations.put("getRoutingOrderV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getRoutingOrderV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingOrderV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExistsRoutingOrderV2"), urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("existsRoutingOrderV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "existsRoutingOrderV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("existsRoutingOrderV2") == null) {
            _myOperations.put("existsRoutingOrderV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("existsRoutingOrderV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingOrderV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteRoutingOrderV2"), urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteRoutingOrderV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "deleteRoutingOrderV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteRoutingOrderV2") == null) {
            _myOperations.put("deleteRoutingOrderV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteRoutingOrderV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Asynchronously"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Asynchronously"), urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("executeRoutingOrderV2Asynchronously", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PK"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "executeRoutingOrderV2Asynchronously"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("executeRoutingOrderV2Asynchronously") == null) {
            _myOperations.put("executeRoutingOrderV2Asynchronously", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("executeRoutingOrderV2Asynchronously")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Synchronously"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSExecuteRoutingOrderV2Synchronously"), urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("executeRoutingOrderV2Synchronously", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSString"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", ">WSString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "executeRoutingOrderV2Synchronously"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("executeRoutingOrderV2Synchronously") == null) {
            _myOperations.put("executeRoutingOrderV2Synchronously", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("executeRoutingOrderV2Synchronously")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2PKsByCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2PKsByCriteria"), urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getRoutingOrderV2PKsByCriteria", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2PKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getRoutingOrderV2PKsByCriteria"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getRoutingOrderV2PKsByCriteria") == null) {
            _myOperations.put("getRoutingOrderV2PKsByCriteria", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getRoutingOrderV2PKsByCriteria")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2sByCriteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSGetRoutingOrderV2sByCriteria"), urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getRoutingOrderV2SByCriteria", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2Array"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingOrderV2Array"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getRoutingOrderV2sByCriteria"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getRoutingOrderV2SByCriteria") == null) {
            _myOperations.put("getRoutingOrderV2SByCriteria", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getRoutingOrderV2SByCriteria")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRouteItemV2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRouteItemV2"), urn_com_amalto_xtentis_webservice.WSRouteItemV2.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("routeItemV2", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingRulePKArray"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "routeItemV2"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("routeItemV2") == null) {
            _myOperations.put("routeItemV2", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("routeItemV2")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Action"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Action"), urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("routingEngineV2Action", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Status"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSRoutingEngineV2Status"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "routingEngineV2Action"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("routingEngineV2Action") == null) {
            _myOperations.put("routingEngineV2Action", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("routingEngineV2Action")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCategoryData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCategoryData"), urn_com_amalto_xtentis_webservice.WSCategoryData.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMDMCategory", _params, new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCategoryData"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSCategoryData"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getMDMCategory"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMDMCategory") == null) {
            _myOperations.put("getMDMCategory", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMDMCategory")).add(_oper);
    }

    public XtentisBindingSkeleton() {
        this.impl = new urn_com_amalto_xtentis_webservice.XtentisBindingImpl();
    }

    public XtentisBindingSkeleton(urn_com_amalto_xtentis_webservice.XtentisPort impl) {
        this.impl = impl;
    }
    public urn_com_amalto_xtentis_webservice.WSVersion getComponentVersion(urn_com_amalto_xtentis_webservice.WSGetComponentVersion wsGetComponentVersion) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSVersion ret = impl.getComponentVersion(wsGetComponentVersion);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString ping(urn_com_amalto_xtentis_webservice.WSPing wsPing) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.ping(wsPing);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString logout(urn_com_amalto_xtentis_webservice.WSLogout wsLogout) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.logout(wsLogout);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSInt initMDM(urn_com_amalto_xtentis_webservice.WSInitData initData) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSInt ret = impl.initMDM(initData);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataModelPK[] getDataModelPKs(urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs regexp) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataModelPK[] ret = impl.getDataModelPKs(regexp);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataModel getDataModel(urn_com_amalto_xtentis_webservice.WSGetDataModel wsDataModelget) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataModel ret = impl.getDataModel(wsDataModelget);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsDataModel(urn_com_amalto_xtentis_webservice.WSExistsDataModel wsDataModelExists) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsDataModel(wsDataModelExists);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataModelPK putDataModel(urn_com_amalto_xtentis_webservice.WSPutDataModel wsDataModel) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataModelPK ret = impl.putDataModel(wsDataModel);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataModelPK deleteDataModel(urn_com_amalto_xtentis_webservice.WSDeleteDataModel wsDeleteDataModel) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataModelPK ret = impl.deleteDataModel(wsDeleteDataModel);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString checkSchema(urn_com_amalto_xtentis_webservice.WSCheckSchema wsSchema) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.checkSchema(wsSchema);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString deleteBusinessConcept(urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept wsDeleteBusinessConcept) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.deleteBusinessConcept(wsDeleteBusinessConcept);
        return ret;
    }

    public java.lang.String[] getBusinessConcepts(urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts wsGetBusinessConcepts) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.getBusinessConcepts(wsGetBusinessConcepts);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString putBusinessConcept(urn_com_amalto_xtentis_webservice.WSPutBusinessConcept wsPutBusinessConcept) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.putBusinessConcept(wsPutBusinessConcept);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString putBusinessConceptSchema(urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.putBusinessConceptSchema(wsPutBusinessConceptSchema);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSConceptKey getBusinessConceptKey(urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey wsGetBusinessConceptKey) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSConceptKey ret = impl.getBusinessConceptKey(wsGetBusinessConceptKey);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataClusterPK[] getDataClusterPKs(urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs regexp) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataClusterPK[] ret = impl.getDataClusterPKs(regexp);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataCluster getDataCluster(urn_com_amalto_xtentis_webservice.WSGetDataCluster wsDataClusterPK) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataCluster ret = impl.getDataCluster(wsDataClusterPK);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsDataCluster(urn_com_amalto_xtentis_webservice.WSExistsDataCluster wsExistsDataCluster) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsDataCluster(wsExistsDataCluster);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsDBDataCluster(urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster wsExistsDBDataCluster) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsDBDataCluster(wsExistsDBDataCluster);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataClusterPK putDataCluster(urn_com_amalto_xtentis_webservice.WSPutDataCluster wsDataCluster) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataClusterPK ret = impl.putDataCluster(wsDataCluster);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean putDBDataCluster(urn_com_amalto_xtentis_webservice.WSPutDBDataCluster wsDataCluster) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.putDBDataCluster(wsDataCluster);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDataClusterPK deleteDataCluster(urn_com_amalto_xtentis_webservice.WSDeleteDataCluster wsDeleteDataCluster) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDataClusterPK ret = impl.deleteDataCluster(wsDeleteDataCluster);
        return ret;
    }

    public java.lang.String[] getConceptsInDataCluster(urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.getConceptsInDataCluster(wsGetConceptsInDataCluster);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[] getConceptsInDataClusterWithRevisions(urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[] ret = impl.getConceptsInDataClusterWithRevisions(wsGetConceptsInDataClusterWithRevisions);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSViewPK[] getViewPKs(urn_com_amalto_xtentis_webservice.WSGetViewPKs regexp) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSViewPK[] ret = impl.getViewPKs(regexp);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSView getView(urn_com_amalto_xtentis_webservice.WSGetView wsViewPK) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSView ret = impl.getView(wsViewPK);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsView(urn_com_amalto_xtentis_webservice.WSExistsView wsViewPK) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsView(wsViewPK);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSViewPK putView(urn_com_amalto_xtentis_webservice.WSPutView wsView) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSViewPK ret = impl.putView(wsView);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSViewPK deleteView(urn_com_amalto_xtentis_webservice.WSDeleteView wsViewDel) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSViewPK ret = impl.deleteView(wsViewDel);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString getBusinessConceptValue(urn_com_amalto_xtentis_webservice.WSGetBusinessConceptValue wsGetBusinessConceptValue) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.getBusinessConceptValue(wsGetBusinessConceptValue);
        return ret;
    }

    public java.lang.String[] getFullPathValues(urn_com_amalto_xtentis_webservice.WSGetFullPathValues wsGetFullPathValues) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.getFullPathValues(wsGetFullPathValues);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItem getItem(urn_com_amalto_xtentis_webservice.WSGetItem wsGetItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItem ret = impl.getItem(wsGetItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsItem(urn_com_amalto_xtentis_webservice.WSExistsItem wsExistsItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsItem(wsExistsItem);
        return ret;
    }

    public java.lang.String[] getItems(urn_com_amalto_xtentis_webservice.WSGetItems wsGetItems) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.getItems(wsGetItems);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[] getItemPKsByCriteria(urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[] ret = impl.getItemPKsByCriteria(wsGetItemPKsByCriteria);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[] getItemPKsByFullCriteria(urn_com_amalto_xtentis_webservice.WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[] ret = impl.getItemPKsByFullCriteria(wsGetItemPKsByFullCriteria);
        return ret;
    }

    public java.lang.String[] viewSearch(urn_com_amalto_xtentis_webservice.WSViewSearch wsViewSearch) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.viewSearch(wsViewSearch);
        return ret;
    }

    public java.lang.String[] xPathsSearch(urn_com_amalto_xtentis_webservice.WSXPathsSearch wsXPathsSearch) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.xPathsSearch(wsXPathsSearch);
        return ret;
    }

    public java.lang.String[] getItemsPivotIndex(urn_com_amalto_xtentis_webservice.WSGetItemsPivotIndex wsGetItemsPivotIndex) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.getItemsPivotIndex(wsGetItemsPivotIndex);
        return ret;
    }

    public java.lang.String[] getChildrenItems(urn_com_amalto_xtentis_webservice.WSGetChildrenItems wsGetChildrenItems) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.getChildrenItems(wsGetChildrenItems);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString count(urn_com_amalto_xtentis_webservice.WSCount wsCount) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.count(wsCount);
        return ret;
    }

    public java.lang.String[] quickSearch(urn_com_amalto_xtentis_webservice.WSQuickSearch wsQuickSearch) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.quickSearch(wsQuickSearch);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK putItem(urn_com_amalto_xtentis_webservice.WSPutItem wsPutItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK ret = impl.putItem(wsPutItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK[] putItemArray(urn_com_amalto_xtentis_webservice.WSPutItem[] wsPutItemArray) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK[] ret = impl.putItemArray(wsPutItemArray);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK[] putItemWithReportArray(urn_com_amalto_xtentis_webservice.WSPutItemWithReport[] wsPutItemWithReportArray) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK[] ret = impl.putItemWithReportArray(wsPutItemWithReportArray);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK putItemWithReport(urn_com_amalto_xtentis_webservice.WSPutItemWithReport wsPutItemWithReport) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK ret = impl.putItemWithReport(wsPutItemWithReport);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK putItemWithCustomReport(urn_com_amalto_xtentis_webservice.WSPutItemWithCustomReport wsPutItemWithCustomReport) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK ret = impl.putItemWithCustomReport(wsPutItemWithCustomReport);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] extractUsingTransformer(urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer wsExtractUsingTransformer) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] ret = impl.extractUsingTransformer(wsExtractUsingTransformer);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] extractUsingTransformerThruView(urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] ret = impl.extractUsingTransformerThruView(wsExtractUsingTransformerThruView);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK deleteItem(urn_com_amalto_xtentis_webservice.WSDeleteItem wsDeleteItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK ret = impl.deleteItem(wsDeleteItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSInt deleteItems(urn_com_amalto_xtentis_webservice.WSDeleteItems wsDeleteItems) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSInt ret = impl.deleteItems(wsDeleteItems);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItemPK dropItem(urn_com_amalto_xtentis_webservice.WSDropItem wsDropItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDroppedItemPK ret = impl.dropItem(wsDropItem);
        return ret;
    }

    public java.lang.String[] runQuery(urn_com_amalto_xtentis_webservice.WSRunQuery wsRunQuery) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.runQuery(wsRunQuery);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse connectorInteraction(urn_com_amalto_xtentis_webservice.WSConnectorInteraction wsConnectorInteraction) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse ret = impl.connectorInteraction(wsConnectorInteraction);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString serviceAction(urn_com_amalto_xtentis_webservice.WSServiceAction wsServiceAction) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.serviceAction(wsServiceAction);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString getServiceConfiguration(urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration wsGetConfiguration) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.getServiceConfiguration(wsGetConfiguration);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString putServiceConfiguration(urn_com_amalto_xtentis_webservice.WSServicePutConfiguration wsPutConfiguration) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.putServiceConfiguration(wsPutConfiguration);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSServicesListItem[] getServicesList(urn_com_amalto_xtentis_webservice.WSGetServicesList wsGetServicesList) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSServicesListItem[] ret = impl.getServicesList(wsGetServicesList);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSServiceGetDocument getServiceDocument(urn_com_amalto_xtentis_webservice.WSString serviceName) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSServiceGetDocument ret = impl.getServiceDocument(serviceName);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedure getStoredProcedure(urn_com_amalto_xtentis_webservice.WSGetStoredProcedure wsGetStoredProcedure) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSStoredProcedure ret = impl.getStoredProcedure(wsGetStoredProcedure);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsStoredProcedure(urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure wsExistsStoredProcedure) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsStoredProcedure(wsExistsStoredProcedure);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[] getStoredProcedurePKs(urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure regex) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[] ret = impl.getStoredProcedurePKs(regex);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedurePK putStoredProcedure(urn_com_amalto_xtentis_webservice.WSPutStoredProcedure wsStoredProcedure) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSStoredProcedurePK ret = impl.putStoredProcedure(wsStoredProcedure);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSStoredProcedurePK deleteStoredProcedure(urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure wsStoredProcedureDelete) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSStoredProcedurePK ret = impl.deleteStoredProcedure(wsStoredProcedureDelete);
        return ret;
    }

    public java.lang.String[] executeStoredProcedure(urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure wsExecuteStoredProcedure) throws java.rmi.RemoteException
    {
        java.lang.String[] ret = impl.executeStoredProcedure(wsExecuteStoredProcedure);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformer getTransformer(urn_com_amalto_xtentis_webservice.WSGetTransformer wsGetTransformer) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformer ret = impl.getTransformer(wsGetTransformer);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsTransformer(urn_com_amalto_xtentis_webservice.WSExistsTransformer wsExistsTransformer) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsTransformer(wsExistsTransformer);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPK[] getTransformerPKs(urn_com_amalto_xtentis_webservice.WSGetTransformerPKs regex) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerPK[] ret = impl.getTransformerPKs(regex);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPK putTransformer(urn_com_amalto_xtentis_webservice.WSPutTransformer wsTransformer) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerPK ret = impl.putTransformer(wsTransformer);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPK deleteTransformer(urn_com_amalto_xtentis_webservice.WSDeleteTransformer wsTransformerDelete) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerPK ret = impl.deleteTransformer(wsTransformerDelete);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] processBytesUsingTransformer(urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer wsProcessBytesUsingTransformer) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] ret = impl.processBytesUsingTransformer(wsProcessBytesUsingTransformer);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] processFileUsingTransformer(urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer wsProcessFileUsingTransformer) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] ret = impl.processFileUsingTransformer(wsProcessFileUsingTransformer);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK ret = impl.processBytesUsingTransformerAsBackgroundJob(wsProcessBytesUsingTransformerAsBackgroundJob);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK ret = impl.processFileUsingTransformerAsBackgroundJob(wsProcessFileUsingTransformerAsBackgroundJob);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSMenu getMenu(urn_com_amalto_xtentis_webservice.WSGetMenu wsGetMenu) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSMenu ret = impl.getMenu(wsGetMenu);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsMenu(urn_com_amalto_xtentis_webservice.WSExistsMenu wsExistsMenu) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsMenu(wsExistsMenu);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSMenuPK[] getMenuPKs(urn_com_amalto_xtentis_webservice.WSGetMenuPKs regex) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSMenuPK[] ret = impl.getMenuPKs(regex);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSMenuPK putMenu(urn_com_amalto_xtentis_webservice.WSPutMenu wsMenu) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSMenuPK ret = impl.putMenu(wsMenu);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSMenuPK deleteMenu(urn_com_amalto_xtentis_webservice.WSDeleteMenu wsMenuDelete) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSMenuPK ret = impl.deleteMenu(wsMenuDelete);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[] findBackgroundJobPKs(urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs status) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[] ret = impl.findBackgroundJobPKs(status);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJob getBackgroundJob(urn_com_amalto_xtentis_webservice.WSGetBackgroundJob wsGetBackgroundJob) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBackgroundJob ret = impl.getBackgroundJob(wsGetBackgroundJob);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK putBackgroundJob(urn_com_amalto_xtentis_webservice.WSPutBackgroundJob wsPutBackgroundJob) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK ret = impl.putBackgroundJob(wsPutBackgroundJob);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSUniverse getCurrentUniverse(urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse wsGetCurrentUniverse) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSUniverse ret = impl.getCurrentUniverse(wsGetCurrentUniverse);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSItemPK recoverDroppedItem(urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem wsRecoverDroppedItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSItemPK ret = impl.recoverDroppedItem(wsRecoverDroppedItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItemPK[] findAllDroppedItemsPKs(urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs regex) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDroppedItemPK[] ret = impl.findAllDroppedItemsPKs(regex);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItem loadDroppedItem(urn_com_amalto_xtentis_webservice.WSLoadDroppedItem wsLoadDroppedItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDroppedItem ret = impl.loadDroppedItem(wsLoadDroppedItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSDroppedItemPK removeDroppedItem(urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem wsRemoveDroppedItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSDroppedItemPK ret = impl.removeDroppedItem(wsRemoveDroppedItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSMDMConfig getMDMConfiguration() throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSMDMConfig ret = impl.getMDMConfiguration();
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse checkServiceConfiguration(urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest serviceName) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse ret = impl.checkServiceConfiguration(serviceName);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] getRoutingRulePKs(urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs regexp) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] ret = impl.getRoutingRulePKs(regexp);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRule getRoutingRule(urn_com_amalto_xtentis_webservice.WSGetRoutingRule wsRoutingRulePK) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingRule ret = impl.getRoutingRule(wsRoutingRulePK);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsRoutingRule(urn_com_amalto_xtentis_webservice.WSExistsRoutingRule wsExistsRoutingRule) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsRoutingRule(wsExistsRoutingRule);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK putRoutingRule(urn_com_amalto_xtentis_webservice.WSPutRoutingRule wsRoutingRule) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK ret = impl.putRoutingRule(wsRoutingRule);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK deleteRoutingRule(urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule wsRoutingRuleDel) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK ret = impl.deleteRoutingRule(wsRoutingRuleDel);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2 getTransformerV2(urn_com_amalto_xtentis_webservice.WSGetTransformerV2 wsGetTransformerV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerV2 ret = impl.getTransformerV2(wsGetTransformerV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsTransformerV2(urn_com_amalto_xtentis_webservice.WSExistsTransformerV2 wsExistsTransformerV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsTransformerV2(wsExistsTransformerV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2PK[] getTransformerV2PKs(urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs regex) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerV2PK[] ret = impl.getTransformerV2PKs(regex);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2PK putTransformerV2(urn_com_amalto_xtentis_webservice.WSPutTransformerV2 wsTransformerV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerV2PK ret = impl.putTransformerV2(wsTransformerV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerV2PK deleteTransformerV2(urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2 wsDeleteTransformerV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerV2PK ret = impl.deleteTransformerV2(wsDeleteTransformerV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerContext executeTransformerV2(urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2 wsExecuteTransformerV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerContext ret = impl.executeTransformerV2(wsExecuteTransformerV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBackgroundJobPK executeTransformerV2AsJob(urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK ret = impl.executeTransformerV2AsJob(wsExecuteTransformerV2AsJob);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerContext extractThroughTransformerV2(urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerContext ret = impl.extractThroughTransformerV2(wsExtractThroughTransformerV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSBoolean existsTransformerPluginV2(urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2 wsExistsTransformerPluginV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSBoolean ret = impl.existsTransformerPluginV2(wsExistsTransformerPluginV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString getTransformerPluginV2Configuration(urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.getTransformerPluginV2Configuration(wsGetConfiguration);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString putTransformerPluginV2Configuration(urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.putTransformerPluginV2Configuration(wsPutConfiguration);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details getTransformerPluginV2Details(urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details wsGetTransformerPluginV2Details) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details ret = impl.getTransformerPluginV2Details(wsGetTransformerPluginV2Details);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[] getTransformerPluginV2SList(urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList wsGetTransformerPluginV2SList) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[] ret = impl.getTransformerPluginV2SList(wsGetTransformerPluginV2SList);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 getRoutingOrderV2(urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2 wsGetRoutingOrderV2) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 ret = impl.getRoutingOrderV2(wsGetRoutingOrderV2);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 existsRoutingOrderV2(urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 ret = impl.existsRoutingOrderV2(wsExistsRoutingOrder);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK deleteRoutingOrderV2(urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK ret = impl.deleteRoutingOrderV2(wsDeleteRoutingOrder);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK ret = impl.executeRoutingOrderV2Asynchronously(wsExecuteRoutingOrderAsynchronously);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSString executeRoutingOrderV2Synchronously(urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSString ret = impl.executeRoutingOrderV2Synchronously(wsExecuteRoutingOrderSynchronously);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[] getRoutingOrderV2PKsByCriteria(urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[] ret = impl.getRoutingOrderV2PKsByCriteria(wsGetRoutingOrderV2PKsByCriteria);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[] getRoutingOrderV2SByCriteria(urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[] ret = impl.getRoutingOrderV2SByCriteria(wsGetRoutingOrderV2SByCriteria);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] routeItemV2(urn_com_amalto_xtentis_webservice.WSRouteItemV2 wsRouteItem) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] ret = impl.routeItemV2(wsRouteItem);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status routingEngineV2Action(urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action wsRoutingEngineAction) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status ret = impl.routingEngineV2Action(wsRoutingEngineAction);
        return ret;
    }

    public urn_com_amalto_xtentis_webservice.WSCategoryData getMDMCategory(urn_com_amalto_xtentis_webservice.WSCategoryData wsCategoryDataRequest) throws java.rmi.RemoteException
    {
        urn_com_amalto_xtentis_webservice.WSCategoryData ret = impl.getMDMCategory(wsCategoryDataRequest);
        return ret;
    }

}
