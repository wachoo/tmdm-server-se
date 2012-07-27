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
import javax.ws.rs.core.Response;
import java.util.*;

@Path(TaskService.TASKS)
public class TaskService {

    public static final String TASKS = "/tasks";

    private final Map<String, TaskStatus> database = new HashMap<String, TaskStatus>();

    public TaskService() {
        for (int i = 0; i < 100; i++) {
            String uuid = String.valueOf(UUID.randomUUID());
            database.put(uuid, new TaskStatus());
        }
    }

    /**
     * @return A list of tasks
     */
    @GET
    public List<String> list(String status) {
        return new ArrayList<String>(database.keySet());
    }

    @POST
    public String newTask() {
        String uuid = String.valueOf(UUID.randomUUID());
        database.put(uuid, new TaskStatus());
        return uuid;
    }

    @GET
    @Path("/{taskId}/")
    public String getTaskDetails(@PathParam("taskId") String taskId) {
        return taskId;
    }

    @PUT
    @Path("/{taskId}/")
    public void updateTask(@PathParam("taskId") String taskId) {
        System.out.println("");
    }

    @DELETE
    @Path("/{taskId}/")
    public void cancelTask(@PathParam("taskId") String taskId) {
        System.out.println();
    }
}
