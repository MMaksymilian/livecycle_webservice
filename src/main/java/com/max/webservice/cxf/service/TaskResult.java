package com.max.webservice.cxf.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: USER
 * Date: 20.09.12
 * Time: 09:57
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlSeeAlso(java.util.LinkedHashMap.class)
public class TaskResult {

    LinkedHashMap<String, Object> results = new LinkedHashMap<String, Object>();

    @XmlElementWrapper(name="resultEntities")
    public LinkedHashMap<String, Object> getResults() {
        return results;
    }

    public void setResults(LinkedHashMap<String, Object> results) {
        this.results = results;
    }

}
