/**
 * WSDeleteItems.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package urn_com_amalto_xtentis_webservice;


/**
 * Deletes items of a concept based on a query
 */
public class WSDeleteItems  implements java.io.Serializable {
    private urn_com_amalto_xtentis_webservice.WSDataClusterPK wsDataClusterPK;

    private java.lang.String conceptName;

    private urn_com_amalto_xtentis_webservice.WSWhereItem wsWhereItem;

    private int spellTreshold;

    public WSDeleteItems() {
    }

    public WSDeleteItems(
           urn_com_amalto_xtentis_webservice.WSDataClusterPK wsDataClusterPK,
           java.lang.String conceptName,
           urn_com_amalto_xtentis_webservice.WSWhereItem wsWhereItem,
           int spellTreshold) {
           this.wsDataClusterPK = wsDataClusterPK;
           this.conceptName = conceptName;
           this.wsWhereItem = wsWhereItem;
           this.spellTreshold = spellTreshold;
    }


    /**
     * Gets the wsDataClusterPK value for this WSDeleteItems.
     * 
     * @return wsDataClusterPK
     */
    public urn_com_amalto_xtentis_webservice.WSDataClusterPK getWsDataClusterPK() {
        return wsDataClusterPK;
    }


    /**
     * Sets the wsDataClusterPK value for this WSDeleteItems.
     * 
     * @param wsDataClusterPK
     */
    public void setWsDataClusterPK(urn_com_amalto_xtentis_webservice.WSDataClusterPK wsDataClusterPK) {
        this.wsDataClusterPK = wsDataClusterPK;
    }


    /**
     * Gets the conceptName value for this WSDeleteItems.
     * 
     * @return conceptName
     */
    public java.lang.String getConceptName() {
        return conceptName;
    }


    /**
     * Sets the conceptName value for this WSDeleteItems.
     * 
     * @param conceptName
     */
    public void setConceptName(java.lang.String conceptName) {
        this.conceptName = conceptName;
    }


    /**
     * Gets the wsWhereItem value for this WSDeleteItems.
     * 
     * @return wsWhereItem
     */
    public urn_com_amalto_xtentis_webservice.WSWhereItem getWsWhereItem() {
        return wsWhereItem;
    }


    /**
     * Sets the wsWhereItem value for this WSDeleteItems.
     * 
     * @param wsWhereItem
     */
    public void setWsWhereItem(urn_com_amalto_xtentis_webservice.WSWhereItem wsWhereItem) {
        this.wsWhereItem = wsWhereItem;
    }


    /**
     * Gets the spellTreshold value for this WSDeleteItems.
     * 
     * @return spellTreshold
     */
    public int getSpellTreshold() {
        return spellTreshold;
    }


    /**
     * Sets the spellTreshold value for this WSDeleteItems.
     * 
     * @param spellTreshold
     */
    public void setSpellTreshold(int spellTreshold) {
        this.spellTreshold = spellTreshold;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSDeleteItems)) return false;
        WSDeleteItems other = (WSDeleteItems) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.wsDataClusterPK==null && other.getWsDataClusterPK()==null) || 
             (this.wsDataClusterPK!=null &&
              this.wsDataClusterPK.equals(other.getWsDataClusterPK()))) &&
            ((this.conceptName==null && other.getConceptName()==null) || 
             (this.conceptName!=null &&
              this.conceptName.equals(other.getConceptName()))) &&
            ((this.wsWhereItem==null && other.getWsWhereItem()==null) || 
             (this.wsWhereItem!=null &&
              this.wsWhereItem.equals(other.getWsWhereItem()))) &&
            this.spellTreshold == other.getSpellTreshold();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getWsDataClusterPK() != null) {
            _hashCode += getWsDataClusterPK().hashCode();
        }
        if (getConceptName() != null) {
            _hashCode += getConceptName().hashCode();
        }
        if (getWsWhereItem() != null) {
            _hashCode += getWsWhereItem().hashCode();
        }
        _hashCode += getSpellTreshold();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSDeleteItems.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItems"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wsDataClusterPK");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wsDataClusterPK"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDataClusterPK"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conceptName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conceptName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wsWhereItem");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wsWhereItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSWhereItem"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("spellTreshold");
        elemField.setXmlName(new javax.xml.namespace.QName("", "spellTreshold"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
