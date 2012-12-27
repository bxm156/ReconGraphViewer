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
 *         &lt;element name="MakeGraphResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphResult"
        })
@XmlRootElement(name = "MakeGraphResponse")
public class MakeGraphResponse {

    @XmlElement(name = "MakeGraphResult")
    protected String makeGraphResult;

    /**
     * Gets the value of the makeGraphResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphResult() {
        return makeGraphResult;
    }

    /**
     * Sets the value of the makeGraphResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphResult(String value) {
        this.makeGraphResult = value;
    }

}
