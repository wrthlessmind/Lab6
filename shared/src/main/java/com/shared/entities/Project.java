package com.shared.entities;

import java.io.Serializable;

public record Project(
        int id,
        String name,
        String deadline,
        String status
) implements Serializable {}