package com.server.services;

import com.server.exceptions.ResponseException;
import com.server.repositories.AssignmentDAO;
import com.shared.entities.Employee;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

import java.util.List;

public class AssignmentService {
    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public AssignmentService(EmployeeService employeeService, ProjectService projectService) {
        this.employeeService = employeeService;
        this.projectService = projectService;
    }

    public Response assign(int projectId, int employeeId) {
        projectService.getById(projectId);
        employeeService.getById(employeeId);
        if (assignmentDAO.isAssigned(projectId, employeeId))
            throw new ResponseException("Employee is already assigned to this project!");
        assignmentDAO.assign(projectId, employeeId);
        return new Response(true, "Employee assigned to project successfully", null);
    }

    public Response remove(int projectId, int employeeId) {
        if (!assignmentDAO.isAssigned(projectId, employeeId))
            throw new ResponseException("Assignment not found!");
        assignmentDAO.remove(projectId, employeeId);
        return new Response(true, "Employee removed from project successfully", null);
    }

    public Response getEmployeesByProject(int projectId) {
        projectService.getById(projectId);
        List<Employee> result = assignmentDAO.findEmployeeIdsByProject(projectId).stream()
                .map(employeeService::getById)
                .toList();
        return new Response(true, "Employees retrieved successfully", JsonUtil.toJson(result));
    }
}