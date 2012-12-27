
package edu.cwru.nashua.pathwaysservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSoapProcess complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSoapProcess">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SoapProcess" type="{http://nashua.cwru.edu/PathwaysService/}SoapProcess" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSoapProcess", propOrder = {
    "soapProcess"
})
public class ArrayOfSoapProcess {

    @XmlElement(name = "SoapProcess", nillable = true)
    protected List<SoapProcess> soapProcess;

    /**
     * Gets the value of the soapProcess property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soapProcess property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoapProcess().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SoapProcess }
     * 
     * 
     */
    public List<SoapProcess> getSoapProcess() {
        if (soapProcess == null) {
            soapProcess = new ArrayList<SoapProcess>();
        }
        return this.soapProcess;
    }

}
