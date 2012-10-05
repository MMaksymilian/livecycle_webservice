package com.max.webservice.cxf.service;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: USER
 * Date: 05.10.12
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlSeeAlso(java.util.LinkedHashMap.class)
public class VariablesResult {

    private LinkedHashMap<String, String> processVariables = new LinkedHashMap<String, String>();

    private String taskId;

    @XmlElementWrapper(name="resultVariables")
    public LinkedHashMap<String, String> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(LinkedHashMap<String, String> processVariables) {
        this.processVariables = processVariables;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
