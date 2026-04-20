package com.shared.entities;

import java.io.Serializable;

public record Employee(
        int id,
        String name,
        int roleId,
        int departmentId,
        double salary
) implements Serializable {}