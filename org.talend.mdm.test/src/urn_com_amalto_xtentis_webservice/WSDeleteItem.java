/**
 * WSDeleteItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package urn_com_amalto_xtentis_webservice;


/**
 * Deletes an item based on its cluster pk and its key(s)
 */
public class WSDeleteItem  implements java.io.Serializable {
    private urn_com_amalto_xtentis_webservice.WSItemPK wsItemPK;

    public WSDeleteItem() {
    }

    public WSDeleteItem(
           urn_com_amalto_xtentis_webservice.WSItemPK wsItemPK) {
           this.wsItemPK = wsItemPK;
    }


    /**
     * Gets the wsItemPK value for this WSDeleteItem.
     * 
     * @return wsItemPK
     */
    public urn_com_amalto_xtentis_webservice.WSItemPK getWsItemPK() {
        return wsItemPK;
    }


    /**
     * Sets the wsItemPK value for this WSDeleteItem.
     * 
     * @param wsItemPK
     */
    public void setWsItemPK(urn_com_amalto_xtentis_webservice.WSItemPK wsItemPK) {
        this.wsItemPK = wsItemPK;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSDeleteItem)) return false;
        WSDeleteItem other = (WSDeleteItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.wsItemPK==null && other.getWsItemPK()==null) || 
             (this.wsItemPK!=null &&
              this.wsItemPK.equals(other.getWsItemPK())));
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
        if (getWsItemPK() != null) {
            _hashCode += getWsItemPK().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSDeleteItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSDeleteItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wsItemPK");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wsItemPK"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn-com-amalto-xtentis-webservice", "WSItemPK"));
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
