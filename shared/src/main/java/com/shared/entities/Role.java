package com.shared.entities;

import java.io.Serializable;

public record Role(
        int id,
        String name
) implements Serializable {}