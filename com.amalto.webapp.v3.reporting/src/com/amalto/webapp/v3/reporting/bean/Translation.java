package com.amalto.webapp.v3.reporting.bean;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;


public class Translation {

	private String document;
	private TranslationField[] translationFields; 
	
	public Translation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public TranslationField[] getTranslationFields() {
		return translationFields;
	}

	public void setTranslationFields(TranslationField[] translationFields) {
		this.translationFields = translationFields;
	}
	
	public static Translation parse(String xml){
		Translation translation = new Translation();
		Element result;
		try {
			result = Util.parse(xml).getDocumentElement();
			translation.setDocument(Util.getFirstTextNode(result, "//Document"));			
			NodeList fields = Util.getNodeList(result,"/Translation/ListOfFields/Field");
			if (fields!=null){
				TranslationField[] translationFields = new TranslationField[fields.getLength()];
				for (int i=0;i<fields.getLength();i++){
					TranslationField  translationField = new TranslationField();
					translationField.setXPath(Util.getFirstTextNode(fields.item(i),"./XPath"));
					translationField.setEn(Util.getFirstTextNode(fields.item(i),"./EN"));
					translationField.setFr(Util.getFirstTextNode(fields.item(i),"./FR"));
					translationFields[i] = translationField;					
				}
				translation.setTranslationFields(translationFields);
			}					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translation;		
	}
	
	/**
	 * not used
	 * @return
	 */
	public String serialize(){
		StringBuffer xml = new StringBuffer();
        xml.append("<Translation>");
        xml.append("<Document>"+document+"</Document>");
        xml.append("<ListOfFields>");
        for(int i=0;i<translationFields.length;i++) {
        	xml.append("<Field>");
        	xml.append("<XPath>"+translationFields[i].getXpath()+"</XPath>");
        	xml.append("<EN>"+translationFields[i].getEn()+"</EN>");
        	xml.append("<FR>"+translationFields[i].getFr()+"</FR>");
        	xml.append("</Field>");        }
        xml.append("</ListOfFields>");
        xml.append("</Translation>");
        return xml.toString();		
	}
}
