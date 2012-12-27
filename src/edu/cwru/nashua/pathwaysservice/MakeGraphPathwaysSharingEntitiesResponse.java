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
 *         &lt;element name="MakeGraphPathwaysSharingEntitiesResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphPathwaysSharingEntitiesResult"
        })
@XmlRootElement(name = "MakeGraphPathwaysSharingEntitiesResponse")
public class MakeGraphPathwaysSharingEntitiesResponse {

    @XmlElement(name = "MakeGraphPathwaysSharingEntitiesResult")
    protected String makeGraphPathwaysSharingEntitiesResult;

    /**
     * Gets the value of the makeGraphPathwaysSharingEntitiesResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphPathwaysSharingEntitiesResult() {
        return makeGraphPathwaysSharingEntitiesResult;
    }

    /**
     * Sets the value of the makeGraphPathwaysSharingEntitiesResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphPathwaysSharingEntitiesResult(String value) {
        this.makeGraphPathwaysSharingEntitiesResult = value;
    }

}
