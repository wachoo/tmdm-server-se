/**
 * @deprecated
 */
package com.amalto.core.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class BusinessElement implements Serializable{
    protected java.lang.String name;
    protected WSI18NString[] labels;
    protected WSI18NString[] descriptions;
    protected WSComplexTypeBusinessElement complexType;
    protected WSSimpleTypeBusinessElement simpleType;
    protected java.lang.String xsd;
    
    public BusinessElement() {
    }
    
    
    public BusinessElement(java.lang.String name, WSI18NString[] labels, WSI18NString[] descriptions, WSComplexTypeBusinessElement complexType, WSSimpleTypeBusinessElement simpleType, java.lang.String xsd) {
        this.name = name;
        this.labels = labels;
        this.descriptions = descriptions;
        this.complexType = complexType;
        this.simpleType = simpleType;
        this.xsd = xsd;
    }
    
    
    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    public WSI18NString[] getLabels() {
        return labels;
    }
    
    public void setLabels(WSI18NString[] labels) {
        this.labels = labels;
    }
    
    public WSI18NString[] getDescriptions() {
        return descriptions;
    }
    
    public void setDescriptions(WSI18NString[] descriptions) {
        this.descriptions = descriptions;
    }
    
    public WSComplexTypeBusinessElement getComplexType() {
        return complexType;
    }
    
    public void setComplexType(WSComplexTypeBusinessElement complexType) {
        this.complexType = complexType;
    }
    
    public WSSimpleTypeBusinessElement getSimpleType() {
        return simpleType;
    }
    
    public void setSimpleType(WSSimpleTypeBusinessElement simpleType) {
        this.simpleType = simpleType;
    }
    
    public java.lang.String getXsd() {
        return xsd;
    }
    
    public void setXsd(java.lang.String xsd) {
        this.xsd = xsd;
    }
    
    
    class WSI18NString implements Serializable{
        protected WSLanguage language;
        protected java.lang.String label;
        
        public WSI18NString() {
        }
        
        public WSI18NString(WSLanguage language, java.lang.String label) {
            this.language = language;
            this.label = label;
        }
        
        public WSLanguage getLanguage() {
            return language;
        }
        
        public void setLanguage(WSLanguage language) {
            this.language = language;
        }
        
        public java.lang.String getLabel() {
            return label;
        }
        
        public void setLabel(java.lang.String label) {
            this.label = label;
        }
    }
    
    class WSLanguage implements Serializable{
        private java.lang.String value;
        private  Map valueMap = new HashMap();
        public final String _FRString = "FR";
        public final String _ENString = "EN";
        
        public final java.lang.String _FR = new java.lang.String(_FRString);
        public final java.lang.String _EN = new java.lang.String(_ENString);
        
        public final WSLanguage FR = new WSLanguage(_FR);
        public final WSLanguage EN = new WSLanguage(_EN);
        
        protected WSLanguage(java.lang.String value) {
            this.value = value;
            valueMap.put(this.toString(), this);
        }
        
        public java.lang.String getValue() {
            return value;
        }
        
        public WSLanguage fromValue(java.lang.String value)
            throws java.lang.IllegalStateException {
            if (FR.value.equals(value)) {
                return FR;
            } else if (EN.value.equals(value)) {
                return EN;
            }
            throw new IllegalArgumentException();
        }
        
        public WSLanguage fromString(String value)
            throws java.lang.IllegalStateException {
            WSLanguage ret = (WSLanguage)valueMap.get(value);
            if (ret != null) {
                return ret;
            }
            if (value.equals(_FRString)) {
                return FR;
            } else if (value.equals(_ENString)) {
                return EN;
            }
            throw new IllegalArgumentException();
        }
        
        public String toString() {
            return value.toString();
        }
        
        private Object readResolve()
            throws java.io.ObjectStreamException {
            return fromValue(getValue());
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof WSLanguage)) {
                return false;
            }
            return ((WSLanguage)obj).value.equals(value);
        }
        
        public int hashCode() {
            return value.hashCode();
        }
    }

    class WSChildBusinessElement  implements Serializable{
        protected java.lang.String name;
        protected int minOccurs;
        protected int maxOccurs;
        
        public WSChildBusinessElement() {
        }
        
        public WSChildBusinessElement(java.lang.String name, int minOccurs, int maxOccurs) {
            this.name = name;
            this.minOccurs = minOccurs;
            this.maxOccurs = maxOccurs;
        }
        
        public java.lang.String getName() {
            return name;
        }
        
        public void setName(java.lang.String name) {
            this.name = name;
        }
        
        public int getMinOccurs() {
            return minOccurs;
        }
        
        public void setMinOccurs(int minOccurs) {
            this.minOccurs = minOccurs;
        }
        
        public int getMaxOccurs() {
            return maxOccurs;
        }
        
        public void setMaxOccurs(int maxOccurs) {
            this.maxOccurs = maxOccurs;
        }
    }
    
    class WSComplexTypeBusinessElement  implements Serializable{
        protected WSChildBusinessElement[] childBusinessElement;
        protected java.lang.String keyChildBusinessElement;
        
        public WSComplexTypeBusinessElement() {
        }
        
        public WSComplexTypeBusinessElement(WSChildBusinessElement[] childBusinessElement, java.lang.String keyChildBusinessElement) {
            this.childBusinessElement = childBusinessElement;
            this.keyChildBusinessElement = keyChildBusinessElement;
        }
        
        public WSChildBusinessElement[] getChildBusinessElement() {
            return childBusinessElement;
        }
        
        public void setChildBusinessElement(WSChildBusinessElement[] childBusinessElement) {
            this.childBusinessElement = childBusinessElement;
        }
        
        public java.lang.String getKeyChildBusinessElement() {
            return keyChildBusinessElement;
        }
        
        public void setKeyChildBusinessElement(java.lang.String keyChildBusinessElement) {
            this.keyChildBusinessElement = keyChildBusinessElement;
        }
    }
    
    class WSSimpleTypeBusinessElement  implements Serializable{
        protected WSBaseType baseType;
        protected java.lang.String pattern;
        protected java.lang.String[] tokensList;
        
        public WSSimpleTypeBusinessElement() {
        }
        
        public WSSimpleTypeBusinessElement(WSBaseType baseType, java.lang.String pattern, java.lang.String[] tokensList) {
            this.baseType = baseType;
            this.pattern = pattern;
            this.tokensList = tokensList;
        }
        
        public WSBaseType getBaseType() {
            return baseType;
        }
        
        public void setBaseType(WSBaseType baseType) {
            this.baseType = baseType;
        }
        
        public java.lang.String getPattern() {
            return pattern;
        }
        
        public void setPattern(java.lang.String pattern) {
            this.pattern = pattern;
        }
        
        public java.lang.String[] getTokensList() {
            return tokensList;
        }
        
        public void setTokensList(java.lang.String[] tokensList) {
            this.tokensList = tokensList;
        }
    }
    
    class WSBaseType implements Serializable{
        private java.lang.String value;
        private  Map valueMap = new HashMap();
        public  final String _value1String = "string";
        public  final String _value2String = "int";
        public  final String _value3String = "token";
        
        public  final java.lang.String _value1 = new java.lang.String(_value1String);
        public  final java.lang.String _value2 = new java.lang.String(_value2String);
        public  final java.lang.String _value3 = new java.lang.String(_value3String);
        
        public  final WSBaseType value1 = new WSBaseType(_value1);
        public  final WSBaseType value2 = new WSBaseType(_value2);
        public  final WSBaseType value3 = new WSBaseType(_value3);
        
        protected WSBaseType(java.lang.String value) {
            this.value = value;
            valueMap.put(this.toString(), this);
        }
        
        public java.lang.String getValue() {
            return value;
        }
        
        public  WSBaseType fromValue(java.lang.String value)
            throws java.lang.IllegalStateException {
            if (value1.value.equals(value)) {
                return value1;
            } else if (value2.value.equals(value)) {
                return value2;
            } else if (value3.value.equals(value)) {
                return value3;
            }
            throw new IllegalArgumentException();
        }
        
        public  WSBaseType fromString(String value)
            throws java.lang.IllegalStateException {
            WSBaseType ret = (WSBaseType)valueMap.get(value);
            if (ret != null) {
                return ret;
            }
            if (value.equals(_value1String)) {
                return value1;
            } else if (value.equals(_value2String)) {
                return value2;
            } else if (value.equals(_value3String)) {
                return value3;
            }
            throw new IllegalArgumentException();
        }
        
        public String toString() {
            return value.toString();
        }
        
        private Object readResolve()
            throws java.io.ObjectStreamException {
            return fromValue(getValue());
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof WSBaseType)) {
                return false;
            }
            return ((WSBaseType)obj).value.equals(value);
        }
        
        public int hashCode() {
            return value.hashCode();
        }
    }
    
}
