package com.server.repositories;

import java.io.*;
import java.util.*;

public class AssignmentDAO {
    private static final String FILE_PATH = "data/assignments.dat";
    private static final AssignmentDAO INSTANCE = new AssignmentDAO();

    private Map<Integer, Set<Integer>> assignments = new HashMap<>();

    private AssignmentDAO() {
        load();
    }

    public static AssignmentDAO getInstance() {
        return INSTANCE;
    }

    public void assign(int projectId, int employeeId) {
        assignments.computeIfAbsent(projectId, k -> new HashSet<>()).add(employeeId);
        persist();
    }

    public void remove(int projectId, int employeeId) {
        Set<Integer> empIds = assignments.get(projectId);
        if (empIds != null) {
            empIds.remove(employeeId);
            persist();
        }
    }

    public Set<Integer> findEmployeeIdsByProject(int projectId) {
        return assignments.getOrDefault(projectId, Collections.emptySet());
    }

    public boolean isAssigned(int projectId, int employeeId) {
        Set<Integer> empIds = assignments.get(projectId);
        return empIds != null && empIds.contains(employeeId);
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            assignments = (Map<Integer, Set<Integer>>) ois.readObject();
            System.out.println("Загружено назначений: " + assignments.size());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки назначений: " + e.getMessage());
        }
    }

    private void persist() {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(assignments);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения назначений: " + e.getMessage());
        }
    }
}