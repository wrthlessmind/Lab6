package com.server.controllers;

import com.server.services.AssignmentService;
import com.server.services.EmployeeService;
import com.server.services.ProjectService;
import com.shared.network.Request;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class AssignmentController {
    // Пункт 2: используем те же экземпляры сервисов, что и другие контроллеры
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final ProjectService projectService = ProjectService.getInstance();
    private final AssignmentService assignmentService =
            new AssignmentService(employeeService, projectService);

    public Response assign(Request request) {
        int[] ids = JsonUtil.fromJson(request.getData(), int[].class);
        return assignmentService.assign(ids[0], ids[1]);
    }

    public Response remove(Request request) {
        int[] ids = JsonUtil.fromJson(request.getData(), int[].class);
        return assignmentService.remove(ids[0], ids[1]);
    }

    public Response getEmployeesByProject(Request request) {
        int projectId = JsonUtil.fromJson(request.getData(), Integer.class);
        return assignmentService.getEmployeesByProject(projectId);
    }
}