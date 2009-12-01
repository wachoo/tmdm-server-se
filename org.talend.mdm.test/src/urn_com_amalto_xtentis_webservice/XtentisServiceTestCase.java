/**
 * XtentisServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package urn_com_amalto_xtentis_webservice;

public class XtentisServiceTestCase extends junit.framework.TestCase {
    public XtentisServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testXtentisPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1XtentisPortGetComponentVersion() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersion value = null;
        value = binding.getComponentVersion(new urn_com_amalto_xtentis_webservice.WSGetComponentVersion());
        // TBD - validate results
    }

    public void test2XtentisPortPing() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.ping(new urn_com_amalto_xtentis_webservice.WSPing());
        // TBD - validate results
    }

    public void test3XtentisPortLogout() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.logout(new urn_com_amalto_xtentis_webservice.WSLogout());
        // TBD - validate results
    }

    public void test4XtentisPortInitMDM() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSInt value = null;
        value = binding.initMDM(new urn_com_amalto_xtentis_webservice.WSInitData());
        // TBD - validate results
    }

    public void test5XtentisPortGetDataModelPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataModelPK[] value = null;
        value = binding.getDataModelPKs(new urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs());
        // TBD - validate results
    }

    public void test6XtentisPortExistsDataModel() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsDataModel(new urn_com_amalto_xtentis_webservice.WSExistsDataModel());
        // TBD - validate results
    }

    public void test7XtentisPortGetDataModel() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataModel value = null;
        value = binding.getDataModel(new urn_com_amalto_xtentis_webservice.WSGetDataModel());
        // TBD - validate results
    }

    public void test8XtentisPortPutDataModel() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataModelPK value = null;
        value = binding.putDataModel(new urn_com_amalto_xtentis_webservice.WSPutDataModel());
        // TBD - validate results
    }

    public void test9XtentisPortDeleteDataModel() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataModelPK value = null;
        value = binding.deleteDataModel(new urn_com_amalto_xtentis_webservice.WSDeleteDataModel());
        // TBD - validate results
    }

    public void test10XtentisPortCheckSchema() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.checkSchema(new urn_com_amalto_xtentis_webservice.WSCheckSchema());
        // TBD - validate results
    }

    public void test11XtentisPortDeleteBusinessConcept() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.deleteBusinessConcept(new urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept());
        // TBD - validate results
    }

    public void test12XtentisPortPutBusinessConceptSchema() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.putBusinessConceptSchema(new urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema());
        // TBD - validate results
    }

    public void test13XtentisPortGetBusinessConcepts() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getBusinessConcepts(new urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts());
        // TBD - validate results
    }

    public void test14XtentisPortPutBusinessConcept() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.putBusinessConcept(new urn_com_amalto_xtentis_webservice.WSPutBusinessConcept());
        // TBD - validate results
    }

    public void test15XtentisPortGetBusinessConceptKey() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSConceptKey value = null;
        value = binding.getBusinessConceptKey(new urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey());
        // TBD - validate results
    }

    public void test16XtentisPortGetDataClusterPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataClusterPK[] value = null;
        value = binding.getDataClusterPKs(new urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs());
        // TBD - validate results
    }

    public void test17XtentisPortGetDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataCluster value = null;
        value = binding.getDataCluster(new urn_com_amalto_xtentis_webservice.WSGetDataCluster());
        // TBD - validate results
    }

    public void test18XtentisPortExistsDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsDataCluster(new urn_com_amalto_xtentis_webservice.WSExistsDataCluster());
        // TBD - validate results
    }

    public void test19XtentisPortExistsDBDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsDBDataCluster(new urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster());
        // TBD - validate results
    }

    public void test20XtentisPortPutDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataClusterPK value = null;
        value = binding.putDataCluster(new urn_com_amalto_xtentis_webservice.WSPutDataCluster());
        // TBD - validate results
    }

    public void test21XtentisPortPutDBDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.putDBDataCluster(new urn_com_amalto_xtentis_webservice.WSPutDBDataCluster());
        // TBD - validate results
    }

    public void test22XtentisPortDeleteDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDataClusterPK value = null;
        value = binding.deleteDataCluster(new urn_com_amalto_xtentis_webservice.WSDeleteDataCluster());
        // TBD - validate results
    }

    public void test23XtentisPortGetConceptsInDataCluster() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getConceptsInDataCluster(new urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster());
        // TBD - validate results
    }

    public void test24XtentisPortGetConceptsInDataClusterWithRevisions() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry[] value = null;
        value = binding.getConceptsInDataClusterWithRevisions(new urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions());
        // TBD - validate results
    }

    public void test25XtentisPortGetViewPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSViewPK[] value = null;
        value = binding.getViewPKs(new urn_com_amalto_xtentis_webservice.WSGetViewPKs());
        // TBD - validate results
    }

    public void test26XtentisPortGetView() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSView value = null;
        value = binding.getView(new urn_com_amalto_xtentis_webservice.WSGetView());
        // TBD - validate results
    }

    public void test27XtentisPortExistsView() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsView(new urn_com_amalto_xtentis_webservice.WSExistsView());
        // TBD - validate results
    }

    public void test28XtentisPortPutView() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSViewPK value = null;
        value = binding.putView(new urn_com_amalto_xtentis_webservice.WSPutView());
        // TBD - validate results
    }

    public void test29XtentisPortDeleteView() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSViewPK value = null;
        value = binding.deleteView(new urn_com_amalto_xtentis_webservice.WSDeleteView());
        // TBD - validate results
    }

    public void test30XtentisPortGetBusinessConceptValue() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.getBusinessConceptValue(new urn_com_amalto_xtentis_webservice.WSGetBusinessConceptValue());
        // TBD - validate results
    }

    public void test31XtentisPortGetFullPathValues() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getFullPathValues(new urn_com_amalto_xtentis_webservice.WSGetFullPathValues());
        // TBD - validate results
    }

    public void test32XtentisPortGetItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItem value = null;
        value = binding.getItem(new urn_com_amalto_xtentis_webservice.WSGetItem());
        // TBD - validate results
    }

    public void test33XtentisPortExistsItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsItem(new urn_com_amalto_xtentis_webservice.WSExistsItem());
        // TBD - validate results
    }

    public void test34XtentisPortGetItems() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getItems(new urn_com_amalto_xtentis_webservice.WSGetItems());
        // TBD - validate results
    }

    public void test35XtentisPortGetItemPKsByCriteria() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults[] value = null;
        value = binding.getItemPKsByCriteria(new urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria());
        // TBD - validate results
    }

    public void test36XtentisPortViewSearch() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.viewSearch(new urn_com_amalto_xtentis_webservice.WSViewSearch());
        // TBD - validate results
    }

    public void test37XtentisPortXPathsSearch() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.xPathsSearch(new urn_com_amalto_xtentis_webservice.WSXPathsSearch());
        // TBD - validate results
    }

    public void test38XtentisPortGetItemsPivotIndex() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getItemsPivotIndex(new urn_com_amalto_xtentis_webservice.WSGetItemsPivotIndex());
        // TBD - validate results
    }

    public void test39XtentisPortCount() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.count(new urn_com_amalto_xtentis_webservice.WSCount());
        // TBD - validate results
    }

    public void test40XtentisPortQuickSearch() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.quickSearch(new urn_com_amalto_xtentis_webservice.WSQuickSearch());
        // TBD - validate results
    }

    public void test41XtentisPortPutItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK value = null;
        value = binding.putItem(new urn_com_amalto_xtentis_webservice.WSPutItem());
        // TBD - validate results
    }

    public void test42XtentisPortPutItemArray() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK[] value = null;
        value = binding.putItemArray(new urn_com_amalto_xtentis_webservice.WSPutItem[0]);
        // TBD - validate results
    }

    public void test43XtentisPortPutItemWithReport() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK value = null;
        value = binding.putItemWithReport(new urn_com_amalto_xtentis_webservice.WSPutItemWithReport());
        // TBD - validate results
    }

    public void test44XtentisPortPutItemWithReportArray() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK[] value = null;
        value = binding.putItemWithReportArray(new urn_com_amalto_xtentis_webservice.WSPutItemWithReport[0]);
        // TBD - validate results
    }

    public void test45XtentisPortExtractUsingTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] value = null;
        value = binding.extractUsingTransformer(new urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer());
        // TBD - validate results
    }

    public void test46XtentisPortExtractUsingTransformerThruView() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] value = null;
        value = binding.extractUsingTransformerThruView(new urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView());
        // TBD - validate results
    }

    public void test47XtentisPortDeleteItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK value = null;
        value = binding.deleteItem(new urn_com_amalto_xtentis_webservice.WSDeleteItem());
        // TBD - validate results
    }

    public void test48XtentisPortDeleteItems() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSInt value = null;
        value = binding.deleteItems(new urn_com_amalto_xtentis_webservice.WSDeleteItems());
        // TBD - validate results
    }

    public void test49XtentisPortDropItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDroppedItemPK value = null;
        value = binding.dropItem(new urn_com_amalto_xtentis_webservice.WSDropItem());
        // TBD - validate results
    }

    public void test50XtentisPortRunQuery() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.runQuery(new urn_com_amalto_xtentis_webservice.WSRunQuery());
        // TBD - validate results
    }

    public void test51XtentisPortConnectorInteraction() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSConnectorInteractionResponse value = null;
        value = binding.connectorInteraction(new urn_com_amalto_xtentis_webservice.WSConnectorInteraction());
        // TBD - validate results
    }

    public void test52XtentisPortGetRoutingRulePKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] value = null;
        value = binding.getRoutingRulePKs(new urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs());
        // TBD - validate results
    }

    public void test53XtentisPortGetRoutingRule() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingRule value = null;
        value = binding.getRoutingRule(new urn_com_amalto_xtentis_webservice.WSGetRoutingRule());
        // TBD - validate results
    }

    public void test54XtentisPortExistsRoutingRule() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsRoutingRule(new urn_com_amalto_xtentis_webservice.WSExistsRoutingRule());
        // TBD - validate results
    }

    public void test55XtentisPortPutRoutingRule() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK value = null;
        value = binding.putRoutingRule(new urn_com_amalto_xtentis_webservice.WSPutRoutingRule());
        // TBD - validate results
    }

    public void test56XtentisPortDeleteRoutingRule() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK value = null;
        value = binding.deleteRoutingRule(new urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule());
        // TBD - validate results
    }

    public void test57XtentisPortServiceAction() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.serviceAction(new urn_com_amalto_xtentis_webservice.WSServiceAction());
        // TBD - validate results
    }

    public void test58XtentisPortGetServiceConfiguration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.getServiceConfiguration(new urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration());
        // TBD - validate results
    }

    public void test59XtentisPortPutServiceConfiguration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.putServiceConfiguration(new urn_com_amalto_xtentis_webservice.WSServicePutConfiguration());
        // TBD - validate results
    }

    public void test60XtentisPortGetServicesList() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSServicesListItem[] value = null;
        value = binding.getServicesList(new urn_com_amalto_xtentis_webservice.WSGetServicesList());
        // TBD - validate results
    }

    public void test61XtentisPortGetServiceDocument() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSServiceGetDocument value = null;
        value = binding.getServiceDocument(new urn_com_amalto_xtentis_webservice.WSString());
        // TBD - validate results
    }

    public void test62XtentisPortGetStoredProcedure() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSStoredProcedure value = null;
        value = binding.getStoredProcedure(new urn_com_amalto_xtentis_webservice.WSGetStoredProcedure());
        // TBD - validate results
    }

    public void test63XtentisPortExistsStoredProcedure() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsStoredProcedure(new urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure());
        // TBD - validate results
    }

    public void test64XtentisPortGetStoredProcedurePKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSStoredProcedurePK[] value = null;
        value = binding.getStoredProcedurePKs(new urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure());
        // TBD - validate results
    }

    public void test65XtentisPortPutStoredProcedure() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSStoredProcedurePK value = null;
        value = binding.putStoredProcedure(new urn_com_amalto_xtentis_webservice.WSPutStoredProcedure());
        // TBD - validate results
    }

    public void test66XtentisPortDeleteStoredProcedure() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSStoredProcedurePK value = null;
        value = binding.deleteStoredProcedure(new urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure());
        // TBD - validate results
    }

    public void test67XtentisPortExecuteStoredProcedure() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.executeStoredProcedure(new urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure());
        // TBD - validate results
    }

    public void test68XtentisPortGetTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformer value = null;
        value = binding.getTransformer(new urn_com_amalto_xtentis_webservice.WSGetTransformer());
        // TBD - validate results
    }

    public void test69XtentisPortExistsTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsTransformer(new urn_com_amalto_xtentis_webservice.WSExistsTransformer());
        // TBD - validate results
    }

    public void test70XtentisPortGetTransformerPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerPK[] value = null;
        value = binding.getTransformerPKs(new urn_com_amalto_xtentis_webservice.WSGetTransformerPKs());
        // TBD - validate results
    }

    public void test71XtentisPortPutTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerPK value = null;
        value = binding.putTransformer(new urn_com_amalto_xtentis_webservice.WSPutTransformer());
        // TBD - validate results
    }

    public void test72XtentisPortDeleteTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerPK value = null;
        value = binding.deleteTransformer(new urn_com_amalto_xtentis_webservice.WSDeleteTransformer());
        // TBD - validate results
    }

    public void test73XtentisPortProcessBytesUsingTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] value = null;
        value = binding.processBytesUsingTransformer(new urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer());
        // TBD - validate results
    }

    public void test74XtentisPortProcessFileUsingTransformer() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry[] value = null;
        value = binding.processFileUsingTransformer(new urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer());
        // TBD - validate results
    }

    public void test75XtentisPortProcessBytesUsingTransformerAsBackgroundJob() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.processBytesUsingTransformerAsBackgroundJob(new urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob());
        // TBD - validate results
    }

    public void test76XtentisPortProcessFileUsingTransformerAsBackgroundJob() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.processFileUsingTransformerAsBackgroundJob(new urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob());
        // TBD - validate results
    }

    public void test77XtentisPortGetTransformerV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerV2 value = null;
        value = binding.getTransformerV2(new urn_com_amalto_xtentis_webservice.WSGetTransformerV2());
        // TBD - validate results
    }

    public void test78XtentisPortExistsTransformerV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsTransformerV2(new urn_com_amalto_xtentis_webservice.WSExistsTransformerV2());
        // TBD - validate results
    }

    public void test79XtentisPortGetTransformerV2PKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerV2PK[] value = null;
        value = binding.getTransformerV2PKs(new urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs());
        // TBD - validate results
    }

    public void test80XtentisPortPutTransformerV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerV2PK value = null;
        value = binding.putTransformerV2(new urn_com_amalto_xtentis_webservice.WSPutTransformerV2());
        // TBD - validate results
    }

    public void test81XtentisPortDeleteTransformerV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerV2PK value = null;
        value = binding.deleteTransformerV2(new urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2());
        // TBD - validate results
    }

    public void test82XtentisPortExecuteTransformerV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerContext value = null;
        value = binding.executeTransformerV2(new urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2());
        // TBD - validate results
    }

    public void test83XtentisPortExecuteTransformerV2AsJob() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.executeTransformerV2AsJob(new urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob());
        // TBD - validate results
    }

    public void test84XtentisPortExtractThroughTransformerV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerContext value = null;
        value = binding.extractThroughTransformerV2(new urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2());
        // TBD - validate results
    }

    public void test85XtentisPortExistsTransformerPluginV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsTransformerPluginV2(new urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2());
        // TBD - validate results
    }

    public void test86XtentisPortGetTransformerPluginV2Configuration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.getTransformerPluginV2Configuration(new urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration());
        // TBD - validate results
    }

    public void test87XtentisPortPutTransformerPluginV2Configuration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.putTransformerPluginV2Configuration(new urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration());
        // TBD - validate results
    }

    public void test88XtentisPortGetTransformerPluginV2Details() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details value = null;
        value = binding.getTransformerPluginV2Details(new urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details());
        // TBD - validate results
    }

    public void test89XtentisPortGetTransformerPluginV2SList() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem[] value = null;
        value = binding.getTransformerPluginV2SList(new urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList());
        // TBD - validate results
    }

    public void test90XtentisPortGetRole() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRole value = null;
        value = binding.getRole(new urn_com_amalto_xtentis_webservice.WSGetRole());
        // TBD - validate results
    }

    public void test91XtentisPortExistsRole() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsRole(new urn_com_amalto_xtentis_webservice.WSExistsRole());
        // TBD - validate results
    }

    public void test92XtentisPortGetRolePKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRolePK[] value = null;
        value = binding.getRolePKs(new urn_com_amalto_xtentis_webservice.WSGetRolePKs());
        // TBD - validate results
    }

    public void test93XtentisPortPutRole() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRolePK value = null;
        value = binding.putRole(new urn_com_amalto_xtentis_webservice.WSPutRole());
        // TBD - validate results
    }

    public void test94XtentisPortDeleteRole() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRolePK value = null;
        value = binding.deleteRole(new urn_com_amalto_xtentis_webservice.WSDeleteRole());
        // TBD - validate results
    }

    public void test95XtentisPortGetObjectsForRoles() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getObjectsForRoles(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test96XtentisPortGetMenu() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSMenu value = null;
        value = binding.getMenu(new urn_com_amalto_xtentis_webservice.WSGetMenu());
        // TBD - validate results
    }

    public void test97XtentisPortExistsMenu() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsMenu(new urn_com_amalto_xtentis_webservice.WSExistsMenu());
        // TBD - validate results
    }

    public void test98XtentisPortGetMenuPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSMenuPK[] value = null;
        value = binding.getMenuPKs(new urn_com_amalto_xtentis_webservice.WSGetMenuPKs());
        // TBD - validate results
    }

    public void test99XtentisPortPutMenu() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSMenuPK value = null;
        value = binding.putMenu(new urn_com_amalto_xtentis_webservice.WSPutMenu());
        // TBD - validate results
    }

    public void test100XtentisPortDeleteMenu() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSMenuPK value = null;
        value = binding.deleteMenu(new urn_com_amalto_xtentis_webservice.WSDeleteMenu());
        // TBD - validate results
    }

    public void test101XtentisPortVersioningCommitItems() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningCommitItems(new urn_com_amalto_xtentis_webservice.WSVersioningCommitItems());
        // TBD - validate results
    }

    public void test102XtentisPortVersioningRestoreItemByRevision() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.versioningRestoreItemByRevision(new urn_com_amalto_xtentis_webservice.WSVersioningRestoreItemByRevision());
        // TBD - validate results
    }

    public void test103XtentisPortVersioningGetItemHistory() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersioningItemHistory value = null;
        value = binding.versioningGetItemHistory(new urn_com_amalto_xtentis_webservice.WSVersioningGetItemHistory());
        // TBD - validate results
    }

    public void test104XtentisPortVersioningGetItemsVersions() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersioningItemsVersionsItems[] value = null;
        value = binding.versioningGetItemsVersions(new urn_com_amalto_xtentis_webservice.WSVersioningGetItemsVersions());
        // TBD - validate results
    }

    public void test105XtentisPortVersioningGetItemContent() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.versioningGetItemContent(new urn_com_amalto_xtentis_webservice.WSVersioningGetItemContent());
        // TBD - validate results
    }

    public void test106XtentisPortVersioningGetObjectsVersions() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersioningObjectsVersionsObjects[] value = null;
        value = binding.versioningGetObjectsVersions(new urn_com_amalto_xtentis_webservice.WSVersioningGetObjectsVersions());
        // TBD - validate results
    }

    public void test107XtentisPortVersioningGetUniverseVersions() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersioningUniverseVersionsTagStructure[] value = null;
        value = binding.versioningGetUniverseVersions(new urn_com_amalto_xtentis_webservice.WSVersioningGetUniverseVersions());
        // TBD - validate results
    }

    public void test108XtentisPortGetVersioningSystemConfiguration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersioningSystemConfiguration value = null;
        value = binding.getVersioningSystemConfiguration(new urn_com_amalto_xtentis_webservice.WSGetVersioningSystemConfiguration());
        // TBD - validate results
    }

    public void test109XtentisPortPutVersioningSystemConfiguration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.putVersioningSystemConfiguration(new urn_com_amalto_xtentis_webservice.WSPutVersioningSystemConfiguration());
        // TBD - validate results
    }

    public void test110XtentisPortVersioningGetInfo() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSVersioningInfo value = null;
        value = binding.versioningGetInfo(new urn_com_amalto_xtentis_webservice.WSVersioningGetInfo());
        // TBD - validate results
    }

    public void test111XtentisPortVersioningTagObjects() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningTagObjects(new urn_com_amalto_xtentis_webservice.WSVersioningTagObjects());
        // TBD - validate results
    }

    public void test112XtentisPortVersioningTagUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningTagUniverse(new urn_com_amalto_xtentis_webservice.WSVersioningTagUniverse());
        // TBD - validate results
    }

    public void test113XtentisPortVersioningTagItems() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningTagItems(new urn_com_amalto_xtentis_webservice.WSVersioningTagItems());
        // TBD - validate results
    }

    public void test114XtentisPortVersioningRestoreObjects() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningRestoreObjects(new urn_com_amalto_xtentis_webservice.WSVersioningRestoreObjects());
        // TBD - validate results
    }

    public void test115XtentisPortVersioningRestoreUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningRestoreUniverse(new urn_com_amalto_xtentis_webservice.WSVersioningRestoreUniverse());
        // TBD - validate results
    }

    public void test116XtentisPortVersioningRestoreItems() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.versioningRestoreItems(new urn_com_amalto_xtentis_webservice.WSVersioningRestoreItems());
        // TBD - validate results
    }

    public void test117XtentisPortFindBackgroundJobPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK[] value = null;
        value = binding.findBackgroundJobPKs(new urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs());
        // TBD - validate results
    }

    public void test118XtentisPortPutBackgroundJob() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJobPK value = null;
        value = binding.putBackgroundJob(new urn_com_amalto_xtentis_webservice.WSPutBackgroundJob());
        // TBD - validate results
    }

    public void test119XtentisPortGetBackgroundJob() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBackgroundJob value = null;
        value = binding.getBackgroundJob(new urn_com_amalto_xtentis_webservice.WSGetBackgroundJob());
        // TBD - validate results
    }

    public void test120XtentisPortGetRoutingOrderV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 value = null;
        value = binding.getRoutingOrderV2(new urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2());
        // TBD - validate results
    }

    public void test121XtentisPortExistsRoutingOrderV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2 value = null;
        value = binding.existsRoutingOrderV2(new urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2());
        // TBD - validate results
    }

    public void test122XtentisPortDeleteRoutingOrderV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK value = null;
        value = binding.deleteRoutingOrderV2(new urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2());
        // TBD - validate results
    }

    public void test123XtentisPortExecuteRoutingOrderV2Asynchronously() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK value = null;
        value = binding.executeRoutingOrderV2Asynchronously(new urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously());
        // TBD - validate results
    }

    public void test124XtentisPortExecuteRoutingOrderV2Synchronously() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.executeRoutingOrderV2Synchronously(new urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously());
        // TBD - validate results
    }

    public void test125XtentisPortGetRoutingOrderV2PKsByCriteria() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK[] value = null;
        value = binding.getRoutingOrderV2PKsByCriteria(new urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria());
        // TBD - validate results
    }

    public void test126XtentisPortGetRoutingOrderV2SByCriteria() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingOrderV2[] value = null;
        value = binding.getRoutingOrderV2SByCriteria(new urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria());
        // TBD - validate results
    }

    public void test127XtentisPortRouteItemV2() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingRulePK[] value = null;
        value = binding.routeItemV2(new urn_com_amalto_xtentis_webservice.WSRouteItemV2());
        // TBD - validate results
    }

    public void test128XtentisPortRoutingEngineV2Action() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status value = null;
        value = binding.routingEngineV2Action(new urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action());
        // TBD - validate results
    }

    public void test129XtentisPortGetUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSUniverse value = null;
        value = binding.getUniverse(new urn_com_amalto_xtentis_webservice.WSGetUniverse());
        // TBD - validate results
    }

    public void test130XtentisPortExistsUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsUniverse(new urn_com_amalto_xtentis_webservice.WSExistsUniverse());
        // TBD - validate results
    }

    public void test131XtentisPortGetUniversePKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSUniversePK[] value = null;
        value = binding.getUniversePKs(new urn_com_amalto_xtentis_webservice.WSGetUniversePKs());
        // TBD - validate results
    }

    public void test132XtentisPortPutUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSUniversePK value = null;
        value = binding.putUniverse(new urn_com_amalto_xtentis_webservice.WSPutUniverse());
        // TBD - validate results
    }

    public void test133XtentisPortDeleteUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSUniversePK value = null;
        value = binding.deleteUniverse(new urn_com_amalto_xtentis_webservice.WSDeleteUniverse());
        // TBD - validate results
    }

    public void test134XtentisPortGetObjectsForUniverses() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getObjectsForUniverses(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test135XtentisPortGetCurrentUniverse() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSUniverse value = null;
        value = binding.getCurrentUniverse(new urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse());
        // TBD - validate results
    }

    public void test136XtentisPortGetUniverseByRevision() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSUniversePK[] value = null;
        value = binding.getUniverseByRevision(new urn_com_amalto_xtentis_webservice.WSGetUniverseByRevision());
        // TBD - validate results
    }

    public void test137XtentisPortGetSynchronizationPlan() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationPlan value = null;
        value = binding.getSynchronizationPlan(new urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlan());
        // TBD - validate results
    }

    public void test138XtentisPortExistsSynchronizationPlan() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsSynchronizationPlan(new urn_com_amalto_xtentis_webservice.WSExistsSynchronizationPlan());
        // TBD - validate results
    }

    public void test139XtentisPortGetSynchronizationPlanPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK[] value = null;
        value = binding.getSynchronizationPlanPKs(new urn_com_amalto_xtentis_webservice.WSGetSynchronizationPlanPKs());
        // TBD - validate results
    }

    public void test140XtentisPortPutSynchronizationPlan() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK value = null;
        value = binding.putSynchronizationPlan(new urn_com_amalto_xtentis_webservice.WSPutSynchronizationPlan());
        // TBD - validate results
    }

    public void test141XtentisPortDeleteSynchronizationPlan() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationPlanPK value = null;
        value = binding.deleteSynchronizationPlan(new urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationPlan());
        // TBD - validate results
    }

    public void test142XtentisPortGetObjectsForSynchronizationPlans() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getObjectsForSynchronizationPlans(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test143XtentisPortGetSynchronizationPlanObjectsAlgorithms() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getSynchronizationPlanObjectsAlgorithms(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test144XtentisPortGetSynchronizationPlanItemsAlgorithms() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.getSynchronizationPlanItemsAlgorithms(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test145XtentisPortSynchronizationPlanAction() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationPlanStatus value = null;
        value = binding.synchronizationPlanAction(new urn_com_amalto_xtentis_webservice.WSSynchronizationPlanAction());
        // TBD - validate results
    }

    public void test146XtentisPortSynchronizationGetUnsynchronizedObjectsIDs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.synchronizationGetUnsynchronizedObjectsIDs(new urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedObjectsIDs());
        // TBD - validate results
    }

    public void test147XtentisPortSynchronizationGetObjectXML() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.synchronizationGetObjectXML(new urn_com_amalto_xtentis_webservice.WSSynchronizationGetObjectXML());
        // TBD - validate results
    }

    public void test148XtentisPortSynchronizationPutObjectXML() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.synchronizationPutObjectXML(new urn_com_amalto_xtentis_webservice.WSSynchronizationPutObjectXML());
        // TBD - validate results
    }

    public void test149XtentisPortSynchronizationGetUnsynchronizedItemPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK[] value = null;
        value = binding.synchronizationGetUnsynchronizedItemPKs(new urn_com_amalto_xtentis_webservice.WSSynchronizationGetUnsynchronizedItemPKs());
        // TBD - validate results
    }

    public void test150XtentisPortSynchronizationGetItemXML() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSString value = null;
        value = binding.synchronizationGetItemXML(new urn_com_amalto_xtentis_webservice.WSSynchronizationGetItemXML());
        // TBD - validate results
    }

    public void test151XtentisPortSynchronizationPutItemXML() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK value = null;
        value = binding.synchronizationPutItemXML(new urn_com_amalto_xtentis_webservice.WSSynchronizationPutItemXML());
        // TBD - validate results
    }

    public void test152XtentisPortGetSynchronizationItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationItem value = null;
        value = binding.getSynchronizationItem(new urn_com_amalto_xtentis_webservice.WSGetSynchronizationItem());
        // TBD - validate results
    }

    public void test153XtentisPortExistsSynchronizationItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSBoolean value = null;
        value = binding.existsSynchronizationItem(new urn_com_amalto_xtentis_webservice.WSExistsSynchronizationItem());
        // TBD - validate results
    }

    public void test154XtentisPortGetSynchronizationItemPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[][] value = null;
        value = binding.getSynchronizationItemPKs(new urn_com_amalto_xtentis_webservice.WSGetSynchronizationItemPKs());
        // TBD - validate results
    }

    public void test155XtentisPortPutSynchronizationItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.putSynchronizationItem(new urn_com_amalto_xtentis_webservice.WSPutSynchronizationItem());
        // TBD - validate results
    }

    public void test156XtentisPortDeleteSynchronizationItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String[] value = null;
        value = binding.deleteSynchronizationItem(new urn_com_amalto_xtentis_webservice.WSDeleteSynchronizationItem());
        // TBD - validate results
    }

    public void test157XtentisPortResolveSynchronizationItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSSynchronizationItem value = null;
        value = binding.resolveSynchronizationItem(new urn_com_amalto_xtentis_webservice.WSResolveSynchronizationItem());
        // TBD - validate results
    }

    public void test158XtentisPortRecoverDroppedItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSItemPK value = null;
        value = binding.recoverDroppedItem(new urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem());
        // TBD - validate results
    }

    public void test159XtentisPortFindAllDroppedItemsPKs() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDroppedItemPK[] value = null;
        value = binding.findAllDroppedItemsPKs(new urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs());
        // TBD - validate results
    }

    public void test160XtentisPortLoadDroppedItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDroppedItem value = null;
        value = binding.loadDroppedItem(new urn_com_amalto_xtentis_webservice.WSLoadDroppedItem());
        // TBD - validate results
    }

    public void test161XtentisPortRemoveDroppedItem() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSDroppedItemPK value = null;
        value = binding.removeDroppedItem(new urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem());
        // TBD - validate results
    }

    public void test162XtentisPortGetMDMConfiguration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSMDMConfig value = null;
        value = binding.getMDMConfiguration();
        // TBD - validate results
    }

    public void test163XtentisPortCheckServiceConfiguration() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse value = null;
        value = binding.checkServiceConfiguration(new urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest());
        // TBD - validate results
    }

    public void test164XtentisPortWorkflowGetProcessDefinitions() throws Exception {
        urn_com_amalto_xtentis_webservice.XtentisBindingStub binding;
        try {
            binding = (urn_com_amalto_xtentis_webservice.XtentisBindingStub)
                          new urn_com_amalto_xtentis_webservice.XtentisServiceLocator().getXtentisPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        urn_com_amalto_xtentis_webservice.WSWorkflowProcessDefinitionUUID[] value = null;
        value = binding.workflowGetProcessDefinitions(new urn_com_amalto_xtentis_webservice.WSWorkflowGetProcessDefinitions());
        // TBD - validate results
    }

}
