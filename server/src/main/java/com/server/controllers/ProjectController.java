package com.server.controllers;

import com.server.services.ProjectService;
import com.shared.entities.Project;
import com.shared.network.Request;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class ProjectController {
    private final ProjectService projectService = ProjectService.getInstance();

    public Response create(Request request) {
        Project project = JsonUtil.fromJson(request.getData(), Project.class);
        return projectService.create(project);
    }

    public Response update(Request request) {
        Project project = JsonUtil.fromJson(request.getData(), Project.class);
        return projectService.update(project);
    }

    public Response delete(Request request) {
        int id = JsonUtil.fromJson(request.getData(), Integer.class);
        return projectService.delete(id);
    }

    public Response getAll() {
        return projectService.getAll();
    }
}