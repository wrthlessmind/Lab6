package com.server.repositories;

import com.shared.entities.Role;

import java.io.*;
import java.util.*;

public class RoleDAO {
    private static final String FILE_PATH = "data/roles.dat";
    private static final RoleDAO INSTANCE = new RoleDAO();

    private Map<Integer, Role> roles = new HashMap<>();
    private int nextId = 1;

    private RoleDAO() {
        load();
        if (roles.isEmpty()) {
            seedDefaults();
        }
    }

    public static RoleDAO getInstance() {
        return INSTANCE;
    }

    private void seedDefaults() {
        saveInternal(new Role(0, "ADMIN"));
        saveInternal(new Role(0, "MANAGER"));
        saveInternal(new Role(0, "DEVELOPER"));
        saveInternal(new Role(0, "TESTER"));
    }

    public Role save(Role role) {
        Role created = new Role(nextId++, role.name());
        roles.put(created.id(), created);
        persist();
        return created;
    }

    private void saveInternal(Role role) {
        Role created = new Role(nextId++, role.name());
        roles.put(created.id(), created);
        persist();
    }

    public Optional<Role> findById(int id) {
        return Optional.ofNullable(roles.get(id));
    }

    public List<Role> findAll() {
        return new ArrayList<>(roles.values());
    }

    public void delete(int id) {
        roles.remove(id);
        persist();
    }

    public boolean existsById(int id) {
        return roles.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            roles = (Map<Integer, Role>) ois.readObject();
            nextId = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки ролей: " + e.getMessage());
        }
    }

    private void persist() {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(roles);
            oos.writeObject(nextId);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения ролей: " + e.getMessage());
        }
    }
}