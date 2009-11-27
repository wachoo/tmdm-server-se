
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationPlanPKArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationPlanPKArray">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsSynchronizationPlanPK" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationPlanPK" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationPlanPKArray", propOrder = {
    "wsSynchronizationPlanPK"
})
public class WSSynchronizationPlanPKArray {

    @XmlElement(required = true)
    protected List<WSSynchronizationPlanPK> wsSynchronizationPlanPK;

    /**
     * Gets the value of the wsSynchronizationPlanPK property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsSynchronizationPlanPK property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsSynchronizationPlanPK().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSSynchronizationPlanPK }
     * 
     * 
     */
    public List<WSSynchronizationPlanPK> getWsSynchronizationPlanPK() {
        if (wsSynchronizationPlanPK == null) {
            wsSynchronizationPlanPK = new ArrayList<WSSynchronizationPlanPK>();
        }
        return this.wsSynchronizationPlanPK;
    }

}
