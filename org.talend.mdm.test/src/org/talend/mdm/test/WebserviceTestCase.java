package org.talend.mdm.test;

import org.talend.mdm.test.util.Util;

import talend.mdm.test.MDMTestCase;

import urn_com_amalto_xtentis_webservice.XtentisPort;

public class WebserviceTestCase extends MDMTestCase {

	protected static final String URL = "http://localhost:8080/talend/TalendPort";
	protected static final String User = "admin";
	protected static final String PASSWORD = "talend";
	protected static final String Universe = "";
	protected static final String SCHEMA = "<?xml version='1.0' encoding='UTF-8'?><xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' attributeFormDefault='unqualified' blockDefault='' elementFormDefault='unqualified' finalDefault=''><xsd:element abstract='false' name='Conf' nillable='false'><xsd:complexType mixed='false'><xsd:sequence maxOccurs='1' minOccurs='1'><xsd:element maxOccurs='1' minOccurs='1' name='id' nillable='false' type='xsd:string'/><xsd:element maxOccurs='1' minOccurs='1' name='Config' nillable='false' type='xsd:string'/></xsd:sequence></xsd:complexType><xsd:unique name='Conf'><xsd:selector xpath='.'/><xsd:field xpath='id'/></xsd:unique></xsd:element></xsd:schema>";
	protected static final String BUSINESSCONCEPTSCHEMA = "<xsd:import namespace='http://www.w3.org/2001/XMLSchema'/><xsd:element abstract='false' name='Student' nillable='false' type='StudentType'><xsd:unique name='Student'><xsd:selector xpath='.'/><xsd:field xpath='id'/></xsd:unique></xsd:element><xsd:complexType abstract='false' mixed='false' name='StudentType'><xsd:sequence maxOccurs='1' minOccurs='1'><xsd:element maxOccurs='1' minOccurs='1' name='id' nillable='false' type='xsd:string'/><xsd:element maxOccurs='1' minOccurs='1' name='name' nillable='false' type='xsd:string'/><xsd:element maxOccurs='1' minOccurs='1' name='age' nillable='false' type='xsd:string'/></xsd:sequence></xsd:complexType>";
	protected static XtentisPort defaultPort = null;
	static {
		try {
			defaultPort = (XtentisPort) Util.getPort(URL, Universe, User,
					PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
