
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
    "getImmediateChildrenResult"
})
@XmlRootElement(name = "GetImmediateChildrenResponse")
public class GetImmediateChildrenResponse {

    @XmlElement(name = "GetImmediateChildrenResult")
    protected ArrayOfSoapCompartment getImmediateChildrenResult;

    /**
     * Gets the value of the allPathwaysResult property.
     *
     * @return
     *     possible object is
     *     {@link edu.cwru.nashua.pathwaysservice.ArrayOfSoapPathway }
     *
     */
    public ArrayOfSoapCompartment getImmediateChildrenResult() {
        return getImmediateChildrenResult;
    }

    /**
     * Sets the value of the allPathwaysResult property.
     *
     * @param value
     *     allowed object is
     *     {@link edu.cwru.nashua.pathwaysservice.ArrayOfSoapPathway }
     *
     */
    public void setImmediateChildrenResult(ArrayOfSoapCompartment value) {
        this.getImmediateChildrenResult = value;
    }

}