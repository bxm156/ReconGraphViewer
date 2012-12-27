
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
 *         &lt;element name="InsertBugResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "insertBugResult"
})
@XmlRootElement(name = "InsertBugResponse")
public class InsertBugResponse {

    @XmlElement(name = "InsertBugResult")
    protected int insertBugResult;

    /**
     * Gets the value of the loginResult property.
     * 
     */
    public int isInsertBugResult() {
        return insertBugResult;
    }

    /**
     * Sets the value of the loginResult property.
     * 
     */
    public void setInsertBugResult(int value) {
        this.insertBugResult = value;
    }

}
