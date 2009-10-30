package org.talend.mdm.webapp.crossreferencing.bean;

import java.io.StringReader;
import java.util.ArrayList;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

public class CrossReferencingTableDescription {

	private String name;
	private String[] fields;
	private String[] keys;


	public CrossReferencingTableDescription(String name, String[] keys, String[] fields) {
		super();
		this.name = name;
		this.fields = fields;
		this.keys = keys;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CrossReferencingTableDescription() {
		super();
	}


	public static CrossReferencingTableDescription getCrossReferencingTableDescription(String tableName) throws XtentisWebappException{
		try {
			return
				new CrossReferencingTableDescription(tableName,getTableKeys(tableName),getTableFieldNames(tableName));
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to get the cross referencing table descripion of "+tableName+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	private static String[] getTableKeys(String tableName) throws XtentisWebappException{
		try {
			return
				Util.getPort()
					.getBusinessConceptKey(new WSGetBusinessConceptKey(new WSDataModelPK(Configuration.datamodel),tableName))
						.getFields();
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to retrieve the keys of table "+tableName+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	private static String[] getTableFieldNames(String tableName) throws XtentisWebappException{
		try {
			//grab the table fileds (e.g. the concept sub-elements)
			String schema = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(Configuration.datamodel))).getXsdSchema();

	        XSOMParser parser = new XSOMParser();
	        //parser.setAnnotationParser(new DomAnnotationParserFactory());
	        parser.parse(new StringReader(schema));
	        XSSchemaSet xss = parser.getResult();

	        //debug
	        /*for (Iterator iter = xss.iterateElementDecls(); iter.hasNext(); ) {
				XSElementDecl decl = (XSElementDecl) iter.next();
				System.out.println(decl.getTargetNamespace() + " : "+decl.getName());
			}*/

	        XSElementDecl decl;
	        decl = xss.getElementDecl("", tableName);
	        if (decl==null) {
	        	throw new XtentisWebappException("The cross-referencing table \""+tableName+"\" definition cannot be found");
	        }
	        XSComplexType type = (XSComplexType)decl.getType();
	        XSParticle[] xsp =
	        	type.getContentType().asParticle().getTerm().asModelGroup().getChildren();
	        ArrayList<String> fieldNames = new ArrayList<String>();
	        for (int i = 0; i < xsp.length; i++) {
	        	fieldNames.add(xsp[i].getTerm().asElementDecl().getName());
			}
	        return fieldNames.toArray(new String[fieldNames.size()]);
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to retrieve the field names of table "+tableName+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}


}
