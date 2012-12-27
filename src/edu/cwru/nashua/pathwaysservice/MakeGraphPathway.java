package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="pathwayId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organismName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "pathwayId",
        "organismName"
        })
@XmlRootElement(name = "MakeGraphPathway")
public class MakeGraphPathway {

    protected String pathwayId;
    protected String organismName;

    /**
     * Gets the value of the pathwayId property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getPathwayId() {
        return pathwayId;
    }

    /**
     * Sets the value of the pathwayId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPathwayId(String value) {
        this.pathwayId = value;
    }

    /**
     * Gets the value of the organismName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getOrganismName() {
        return organismName;
    }

    /**
     * Sets the value of the organismName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOrganismName(String value) {
        this.organismName = value;
    }

}
