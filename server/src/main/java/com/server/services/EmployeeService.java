package com.server.services;

import com.server.exceptions.ResponseException;
import com.server.repositories.DepartmentDAO;
import com.server.repositories.EmployeeDAO;
import com.server.repositories.RoleDAO;
import com.shared.entities.Employee;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class EmployeeService {
    private static final EmployeeService INSTANCE = new EmployeeService();

    private final EmployeeDAO employeeDAO = EmployeeDAO.getInstance();
    private final RoleDAO roleDAO = RoleDAO.getInstance();
    private final DepartmentDAO departmentDAO = DepartmentDAO.getInstance();

    private EmployeeService() {}

    public static EmployeeService getInstance() {
        return INSTANCE;
    }

    public Response create(Employee emp) {
        if (emp.name() == null || emp.name().isBlank())
            throw new ResponseException("Employee name cannot be empty!");
        if (!roleDAO.existsById(emp.roleId()))
            throw new ResponseException("Role with id " + emp.roleId() + " not found!");
        if (!departmentDAO.existsById(emp.departmentId()))
            throw new ResponseException("Department with id " + emp.departmentId() + " not found!");
        Employee created = employeeDAO.save(emp);
        return new Response(true, "Employee created successfully", JsonUtil.toJson(created));
    }

    public Response read(int id) {
        Employee emp = employeeDAO.findById(id)
                .orElseThrow(() -> new ResponseException("Employee with id " + id + " not found!"));
        return new Response(true, "Employee found", JsonUtil.toJson(emp));
    }

    public Employee getById(int id) {
        return employeeDAO.findById(id)
                .orElseThrow(() -> new ResponseException("Employee with id " + id + " not found!"));
    }

    public Response update(Employee emp) {
        if (!employeeDAO.existsById(emp.id()))
            throw new ResponseException("Employee with id " + emp.id() + " not found!");
        if (emp.name() == null || emp.name().isBlank())
            throw new ResponseException("Employee name cannot be empty!");
        if (!roleDAO.existsById(emp.roleId()))
            throw new ResponseException("Role with id " + emp.roleId() + " not found!");
        if (!departmentDAO.existsById(emp.departmentId()))
            throw new ResponseException("Department with id " + emp.departmentId() + " not found!");
        Employee updated = employeeDAO.update(emp);
        return new Response(true, "Employee updated successfully", JsonUtil.toJson(updated));
    }

    public Response delete(int id) {
        if (!employeeDAO.existsById(id))
            throw new ResponseException("Employee with id " + id + " not found!");
        employeeDAO.delete(id);
        return new Response(true, "Employee deleted successfully", null);
    }

    public Response getAll() {
        return new Response(true, "Employees retrieved successfully",
                JsonUtil.toJson(employeeDAO.findAll()));
    }
}