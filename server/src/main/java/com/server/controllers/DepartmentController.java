package com.server.controllers;

import com.server.services.DepartmentService;
import com.shared.entities.Department;
import com.shared.network.Request;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class DepartmentController {
    private final DepartmentService departmentService = new DepartmentService();

    public Response create(Request request) {
        Department dept = JsonUtil.fromJson(request.getData(), Department.class);
        return departmentService.create(dept);
    }

    public Response delete(Request request) {
        int id = JsonUtil.fromJson(request.getData(), Integer.class);
        return departmentService.delete(id);
    }

    public Response getAll() {
        return departmentService.getAll();
    }
}