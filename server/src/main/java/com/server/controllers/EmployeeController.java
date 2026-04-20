package com.server.controllers;

import com.server.services.EmployeeService;
import com.shared.entities.Employee;
import com.shared.network.Request;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class EmployeeController {
    private final EmployeeService employeeService = EmployeeService.getInstance();

    public Response create(Request request) {
        Employee emp = JsonUtil.fromJson(request.getData(), Employee.class);
        return employeeService.create(emp);
    }

    public Response read(Request request) {
        int id = JsonUtil.fromJson(request.getData(), Integer.class);
        return employeeService.read(id);
    }

    public Response update(Request request) {
        Employee emp = JsonUtil.fromJson(request.getData(), Employee.class);
        return employeeService.update(emp);
    }

    public Response delete(Request request) {
        int id = JsonUtil.fromJson(request.getData(), Integer.class);
        return employeeService.delete(id);
    }

    public Response getAll() {
        return employeeService.getAll();
    }
}