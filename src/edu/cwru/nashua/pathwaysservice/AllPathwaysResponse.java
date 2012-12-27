
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
 *         &lt;element name="AllPathwaysResult" type="{http://nashua.cwru.edu/PathwaysService/}ArrayOfSoapPathway" minOccurs="0"/>
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
    "allPathwaysResult"
})
@XmlRootElement(name = "AllPathwaysResponse")
public class AllPathwaysResponse {

    @XmlElement(name = "AllPathwaysResult")
    protected ArrayOfSoapPathway allPathwaysResult;

    /**
     * Gets the value of the allPathwaysResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSoapPathway }
     *     
     */
    public ArrayOfSoapPathway getAllPathwaysResult() {
        return allPathwaysResult;
    }

    /**
     * Sets the value of the allPathwaysResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSoapPathway }
     *     
     */
    public void setAllPathwaysResult(ArrayOfSoapPathway value) {
        this.allPathwaysResult = value;
    }

}
