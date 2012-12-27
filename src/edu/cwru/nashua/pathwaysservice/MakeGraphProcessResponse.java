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
 *         &lt;element name="MakeGraphProcessResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphProcessResult"
        })
@XmlRootElement(name = "MakeGraphProcessResponse")
public class MakeGraphProcessResponse {

    @XmlElement(name = "MakeGraphProcessResult")
    protected String makeGraphProcessResult;

    /**
     * Gets the value of the makeGraphProcessResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphProcessResult() {
        return makeGraphProcessResult;
    }

    /**
     * Sets the value of the makeGraphProcessResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphProcessResult(String value) {
        this.makeGraphProcessResult = value;
    }

}
