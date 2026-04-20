package com.server.repositories;

import com.shared.entities.Employee;

import java.io.*;
import java.util.*;

public class EmployeeDAO {
    private static final String FILE_PATH = "data/employees.dat";
    private static final EmployeeDAO INSTANCE = new EmployeeDAO();

    private Map<Integer, Employee> employees = new HashMap<>();
    private int nextId = 1;

    private EmployeeDAO() {
        load();
    }

    public static EmployeeDAO getInstance() {
        return INSTANCE;
    }

    public Employee save(Employee emp) {
        Employee created = new Employee(nextId++, emp.name(), emp.roleId(), emp.departmentId(), emp.salary());
        employees.put(created.id(), created);
        persist();
        return created;
    }

    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employees.get(id));
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    public Employee update(Employee emp) {
        employees.put(emp.id(), emp);
        persist();
        return emp;
    }

    public void delete(int id) {
        employees.remove(id);
        persist();
    }

    public boolean existsById(int id) {
        return employees.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            employees = (Map<Integer, Employee>) ois.readObject();
            nextId = (int) ois.readObject();
            System.out.println("Загружено сотрудников: " + employees.size());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки сотрудников: " + e.getMessage());
        }
    }

    private void persist() {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(employees);
            oos.writeObject(nextId);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения сотрудников: " + e.getMessage());
        }
    }
}