
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSWhereOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSWhereOperator">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="JOIN"/>
 *     &lt;enumeration value="CONTAINS"/>
 *     &lt;enumeration value="STARTSWITH"/>
 *     &lt;enumeration value="STRICTCONTAINS"/>
 *     &lt;enumeration value="EQUALS"/>
 *     &lt;enumeration value="NOT_EQUALS"/>
 *     &lt;enumeration value="GREATER_THAN"/>
 *     &lt;enumeration value="GREATER_THAN_OR_EQUAL"/>
 *     &lt;enumeration value="LOWER_THAN"/>
 *     &lt;enumeration value="LOWER_THAN_OR_EQUAL"/>
 *     &lt;enumeration value="NO_OPERATOR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSWhereOperator")
@XmlEnum
public enum WSWhereOperator {

    JOIN,
    CONTAINS,
    STARTSWITH,
    STRICTCONTAINS,
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LOWER_THAN,
    LOWER_THAN_OR_EQUAL,
    NO_OPERATOR;

    public String value() {
        return name();
    }

    public static WSWhereOperator fromValue(String v) {
        return valueOf(v);
    }

}
