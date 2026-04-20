package com.server.repositories;

import com.shared.entities.Project;

import java.io.*;
import java.util.*;

public class ProjectDAO {
    private static final String FILE_PATH = "data/projects.dat";
    private static final ProjectDAO INSTANCE = new ProjectDAO();

    private Map<Integer, Project> projects = new HashMap<>();
    private int nextId = 1;

    private ProjectDAO() {
        load();
    }

    public static ProjectDAO getInstance() {
        return INSTANCE;
    }

    public Project save(Project project) {
        Project created = new Project(nextId++, project.name(), project.deadline(), project.status());
        projects.put(created.id(), created);
        persist();
        return created;
    }

    public Optional<Project> findById(int id) {
        return Optional.ofNullable(projects.get(id));
    }

    public List<Project> findAll() {
        return new ArrayList<>(projects.values());
    }

    public Project update(Project project) {
        projects.put(project.id(), project);
        persist();
        return project;
    }

    public void delete(int id) {
        projects.remove(id);
        persist();
    }

    public boolean existsById(int id) {
        return projects.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            projects = (Map<Integer, Project>) ois.readObject();
            nextId = (int) ois.readObject();
            System.out.println("Загружено проектов: " + projects.size());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки проектов: " + e.getMessage());
        }
    }

    private void persist() {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(projects);
            oos.writeObject(nextId);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения проектов: " + e.getMessage());
        }
    }
}