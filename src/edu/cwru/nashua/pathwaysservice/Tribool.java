
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for Tribool.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Tribool">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="False"/>
 *     &lt;enumeration value="True"/>
 *     &lt;enumeration value="Null"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum Tribool {

    @XmlEnumValue("False")
    FALSE("False"),
    @XmlEnumValue("True")
    TRUE("True"),
    @XmlEnumValue("Null")
    NULL("Null");
    private final String value;

    Tribool(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Tribool fromValue(String v) {
        for (Tribool c: Tribool.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
