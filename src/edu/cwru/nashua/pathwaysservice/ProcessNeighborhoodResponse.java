
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
 *         &lt;element name="ProcessNeighborhoodResult" type="{http://nashua.cwru.edu/PathwaysService/}ArrayOfSoapProcess" minOccurs="0"/>
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
    "processNeighborhoodResult"
})
@XmlRootElement(name = "ProcessNeighborhoodResponse")
public class ProcessNeighborhoodResponse {

    @XmlElement(name = "ProcessNeighborhoodResult")
    protected ArrayOfSoapProcess processNeighborhoodResult;

    /**
     * Gets the value of the processNeighborhoodResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSoapProcess }
     *     
     */
    public ArrayOfSoapProcess getProcessNeighborhoodResult() {
        return processNeighborhoodResult;
    }

    /**
     * Sets the value of the processNeighborhoodResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSoapProcess }
     *     
     */
    public void setProcessNeighborhoodResult(ArrayOfSoapProcess value) {
        this.processNeighborhoodResult = value;
    }

}
