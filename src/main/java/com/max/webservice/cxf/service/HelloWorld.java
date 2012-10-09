package com.max.webservice.cxf.service;

import com.adobe.idp.taskmanager.dsc.client.query.TaskQueryServiceException;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManagerException;
import com.adobe.idp.um.api.UMException;

import javax.jws.WebService;

/**
 * Created with IntelliJ IDEA.
 * User: Maksymilian Małek
 * Date: 10.07.12
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
@WebService
public interface HelloWorld {
    TaskResult[] getTaskDataForUserWithEmail(String email) throws Exception;

    VariablesResult[] getProcessVariablesForUser(String login, String password) throws Exception;

    InsertUsersResult insertUsers(int number, String root) throws UMException;
}
