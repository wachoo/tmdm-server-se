package com.amalto.workbench.utils;

import java.io.StringReader;
import java.io.StringWriter;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

public class RoleWhereCondition {
	String leftPath;
	String operator;
	String rightValueOrPath;
	String predicate;
	public String getLeftPath() {return leftPath;}
	public void setLeftPath(String leftPath) {this.leftPath = leftPath;}
	public String getOperator() {return operator;}
	public void setOperator(String operator) {	this.operator = operator;}
	public String getPredicate() {return predicate;}
	public void setPredicate(String predicate) {this.predicate = predicate;}
	public String getRightValueOrPath() {return rightValueOrPath;}
	public void setRightValueOrPath(String righValueOrPath) {this.rightValueOrPath = righValueOrPath;}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try {
			Marshaller.marshal(this, sw);
		} catch (Exception e) {
			System.out.println("toString() ERROR MARSHALLING WhereCondition");
			e.printStackTrace();
		}
		return sw.toString();
	}
	public static RoleWhereCondition parse(String marshalledWC) {
		RoleWhereCondition rwc = null;
		try {
			rwc = (RoleWhereCondition)Unmarshaller.unmarshal(RoleWhereCondition.class, new StringReader(marshalledWC));
		} catch (Exception e) {
			System.out.println("toString() ERROR UNMARSHALLING WhereCondition");
			e.printStackTrace();
		}
		return rwc;
	}
}
