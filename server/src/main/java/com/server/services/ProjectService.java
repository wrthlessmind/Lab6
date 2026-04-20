package com.server.services;

import com.server.exceptions.ResponseException;
import com.server.repositories.ProjectDAO;
import com.shared.entities.Project;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class ProjectService {
    private static final ProjectService INSTANCE = new ProjectService();

    private final ProjectDAO projectDAO = ProjectDAO.getInstance();

    private ProjectService() {}

    public static ProjectService getInstance() {
        return INSTANCE;
    }

    public Response create(Project project) {
        if (project.name() == null || project.name().isBlank())
            throw new ResponseException("Project name cannot be empty!");
        Project created = projectDAO.save(project);
        return new Response(true, "Project created successfully", JsonUtil.toJson(created));
    }

    public Project getById(int id) {
        return projectDAO.findById(id)
                .orElseThrow(() -> new ResponseException("Project with id " + id + " not found!"));
    }

    public Response update(Project project) {
        if (!projectDAO.existsById(project.id()))
            throw new ResponseException("Project with id " + project.id() + " not found!");
        Project updated = projectDAO.update(project);
        return new Response(true, "Project updated successfully", JsonUtil.toJson(updated));
    }

    public Response delete(int id) {
        if (!projectDAO.existsById(id))
            throw new ResponseException("Project with id " + id + " not found!");
        projectDAO.delete(id);
        return new Response(true, "Project deleted successfully", null);
    }

    public Response getAll() {
        return new Response(true, "Projects retrieved successfully",
                JsonUtil.toJson(projectDAO.findAll()));
    }
}