package com.server.controllers;

import com.server.services.RoleService;
import com.shared.entities.Role;
import com.shared.network.Request;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

public class RoleController {
    private final RoleService roleService = new RoleService();

    public Response create(Request request) {
        Role role = JsonUtil.fromJson(request.getData(), Role.class);
        return roleService.create(role);
    }

    public Response delete(Request request) {
        int id = JsonUtil.fromJson(request.getData(), Integer.class);
        return roleService.delete(id);
    }

    public Response getAll() {
        return roleService.getAll();
    }
}