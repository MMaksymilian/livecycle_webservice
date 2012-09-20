package com.max.webservice.cxf.service.impl;

import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.taskmanager.dsc.client.TaskManagerClientFactory;
import com.adobe.idp.taskmanager.dsc.client.TaskManagerQueryService;
import com.adobe.idp.taskmanager.dsc.client.query.*;
import com.adobe.idp.taskmanager.dsc.client.task.TaskInfo;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManager;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManagerException;
import com.max.webservice.cxf.service.HelloWorld;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jws.WebService;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Maksymilian Małek
 * Date: 10.07.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@WebService(endpointInterface = "com.max.webservice.cxf.service.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    @Autowired
    @Qualifier("connectionProperties")
    Properties connectionProps;

    @Override
    public Object[] getProcessVariablesForUserWithEmail (final String email) throws TaskQueryServiceException, TaskManagerException {

        ServiceClientFactory myFactory = ServiceClientFactory.createInstance(connectionProps);
        TaskManagerQueryService queryManager = TaskManagerClientFactory.getQueryManager(myFactory);
        TaskManager myTaskManager = TaskManagerClientFactory.getTaskManager(myFactory);

        //WYSZUKIWANIE PROCESÓW
        ProcessSearchFilter processFilter = new ProcessSearchFilter();
        List<ProcessInstanceRow> processes = queryManager.processSearch(processFilter);

        //WYSZUKIWANIE UŻYTKOWNIKÓW
        UserSearchFilter userSearchFilter = new UserSearchFilter();
        List<TMUser> users = queryManager.userSearch(userSearchFilter);
        TMUser tmUser = (TMUser)CollectionUtils.find(users,  new Predicate() {

            @Override
            public boolean evaluate(Object o) {
                TMUser tmUser = (TMUser) o;
                return ((TMUser) o).getEmail().equals(email);
            }
        });
        tmUser.getId();
        TaskFilter filter = queryManager.newTaskFilter();
        filter.setUserId(tmUser.getId());
        List result = queryManager.taskList(filter);
        Iterator iter = result.iterator();
        int i = 0;
        while (iter.hasNext()) {
            TaskRow myTask = (TaskRow) iter.next();
            long taskId = myTask.getTaskId();
            final TaskInfo taskInfo = myTaskManager.getTaskInfo(taskId);
            ProcessInstanceRow processInstanceRow = (ProcessInstanceRow) CollectionUtils.find(processes, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    return ((ProcessInstanceRow) o).getProcessInstanceId() == taskInfo.getProcessInstanceId();
                }
            }) ;
            processInstanceRow.getProcessVariables();
            //TODO zwrócenie zmiennych procesowych
        }


//        QName rootName = new QName("http://ws.apache.org/processV", "processVariables", "");
//
//        List<QName> elements = Lists.newArrayList(new QName("http://ws.apache.org/processV", "first", ""), new QName("http://ws.apache.org/processV", "second", ""));
//
//        Element root = XmlUtils.createElement(rootName);
//        String value = "";
//        Element firstChild = XmlUtils.createElement(elements.get(0), "test");
//        Element secondChild = XmlUtils.createElement(elements.get(1), "test");
//        root.appendChild(secondChild);
//        root.appendChild(firstChild);

        Object[] array = new Object[4];
        array[0] = "param1";
        array[1] = new Integer(3);
        return array;
    }

}
