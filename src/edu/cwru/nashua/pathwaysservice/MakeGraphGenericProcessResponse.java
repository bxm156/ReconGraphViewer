package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MakeGraphGenericProcessResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphGenericProcessResult"
        })
@XmlRootElement(name = "MakeGraphGenericProcessResponse")
public class MakeGraphGenericProcessResponse {

    @XmlElement(name = "MakeGraphGenericProcessResult")
    protected String makeGraphGenericProcessResult;

    /**
     * Gets the value of the makeGraphGenericProcessResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphGenericProcessResult() {
        return makeGraphGenericProcessResult;
    }

    /**
     * Sets the value of the makeGraphGenericProcessResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphGenericProcessResult(String value) {
        this.makeGraphGenericProcessResult = value;
    }

}
