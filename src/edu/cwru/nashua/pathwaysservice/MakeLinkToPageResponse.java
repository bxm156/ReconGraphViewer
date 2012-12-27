
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
 *         &lt;element name="MakeLinkToPageResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "makeLinkToPageResult"
})
@XmlRootElement(name = "MakeLinkToPageResponse")
public class MakeLinkToPageResponse {

    @XmlElement(name = "MakeLinkToPageResult")
    protected String makeLinkToPageResult;

    /**
     * Gets the value of the makeLinkToPageResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMakeLinkToPageResult() {
        return makeLinkToPageResult;
    }

    /**
     * Sets the value of the makeLinkToPageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMakeLinkToPageResult(String value) {
        this.makeLinkToPageResult = value;
    }

}
