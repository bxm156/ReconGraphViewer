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
 *         &lt;element name="MakeGraphPathwayLinksResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphPathwayLinksResult"
        })
@XmlRootElement(name = "MakeGraphPathwayLinksResponse")
public class MakeGraphPathwayLinksResponse {

    @XmlElement(name = "MakeGraphPathwayLinksResult")
    protected String makeGraphPathwayLinksResult;

    /**
     * Gets the value of the makeGraphPathwayLinksResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphPathwayLinksResult() {
        return makeGraphPathwayLinksResult;
    }

    /**
     * Sets the value of the makeGraphPathwayLinksResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphPathwayLinksResult(String value) {
        this.makeGraphPathwayLinksResult = value;
    }

}
