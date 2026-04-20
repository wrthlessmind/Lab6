package com.server.repositories;

import com.shared.entities.Department;

import java.io.*;
import java.util.*;

public class DepartmentDAO {
    private static final String FILE_PATH = "data/departments.dat";
    private static final DepartmentDAO INSTANCE = new DepartmentDAO();

    private Map<Integer, Department> departments = new HashMap<>();
    private int nextId = 1;

    private DepartmentDAO() {
        load();
        if (departments.isEmpty()) {
            seedDefaults();
        }
    }

    public static DepartmentDAO getInstance() {
        return INSTANCE;
    }

    private void seedDefaults() {
        saveInternal(new Department(0, "Backend"));
        saveInternal(new Department(0, "Frontend"));
        saveInternal(new Department(0, "DevOps"));
        saveInternal(new Department(0, "QA"));
    }

    public Department save(Department dept) {
        Department created = new Department(nextId++, dept.name());
        departments.put(created.id(), created);
        persist();
        return created;
    }

    private void saveInternal(Department dept) {
        Department created = new Department(nextId++, dept.name());
        departments.put(created.id(), created);
        persist();
    }

    public Optional<Department> findById(int id) {
        return Optional.ofNullable(departments.get(id));
    }

    public List<Department> findAll() {
        return new ArrayList<>(departments.values());
    }

    public void delete(int id) {
        departments.remove(id);
        persist();
    }

    public boolean existsById(int id) {
        return departments.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            departments = (Map<Integer, Department>) ois.readObject();
            nextId = (int) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки отделов: " + e.getMessage());
        }
    }

    private void persist() {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(departments);
            oos.writeObject(nextId);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения отделов: " + e.getMessage());
        }
    }
}