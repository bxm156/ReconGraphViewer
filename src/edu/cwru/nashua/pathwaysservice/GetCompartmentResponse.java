
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetPathwayResult" type="{http://nashua.cwru.edu/PathwaysService/}SoapPathway" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCompartmentResult"
})
@XmlRootElement(name = "GeCompartmentResponse")
public class GetCompartmentResponse {

    @XmlElement(name = "GetCompartmentResult")
    protected SoapCompartment getCompartmentResult;

    /**
     * Gets the value of the getPathwayResult property.
     *
     * @return
     *     possible object is
     *     {@link edu.cwru.nashua.pathwaysservice.SoapPathway }
     *
     */
    public SoapCompartment getGetCompartmentResult() {
        return getCompartmentResult;
    }

    /**
     * Sets the value of the getPathwayResult property.
     *
     * @param value
     *     allowed object is
     *     {@link edu.cwru.nashua.pathwaysservice.SoapPathway }
     *
     */
    public void setGetCompartmentResult(SoapCompartment value) {
        this.getCompartmentResult = value;
    }

}