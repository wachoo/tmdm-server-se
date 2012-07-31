/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.servlet;

import javax.ws.rs.*;
import java.util.*;

@Path(TaskService.TASKS)
public class TaskService {

    public static final String TASKS = "/tasks";

    private final Map<String, TaskStatus> tasks = new HashMap<String, TaskStatus>();

    private final Map<String, List<ExecutionStatus>> executions = new HashMap<String, List<ExecutionStatus>>();

    public TaskService() {
        for (int i = 0; i < 100; i++) {
            String uuid = String.valueOf(UUID.randomUUID());
            newTask(uuid);
        }
    }

    private void newTask(String uuid) {
        TaskStatus status = new TaskStatus(uuid, "DataContainer", "SINGLE", "3d", "09/09/12 04:04:06");
        tasks.put(uuid, status);
        List<ExecutionStatus> executionList = Arrays.asList(newExecution(UUID.randomUUID().toString()),
                newExecution(UUID.randomUUID().toString()),
                newExecution(UUID.randomUUID().toString()));
        executions.put(uuid, executionList);
    }

    private ExecutionStatus newExecution(String id) {
        return new ExecutionStatus(id, 1000, "07/07/12 03:03:03", "07/07/12 04:03:03", "1h05m5s", 10, 100);
    }

    /*
     * TASK SERVICES
     */

    /**
     * @return A list of tasks
     */
    @GET
    public List<String> listTasks() {
        return new ArrayList<String>(tasks.keySet());
    }

    @POST
    public String newTask() {
        String newTaskUUID = UUID.randomUUID().toString();
        newTask(newTaskUUID);
        return newTaskUUID;
    }

    @GET
    @Path("/{taskId}/")
    public TaskStatus getTaskDetails(@PathParam("taskId") String taskId) {
        return tasks.get(taskId);
    }

    @PUT
    @Path("/{taskId}/")
    public String editTask(@PathParam("taskId") String taskId) {
        return taskId;
    }

    /*
     * TASK EXECUTIONS SERVICES
     */
    @GET
    @Path("/{taskId}/execs")
    public List<String> getTaskExecutions(@PathParam("taskId") String taskId) {
        List<String> executionIds = new LinkedList<String>();
        for (ExecutionStatus executionStatus : this.executions.get(taskId)) {
            executionIds.add(executionStatus.getId());
        }
        return executionIds;
    }

    @GET
    @Path("/{taskId}/execs/{execId}")
    public ExecutionStatus getTaskExecutionDetails(@PathParam("taskId") String taskId, @PathParam("execId") String execId) {
        List<ExecutionStatus> executionStatuses = executions.get(taskId);
        for (ExecutionStatus executionStatus : executionStatuses) {
            if (execId.equals(executionStatus.getId())) {
                return executionStatus;
            }
        }
        throw new IllegalArgumentException("Execution '" + execId + "' does not exist.");
    }

    @DELETE
    @Path("/{taskId}/execs/{execId}")
    public void cancelTaskExecution(@PathParam("taskId") String taskId, @PathParam("execId") String execId) {
    }
}
