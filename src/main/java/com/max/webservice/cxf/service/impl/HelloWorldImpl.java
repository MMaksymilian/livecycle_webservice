package com.max.webservice.cxf.service.impl;

import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.filter.Operator;
import com.adobe.idp.taskmanager.dsc.client.TaskManagerClientFactory;
import com.adobe.idp.taskmanager.dsc.client.TaskManagerQueryService;
import com.adobe.idp.taskmanager.dsc.client.query.*;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManager;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManagerException;
import com.max.webservice.cxf.service.HelloWorld;
import com.max.webservice.cxf.service.TaskResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jws.WebService;
import java.util.*;

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
    public TaskResult[] getProcessVariablesForUserWithEmail(final String email) throws Exception, TaskManagerException {
        ServiceClientFactory myFactory = ServiceClientFactory.createInstance(connectionProps);
        TaskManagerQueryService queryManager = TaskManagerClientFactory.getQueryManager(myFactory);
        TaskManager myTaskManager = TaskManagerClientFactory.getTaskManager(myFactory);

        //WYSZUKIWANIE UŻYTKOWNIKÓW
        UserSearchFilter userSearchFilter = new UserSearchFilter();
        List<TMUser> users = queryManager.userSearch(userSearchFilter);
        TMUser tmUser = (TMUser) CollectionUtils.find(users, new Predicate() {

            @Override
            public boolean evaluate(Object o) {
                TMUser tmUser = (TMUser) o;
                if (((TMUser) o).getEmail() == null) {
                    return false;
                }
                return ((TMUser) o).getEmail().equals(email);
            }
        });
        if (tmUser == null) {
            throw new Exception("No such user with email: " + email);
        }

        TaskSearchFilter filt = new TaskSearchFilter();
        filt.addCondition(TaskSearchingConstants.pASSIGNMENT_QUEUE_OWNER, Operator.EQUALS, tmUser.getLoginId());
//        filt.addCondition(TaskSearchingConstants.pPROCESS_INSTANCE_ID, Operator.EQUALS,new Long(processInstance.getProcessInstanceId()));
        List<TaskRow> userTasks = queryManager.taskSearch(filt);
        CollectionUtils.filter(userTasks, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return ((TaskRow) o).getTaskStatus() == StatusFilter.completed;
            }
        });
        Collections.sort(userTasks, new TaskComparator());
        TaskResult[] resultTab = new TaskResult[userTasks.size()];
        int index = 0;
        for (TaskRow task : userTasks) {
            /*Działa tylko dla obecnie zalogowanego użytkownika, tzn tego z properties'ów - głupie nie?*/
//            List variables = queryManager.getProcessVariableValues(new Long(task.getTaskId() + 1));
            TaskResult tResult = new TaskResult();
            resultTab[index] = tResult;
            tResult.getResults().put("description", task.getDescription());
//            tResult.getResults().put("deadline", task.getDeadline());
            tResult.getResults().put("creation", task.getCurrentAssignment().getAssignmentCreateTime().getTime());
            tResult.getResults().put("updated", task.getCurrentAssignment().getAssignmentUpdateTime().getTime());
            tResult.getResults().put("process", task.getProcessName());
            tResult.getResults().put("instanceId", task.getProcessInstanceId())
//            resultTab[index] = new String[12];
//            resultTab[index][0] = "description";
//            resultTab[index][1] = task.getDescription();
//            resultTab[index][2] = "deadline";
//            resultTab[index][4] = "creation";
//            resultTab[index][3] = new XmlCalendar(task.getDeadline());
//            resultTab[index][5] = task.getCurrentAssignment().getAssignmentCreateTime().getTime();
//            resultTab[index][6] = "updated";
//            resultTab[index][7] = task.getCurrentAssignment().getAssignmentUpdateTime().getTime();
//            resultTab[index][8] = "process";
//            resultTab[index][9] = task.getProcessName();
//            resultTab[index][10] = "instanceId";
//            resultTab[index][11] = task.getProcessInstanceId();
            index++;
        }
        return resultTab;
    }

    public class TaskComparator implements Comparator<TaskRow> {

        @Override
        public int compare(TaskRow o1, TaskRow o2) {
            return o2.getTaskCreateTime().compareTo(o1.getTaskCreateTime());
        }
    }

    public class ProcessComparator implements Comparator<ProcessInstanceRow> {

        @Override
        public int compare(ProcessInstanceRow o1, ProcessInstanceRow o2) {
            return o1.getProcessStartTime().compareTo(o2.getProcessStartTime());
        }
    }
}
