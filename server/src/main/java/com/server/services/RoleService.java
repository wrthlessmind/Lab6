package com.server.services;

import com.server.exceptions.ResponseException;
import com.server.repositories.RoleDAO;
import com.shared.entities.Role;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class RoleService {
    private final RoleDAO roleDAO = RoleDAO.getInstance();

    public Response create(Role role) {
        if (role.name() == null || role.name().isBlank())
            throw new ResponseException("Role name cannot be empty!");
        Role created = roleDAO.save(role);
        return new Response(true, "Role created successfully", JsonUtil.toJson(created));
    }

    public Role getById(int id) {
        return roleDAO.findById(id)
                .orElseThrow(() -> new ResponseException("Role with id " + id + " not found!"));
    }

    public Response delete(int id) {
        if (!roleDAO.existsById(id))
            throw new ResponseException("Role with id " + id + " not found!");
        roleDAO.delete(id);
        return new Response(true, "Role deleted successfully", null);
    }

    public Response getAll() {
        return new Response(true, "Roles retrieved successfully",
                JsonUtil.toJson(roleDAO.findAll()));
    }
}