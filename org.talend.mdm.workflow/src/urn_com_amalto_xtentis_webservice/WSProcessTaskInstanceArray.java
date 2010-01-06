
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSProcessTaskInstanceArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSProcessTaskInstanceArray">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wstaskinstance" type="{urn-com-amalto-xtentis-webservice}WSProcessTaskInstance" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSProcessTaskInstanceArray", propOrder = {
    "wstaskinstance"
})
public class WSProcessTaskInstanceArray {

    @XmlElement(required = true)
    protected List<WSProcessTaskInstance> wstaskinstance;

    /**
     * Gets the value of the wstaskinstance property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wstaskinstance property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWstaskinstance().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSProcessTaskInstance }
     * 
     * 
     */
    public List<WSProcessTaskInstance> getWstaskinstance() {
        if (wstaskinstance == null) {
            wstaskinstance = new ArrayList<WSProcessTaskInstance>();
        }
        return this.wstaskinstance;
    }

}
