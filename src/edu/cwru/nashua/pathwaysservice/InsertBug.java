
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="callHierarchy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eMail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}byte" minOccurs="0"/>
 *         &lt;element name="bugType" type="{http://www.w3.org/2001/XMLSchema}byte" minOccurs="0"/>
 *         &lt;element name="bugTypeOther" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "description",
    "callHierarchy",
    "firstName",
    "lastName",
    "eMail",
    "phone",
    "severity",
    "bugType",
    "bugTypeOther"
})
@XmlRootElement(name = "InsertBug")
public class InsertBug {

	protected String description;
	protected String callHierarchy;
	protected String firstName;
	protected String lastName;
	protected String eMail;
	protected String phone;
	protected byte severity;
	protected byte bugType;
	protected String bugTypeOther;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String value) {
		description = value;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String value) {
		firstName = value;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String value) {
		lastName = value;
	}
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String value) {
		eMail = value;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String value) {
		phone = value;
	}
	public byte getSeverity() {
		return severity;
	}
	public void setSeverity(byte value) {
		severity = value;
	}
	public byte getBugType() {
		return bugType;
	}
	public void setBugType(byte value) {
		bugType = value;
	}
	public String getCallHierarchy() {
		return callHierarchy;
	}
	public void setCallHierarchy(String callHierarchy) {
		this.callHierarchy = callHierarchy;
	}
	public String getBugTypeOther() {
		return bugTypeOther;
	}
	public void setBugTypeOther(String value) {
		bugTypeOther = value;
	}
}
