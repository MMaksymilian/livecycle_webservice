package com.max.webservice.cxf.service.impl;

import com.adobe.idp.dsc.clientsdk.ServiceClient;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.idp.dsc.filter.Operator;
import com.adobe.idp.taskmanager.dsc.client.TaskManagerClientFactory;
import com.adobe.idp.taskmanager.dsc.client.TaskManagerQueryService;
import com.adobe.idp.taskmanager.dsc.client.query.*;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManager;
import com.adobe.idp.taskmanager.dsc.client.task.TaskManagerException;
import com.adobe.idp.um.api.DirectoryManager;
import com.adobe.idp.um.api.UMException;
import com.adobe.idp.um.api.infomodel.User;
import com.adobe.idp.um.api.infomodel.impl.UserImpl;
import com.adobe.livecycle.usermanager.client.DirectoryManagerServiceClient;
import com.max.webservice.cxf.service.HelloWorld;
import com.max.webservice.cxf.service.InsertUsersResult;
import com.max.webservice.cxf.service.TaskResult;
import com.max.webservice.cxf.service.VariablesResult;
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

    @Override
    public InsertUsersResult insertUsers(int number, String root) throws UMException {
        InsertUsersResult result = new InsertUsersResult();
                ServiceClientFactory scf = ServiceClientFactory.createInstance(connectionProps);
        DirectoryManager dirManager = new
                DirectoryManagerServiceClient(scf);

        String[] names = {"Paweł", "Olga", "Michał", "Maria", "Katarzyna", "Zuzanna", "Łukasz", "Piotr", "Anna"};
        String[] surnames = {"Bończyk", "Kowalczyk", "Bralczyk", "Drozd", "Stoch", "Stach"};
        Random generator = new Random();
        for(int index = 1 ; index <= number; index++) {
            UserImpl testUser = new UserImpl();
            String userId = root + index;
            testUser.setEmail(userId + "@sampleorganization.com");
            testUser.setUserid(userId);
            testUser.setDomainName("DefaultDom");
            testUser.setPrincipalType("USER");
            testUser.setGivenName(names[generator.nextInt(names.length)]);
            testUser.setFamilyName(surnames[generator.nextInt(surnames.length)]);
            testUser.setCanonicalName(userId);
            dirManager.createLocalUser(testUser, "password");
            result.getResults().add(testUser);
        }
        return result;
    }

    @Autowired
    @Qualifier("connectionProperties")
    Properties connectionProps;

    @Override
    public TaskResult[] getTaskDataForUserWithEmail(final String email) throws Exception, TaskManagerException {
        ServiceClientFactory myFactory = ServiceClientFactory.createInstance(connectionProps);
        TaskManagerQueryService queryManager = TaskManagerClientFactory.getQueryManager(myFactory);
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
        myFactory = ServiceClientFactory.createInstance(connectionProps);
        connectionProps.setProperty("DSC_CREDENTIAL_USERNAME", tmUser.getLoginId());
        queryManager = TaskManagerClientFactory.getQueryManager(myFactory);
//      myTaskManager = TaskManagerClientFactory.getTaskManager(myFactory);
        TaskSearchFilter filt = new TaskSearchFilter();
//        filt.addCondition(TaskSearchingConstants.pPRINCIPAL_DISPLAYNAME, Operator.EQUALS, tmUser.getDisplayName());
        List<TaskRow> userTasks = queryManager.taskSearch(filt);
        /*filtrowanie kolekcji - ewentualnie do dodania*/
//        CollectionUtils.filter(userTasks, new Predicate() {
//            @Override
//            public boolean evaluate(Object o) {
//                return ((TaskRow) o).getTaskStatus() == StatusFilter.completed;
//            }
//        });
        Collections.sort(userTasks, new TaskComparator());
        TaskResult[] resultTab = new TaskResult[userTasks.size()];
        int index = 0;
        for (TaskRow task : userTasks) {
            /*Działa tylko dla obecnie zalogowanego użytkownika, tzn. tego z properties'ów - głupie nie?*/
            List variablesValues = queryManager.getProcessVariableValues(new Long(task.getTaskId()));
            TaskResult tResult = new TaskResult();
            resultTab[index] = tResult;
            tResult.getResults().put("description", task.getDescription());
            if (task.getDeadline() != null) {
                tResult.getResults().put("deadline", task.getDeadline());
            }
            tResult.getResults().put("id", task.getTaskId());
            tResult.getResults().put("creation", task.getCurrentAssignment().getAssignmentCreateTime().getTime());
            tResult.getResults().put("updated", task.getCurrentAssignment().getAssignmentUpdateTime().getTime());
            tResult.getResults().put("process", task.getProcessName());
            tResult.getResults().put("instanceId", task.getProcessInstanceId());
            index++;
        }
        return resultTab;
    }

    @Override
    public VariablesResult[] getProcessVariablesForUser(String login, String password) throws Exception {
        ServiceClientFactory myFactory = ServiceClientFactory.createInstance(connectionProps);
        TaskManagerQueryService queryManager = TaskManagerClientFactory.getQueryManager(myFactory);
        connectionProps.setProperty("DSC_CREDENTIAL_USERNAME", login);
        connectionProps.setProperty("DSC_CREDENTIAL_PASSWORD", password);
        TaskSearchFilter filt = new TaskSearchFilter();
        List<TaskRow> userTasks = queryManager.taskSearch(filt);
        /*filtrowanie kolekcji - ewentualnie do dodania*/
//        CollectionUtils.filter(userTasks, new Predicate() {
//            @Override
//            public boolean evaluate(Object o) {
//                return ((TaskRow) o).getTaskStatus() == StatusFilter.completed;
//            }
//        });
        Collections.sort(userTasks, new TaskComparator());
        VariablesResult[] resultTab = new VariablesResult[userTasks.size()];
        int index = 0;
        for (TaskRow task : userTasks) {
            /*Działa tylko dla obecnie zalogowanego użytkownika, tzn. tego z properties'ów - głupie nie?*/
            List<MultiTypeVariable> variablesValues = queryManager.getProcessVariableValues(new Long(task.getTaskId()));
            VariablesResult variablesResult = new VariablesResult();
            variablesResult.setTaskId(task.getTaskId() + "");
            LinkedHashMap<String, String> processVariablesMap = new LinkedHashMap<String, String>();
            for(MultiTypeVariable multiTypeVariable: variablesValues) {
                processVariablesMap.put(multiTypeVariable.getName().toString(), multiTypeVariable.getValue() == null ? "null" : multiTypeVariable.getValue().toString());
            }
            variablesResult.setProcessVariables(processVariablesMap);
            resultTab[index] = variablesResult;
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
