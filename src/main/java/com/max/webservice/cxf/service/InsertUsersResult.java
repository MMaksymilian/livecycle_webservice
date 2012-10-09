package com.max.webservice.cxf.service;

import com.adobe.idp.um.api.infomodel.User;
import com.adobe.idp.um.api.infomodel.impl.UserImpl;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: USER
 * Date: 09.10.12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlSeeAlso(java.util.LinkedList.class)
public class InsertUsersResult {
    LinkedList<UserImpl> results = new LinkedList<UserImpl>();

    @XmlElementWrapper(name="resultEntities")
    public LinkedList<UserImpl> getResults() {
        return results;
    }

    public void setResults(LinkedList<UserImpl> results) {
        this.results = results;
    }
}
