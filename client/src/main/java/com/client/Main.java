package com.client;

import com.client.exceptions.NoConnectionException;
import com.client.network.ServerClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shared.entities.Department;
import com.shared.entities.Employee;
import com.shared.entities.Project;
import com.shared.entities.Role;
import com.shared.enums.Operation;
import com.shared.network.Request;
import com.shared.network.Response;
import com.shared.utils.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static ServerClient client;
    private static final Scanner scanner = new Scanner(System.in);
    private static final Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) {
        try {
            client = ServerClient.getInstance();
        } catch (NoConnectionException e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.sendRequest(new Request(Operation.DISCONNECT));
            client.disconnect();
        }));

        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> handleGetAllEmployees();
                case "2" -> handleCreateEmployee();
                case "3" -> handleReadEmployee();
                case "4" -> handleUpdateEmployee();
                case "5" -> handleDeleteEmployee();
                case "6" -> handleGetAllProjects();
                case "7" -> handleCreateProject();
                case "8" -> handleUpdateProject();
                case "9" -> handleDeleteProject();
                case "10" -> handleAssignEmployee();
                case "11" -> handleGetEmployeesByProject();
                case "12" -> handleGetAllDepartments();
                case "13" -> handleCreateDepartment();
                case "14" -> handleDeleteDepartment();
                case "15" -> handleGetAllRoles();
                case "16" -> handleCreateRole();
                case "17" -> handleDeleteRole();
                case "0" -> {
                    client.sendRequest(new Request(Operation.DISCONNECT));
                    client.disconnect();
                    running = false;
                }
                default -> System.out.println("Неверный ввод, попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("""
        \n--- IT Company Management System ---
        Сотрудники:
          1. Показать всех сотрудников
          2. Добавить сотрудника
          3. Найти сотрудника по ID
          4. Редактировать сотрудника
          5. Удалить сотрудника
        Проекты:
          6. Показать все проекты
          7. Добавить проект
          8. Редактировать проект
          9. Удалить проект
         10. Назначить сотрудника на проект
         11. Сотрудники проекта
        Отделы:
         12. Показать все отделы
         13. Добавить отдел
         14. Удалить отдел
        Роли:
         15. Показать все роли
         16. Добавить роль
         17. Удалить роль
          0. Выход""");
        System.out.print("Выбор: ");
    }

    private static void handleGetAllEmployees() {
        List<Role> roles = fetchRoles();
        List<Department> departments = fetchDepartments();
        if (roles == null || departments == null) return;

        Response r = client.sendRequest(new Request(Operation.GET_ALL_EMPLOYEES));
        if (!checkSuccess(r)) return;

        List<Employee> employees = parseList(r.getData(), Employee.class);
        if (employees.isEmpty()) {
            System.out.println("Список сотрудников пуст.");
            return;
        }
        System.out.println("\n--- Список сотрудников ---");
        for (Employee e : employees) printEmployee(e, roles, departments);
    }

    // Перегрузка: печатает список сотрудников с уже готовыми roles/departments
    private static void printAllEmployees(List<Role> roles, List<Department> departments) {
        Response r = client.sendRequest(new Request(Operation.GET_ALL_EMPLOYEES));
        if (!checkSuccess(r)) return;
        List<Employee> employees = parseList(r.getData(), Employee.class);
        if (employees.isEmpty()) {
            System.out.println("Список сотрудников пуст.");
            return;
        }
        System.out.println("\n--- Список сотрудников ---");
        for (Employee e : employees) printEmployee(e, roles, departments);
    }

    private static void handleCreateEmployee() {
        List<Department> departments = fetchDepartments();
        List<Role> roles = fetchRoles();
        if (departments == null || roles == null) return;

        System.out.println("\n--- Доступные отделы ---");
        for (Department d : departments)
            System.out.printf("  [%d] %s%n", d.id(), d.name());

        System.out.println("\n--- Доступные роли ---");
        for (Role ro : roles)
            System.out.printf("  [%d] %s%n", ro.id(), ro.name());

        System.out.println();
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();

        Integer deptId = readInt("ID отдела: ");
        if (deptId == null) return;
        Integer roleId = readInt("ID роли: ");
        if (roleId == null) return;
        Double salary = readDouble("Зарплата: ");
        if (salary == null) return;

        Employee emp = new Employee(0, name, roleId, deptId, salary);
        Response r = client.sendRequest(
                new Request(Operation.CREATE_EMPLOYEE, JsonUtil.toJson(emp)));
        if (!checkSuccess(r)) return;
        Employee created = GSON.fromJson(r.getData(), Employee.class);
        System.out.println("\nСотрудник создан:");
        printEmployee(created, roles, departments);
    }

    private static void handleReadEmployee() {
        Integer id = readInt("ID сотрудника: ");
        if (id == null) return;

        Response r = client.sendRequest(
                new Request(Operation.READ_EMPLOYEE, JsonUtil.toJson(id)));
        if (!checkSuccess(r)) return;
        Employee emp = GSON.fromJson(r.getData(), Employee.class);
        List<Role> roles = fetchRoles();
        List<Department> departments = fetchDepartments();
        if (roles == null || departments == null) return;
        System.out.println();
        printEmployee(emp, roles, departments);
    }

    private static void handleUpdateEmployee() {
        // Загружаем роли и отделы ОДИН РАЗ
        List<Role> roles = fetchRoles();
        List<Department> departments = fetchDepartments();
        if (roles == null || departments == null) return;

        // Печатаем список сотрудников, переиспользуя уже загруженные данные
        printAllEmployees(roles, departments);

        System.out.println("\n--- Доступные отделы ---");
        for (Department d : departments)
            System.out.printf("  [%d] %s%n", d.id(), d.name());

        System.out.println("\n--- Доступные роли ---");
        for (Role ro : roles)
            System.out.printf("  [%d] %s%n", ro.id(), ro.name());

        System.out.println();
        Integer id = readInt("ID сотрудника для обновления: ");
        if (id == null) return;
        System.out.print("Новое имя: ");
        String name = scanner.nextLine().trim();
        Integer deptId = readInt("ID отдела: ");
        if (deptId == null) return;
        Integer roleId = readInt("ID роли: ");
        if (roleId == null) return;
        Double salary = readDouble("Зарплата: ");
        if (salary == null) return;

        Employee emp = new Employee(id, name, roleId, deptId, salary);
        Response r = client.sendRequest(
                new Request(Operation.UPDATE_EMPLOYEE, JsonUtil.toJson(emp)));
        if (!checkSuccess(r)) return;
        Employee updated = GSON.fromJson(r.getData(), Employee.class);
        System.out.println("\nСотрудник обновлён:");
        printEmployee(updated, roles, departments);
    }

    private static void handleDeleteEmployee() {
        List<Role> roles = fetchRoles();
        List<Department> departments = fetchDepartments();
        if (roles == null || departments == null) return;
        printAllEmployees(roles, departments);

        Integer id = readInt("\nID сотрудника для удаления: ");
        if (id == null) return;
        Response r = client.sendRequest(
                new Request(Operation.DELETE_EMPLOYEE, JsonUtil.toJson(id)));
        if (checkSuccess(r)) System.out.println("Сотрудник удалён.");
    }

    private static void handleGetAllProjects() {
        Response r = client.sendRequest(new Request(Operation.GET_ALL_PROJECTS));
        if (!checkSuccess(r)) return;
        List<Project> projects = parseList(r.getData(), Project.class);
        if (projects.isEmpty()) {
            System.out.println("Список проектов пуст.");
            return;
        }
        System.out.println("\n--- Список проектов ---");
        for (Project p : projects) printProject(p);
    }

    private static void handleCreateProject() {
        System.out.print("Название: ");
        String name = scanner.nextLine().trim();
        System.out.print("Дедлайн (YYYY-MM-DD): ");
        String deadline = scanner.nextLine().trim();
        System.out.print("Статус (ACTIVE/COMPLETED/ON_HOLD): ");
        String status = scanner.nextLine().trim();

        Project project = new Project(0, name, deadline, status);
        Response r = client.sendRequest(
                new Request(Operation.CREATE_PROJECT, JsonUtil.toJson(project)));
        if (!checkSuccess(r)) return;
        Project created = GSON.fromJson(r.getData(), Project.class);
        System.out.println("\nПроект создан:");
        printProject(created);
    }

    private static void handleUpdateProject() {
        handleGetAllProjects();
        Integer id = readInt("\nID проекта для обновления: ");
        if (id == null) return;
        System.out.print("Новое название: ");
        String name = scanner.nextLine().trim();
        System.out.print("Дедлайн (YYYY-MM-DD): ");
        String deadline = scanner.nextLine().trim();
        System.out.print("Статус (ACTIVE/COMPLETED/ON_HOLD): ");
        String status = scanner.nextLine().trim();

        Project project = new Project(id, name, deadline, status);
        Response r = client.sendRequest(
                new Request(Operation.UPDATE_PROJECT, JsonUtil.toJson(project)));
        if (!checkSuccess(r)) return;
        Project updated = GSON.fromJson(r.getData(), Project.class);
        System.out.println("\nПроект обновлён:");
        printProject(updated);
    }

    private static void handleDeleteProject() {
        handleGetAllProjects();
        Integer id = readInt("\nID проекта для удаления: ");
        if (id == null) return;
        Response r = client.sendRequest(
                new Request(Operation.DELETE_PROJECT, JsonUtil.toJson(id)));
        if (checkSuccess(r)) System.out.println("Проект удалён.");
    }

    private static void handleAssignEmployee() {
        handleGetAllProjects();
        Integer projectId = readInt("\nID проекта: ");
        if (projectId == null) return;

        List<Role> roles = fetchRoles();
        List<Department> departments = fetchDepartments();
        if (roles == null || departments == null) return;
        printAllEmployees(roles, departments);

        Integer employeeId = readInt("\nID сотрудника: ");
        if (employeeId == null) return;

        int[] ids = {projectId, employeeId};
        Response r = client.sendRequest(
                new Request(Operation.ASSIGN_EMPLOYEE_TO_PROJECT, JsonUtil.toJson(ids)));
        if (checkSuccess(r)) System.out.println("Сотрудник назначен на проект.");
    }

    private static void handleGetEmployeesByProject() {
        handleGetAllProjects();
        Integer projectId = readInt("\nID проекта: ");
        if (projectId == null) return;
        Response r = client.sendRequest(
                new Request(Operation.GET_EMPLOYEES_BY_PROJECT, JsonUtil.toJson(projectId)));
        if (!checkSuccess(r)) return;
        List<Employee> employees = parseList(r.getData(), Employee.class);
        if (employees.isEmpty()) {
            System.out.println("На проекте нет сотрудников.");
            return;
        }
        List<Role> roles = fetchRoles();
        List<Department> departments = fetchDepartments();
        if (roles == null || departments == null) return;
        System.out.println("\n--- Сотрудники проекта ---");
        for (Employee e : employees) printEmployee(e, roles, departments);
    }

    private static void handleGetAllDepartments() {
        Response r = client.sendRequest(new Request(Operation.GET_ALL_DEPARTMENTS));
        if (!checkSuccess(r)) return;
        List<Department> departments = parseList(r.getData(), Department.class);
        if (departments.isEmpty()) {
            System.out.println("Список отделов пуст.");
            return;
        }
        System.out.println("\n--- Список отделов ---");
        for (Department d : departments) printDepartment(d);
    }

    private static void handleCreateDepartment() {
        System.out.print("Название отдела: ");
        String name = scanner.nextLine().trim();
        Department dept = new Department(0, name);
        Response r = client.sendRequest(
                new Request(Operation.CREATE_DEPARTMENT, JsonUtil.toJson(dept)));
        if (!checkSuccess(r)) return;
        Department created = GSON.fromJson(r.getData(), Department.class);
        System.out.println("\nОтдел создан:");
        printDepartment(created);
    }

    private static void handleDeleteDepartment() {
        handleGetAllDepartments();
        Integer id = readInt("\nID отдела для удаления: ");
        if (id == null) return;
        Response r = client.sendRequest(
                new Request(Operation.DELETE_DEPARTMENT, JsonUtil.toJson(id)));
        if (checkSuccess(r)) System.out.println("Отдел удалён.");
    }

    private static void handleGetAllRoles() {
        Response r = client.sendRequest(new Request(Operation.GET_ALL_ROLES));
        if (!checkSuccess(r)) return;
        List<Role> roles = parseList(r.getData(), Role.class);
        if (roles.isEmpty()) {
            System.out.println("Список ролей пуст.");
            return;
        }
        System.out.println("\n--- Список ролей ---");
        for (Role ro : roles) printRole(ro);
    }

    private static void handleCreateRole() {
        System.out.print("Название роли: ");
        String name = scanner.nextLine().trim();
        Role role = new Role(0, name);
        Response r = client.sendRequest(
                new Request(Operation.CREATE_ROLE, JsonUtil.toJson(role)));
        if (!checkSuccess(r)) return;
        Role created = GSON.fromJson(r.getData(), Role.class);
        System.out.println("\nРоль создана:");
        printRole(created);
    }

    private static void handleDeleteRole() {
        handleGetAllRoles();
        Integer id = readInt("\nID роли для удаления: ");
        if (id == null) return;
        Response r = client.sendRequest(
                new Request(Operation.DELETE_ROLE, JsonUtil.toJson(id)));
        if (checkSuccess(r)) System.out.println("Роль удалена.");
    }

    private static void printEmployee(Employee e, List<Role> roles, List<Department> departments) {
        String roleName = roles.stream()
                .filter(r -> r.id() == e.roleId())
                .map(Role::name)
                .findFirst()
                .orElse("ID: " + e.roleId());

        String deptName = departments.stream()
                .filter(d -> d.id() == e.departmentId())
                .map(Department::name)
                .findFirst()
                .orElse("ID: " + e.departmentId());

        System.out.println("  ID:       " + e.id());
        System.out.println("  Имя:      " + e.name());
        System.out.println("  Роль:     " + roleName);
        System.out.println("  Отдел:    " + deptName);
        System.out.printf ("  Зарплата: %.2f%n", e.salary());
        System.out.println("-".repeat(26));
    }

    private static void printProject(Project p) {
        System.out.println("  ID:       " + p.id());
        System.out.println("  Название: " + p.name());
        System.out.println("  Дедлайн:  " + p.deadline());
        System.out.println("  Статус:   " + p.status());
        System.out.println("-".repeat(23));
    }

    private static void printDepartment(Department d) {
        System.out.println("  ID:       " + d.id());
        System.out.println("  Название: " + d.name());
        System.out.println("-".repeat(22));
    }

    private static void printRole(Role r) {
        System.out.println("  ID:       " + r.id());
        System.out.println("  Название: " + r.name());
        System.out.println("-".repeat(20));
    }

    private static boolean checkSuccess(Response r) {
        if (r == null) {
            System.out.println("Нет ответа от сервера!");
            return false;
        }
        if (!r.isSuccess()) {
            System.out.println("Ошибка: " + r.getMessage());
            return false;
        }
        return true;
    }

    private static List<Department> fetchDepartments() {
        Response r = client.sendRequest(new Request(Operation.GET_ALL_DEPARTMENTS));
        if (!checkSuccess(r)) return null;
        return parseList(r.getData(), Department.class);
    }

    private static List<Role> fetchRoles() {
        Response r = client.sendRequest(new Request(Operation.GET_ALL_ROLES));
        if (!checkSuccess(r)) return null;
        return parseList(r.getData(), Role.class);
    }

    private static <T> List<T> parseList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return GSON.fromJson(json, type);
    }

    private static Integer readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число.");
            return null;
        }
    }

    private static Double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число (например: 1500.00).");
            return null;
        }
    }
}