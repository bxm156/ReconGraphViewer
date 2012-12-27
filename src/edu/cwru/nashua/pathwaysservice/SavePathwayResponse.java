
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
 *         &lt;element name="SavePathwayResult" type="{http://nashua.cwru.edu/PathwaysService/}SoapPathway" minOccurs="0"/>
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
    "savePathwayResult"
})
@XmlRootElement(name = "SavePathwayResponse")
public class SavePathwayResponse {

    @XmlElement(name = "SavePathwayResult")
    protected SoapPathway savePathwayResult;

    /**
     * Gets the value of the savePathwayResult property.
     * 
     * @return
     *     possible object is
     *     {@link SoapPathway }
     *     
     */
    public SoapPathway getSavePathwayResult() {
        return savePathwayResult;
    }

    /**
     * Sets the value of the savePathwayResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoapPathway }
     *     
     */
    public void setSavePathwayResult(SoapPathway value) {
        this.savePathwayResult = value;
    }

}
