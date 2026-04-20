package com.server.services;

import com.server.exceptions.ResponseException;
import com.server.repositories.DepartmentDAO;
import com.shared.entities.Department;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class DepartmentService {
    private final DepartmentDAO departmentDAO = DepartmentDAO.getInstance();

    public Response create(Department dept) {
        if (dept.name() == null || dept.name().isBlank())
            throw new ResponseException("Department name cannot be empty!");
        Department created = departmentDAO.save(dept);
        return new Response(true, "Department created successfully", JsonUtil.toJson(created));
    }

    public Department getById(int id) {
        return departmentDAO.findById(id)
                .orElseThrow(() -> new ResponseException("Department with id " + id + " not found!"));
    }

    public Response delete(int id) {
        if (!departmentDAO.existsById(id))
            throw new ResponseException("Department with id " + id + " not found!");
        departmentDAO.delete(id);
        return new Response(true, "Department deleted successfully", null);
    }

    public Response getAll() {
        return new Response(true, "Departments retrieved successfully",
                JsonUtil.toJson(departmentDAO.findAll()));
    }
}