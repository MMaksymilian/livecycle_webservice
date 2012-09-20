package com.max.webservice.cxf.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: USER
 * Date: 20.09.12
 * Time: 09:57
 * To change this template use File | Settings | File Templates.
 */
public class ProcessVariable {

    String attribute;

    String value;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
