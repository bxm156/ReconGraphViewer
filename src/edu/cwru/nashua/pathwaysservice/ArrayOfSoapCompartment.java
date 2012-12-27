
package edu.cwru.nashua.pathwaysservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSoapPathway complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArrayOfSoapPathway">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SoapPathway" type="{http://nashua.cwru.edu/PathwaysService/}SoapPathway" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSoapCompartment", propOrder = {
    "soapCompartment"
})
public class ArrayOfSoapCompartment {

    @XmlElement(name = "SoapCompartment", nillable = true)
    protected List<SoapCompartment> soapCompartment;

    /**
     * Gets the value of the soapPathway property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soapPathway property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoapPathway().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link edu.cwru.nashua.pathwaysservice.SoapPathway }
     *
     *
     */
    public List<SoapCompartment> getSoapCompartment() {
//        if (soapCompartment == null) {
//            soapCompartment = new ArrayList<SoapCompartment>();
//        }
        return this.soapCompartment;
    }

    public void setSoapCompartment(List<SoapCompartment> value) {
        this.soapCompartment = value;
    }
}