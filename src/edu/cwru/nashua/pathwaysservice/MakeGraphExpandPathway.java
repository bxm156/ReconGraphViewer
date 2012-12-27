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
 *         &lt;element name="steps" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "pathwayId",
        "steps"
        })
@XmlRootElement(name = "MakeGraphExpandPathway")
public class MakeGraphExpandPathway {

    protected String pathwayId;
    protected int steps;

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
     * Gets the value of the steps property.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Sets the value of the steps property.
     */
    public void setSteps(int value) {
        this.steps = value;
    }

}
