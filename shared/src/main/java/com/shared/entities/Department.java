package com.shared.entities;

import java.io.Serializable;

public record Department(
        int id,
        String name
) implements Serializable {}