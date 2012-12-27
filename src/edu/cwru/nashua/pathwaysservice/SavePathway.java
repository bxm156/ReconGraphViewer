
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="pathway" type="{http://nashua.cwru.edu/PathwaysService/}SoapPathway" minOccurs="0"/>
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
    "pathway"
})
@XmlRootElement(name = "SavePathway")
public class SavePathway {

    protected SoapPathway pathway;

    /**
     * Gets the value of the pathway property.
     * 
     * @return
     *     possible object is
     *     {@link SoapPathway }
     *     
     */
    public SoapPathway getPathway() {
        return pathway;
    }

    /**
     * Sets the value of the pathway property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoapPathway }
     *     
     */
    public void setPathway(SoapPathway value) {
        this.pathway = value;
    }

}
