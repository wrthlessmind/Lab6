package com.server.network;

import com.server.controllers.*;
import com.server.exceptions.ResponseException;
import com.shared.enums.Operation;
import com.shared.network.Request;
import com.shared.network.Response;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
    private final Socket clientSocket;
    private final EmployeeController employeeController;
    private final ProjectController projectController;
    private final DepartmentController departmentController;
    private final RoleController roleController;
    private final AssignmentController assignmentController;

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        this.employeeController = new EmployeeController();
        this.projectController = new ProjectController();
        this.departmentController = new DepartmentController();
        this.roleController = new RoleController();
        this.assignmentController = new AssignmentController();
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            boolean keepRunning = true;
            while (keepRunning) {
                try {
                    Request request = (Request) input.readObject();
                    if (request != null) {
                        System.out.println("[" + clientSocket.getInetAddress().getHostAddress() + "] "
                                + "Запрос: " + request.getOperation());
                        Response response = processRequest(request);
                        System.out.println("[" + clientSocket.getInetAddress().getHostAddress() + "] "
                                + "Ответ: " + (response.isSuccess() ? "OK" : "ERROR")
                                + " - " + response.getMessage());
                        if (request.getOperation() == Operation.DISCONNECT)
                            keepRunning = false;
                        output.reset();
                        output.writeObject(response);
                        output.flush();
                    } else {
                        output.reset();
                        output.writeObject(new Response(false, "Received invalid object", null));
                        output.flush();
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка соединения: " + e.getMessage());
                    keepRunning = false;
                } catch (ClassNotFoundException e) {
                    System.err.println("Класс не найден: " + e.getMessage());
                    keepRunning = false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    private Response processRequest(Request request) {
        try {
            return switch (request.getOperation()) {
                case CREATE_EMPLOYEE -> employeeController.create(request);
                case READ_EMPLOYEE -> employeeController.read(request);
                case UPDATE_EMPLOYEE -> employeeController.update(request);
                case DELETE_EMPLOYEE -> employeeController.delete(request);
                case GET_ALL_EMPLOYEES -> employeeController.getAll();
                case CREATE_PROJECT -> projectController.create(request);
                case UPDATE_PROJECT -> projectController.update(request);
                case DELETE_PROJECT -> projectController.delete(request);
                case GET_ALL_PROJECTS -> projectController.getAll();
                case CREATE_DEPARTMENT -> departmentController.create(request);
                case DELETE_DEPARTMENT -> departmentController.delete(request);
                case GET_ALL_DEPARTMENTS -> departmentController.getAll();
                case CREATE_ROLE -> roleController.create(request);
                case DELETE_ROLE -> roleController.delete(request);
                case GET_ALL_ROLES -> roleController.getAll();
                case ASSIGN_EMPLOYEE_TO_PROJECT -> assignmentController.assign(request);
                case REMOVE_EMPLOYEE_FROM_PROJECT -> assignmentController.remove(request);
                case GET_EMPLOYEES_BY_PROJECT -> assignmentController.getEmployeesByProject(request);
                case DISCONNECT -> new Response(true, "Disconnected successfully", null);
            };
        } catch (ResponseException e) {
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Something went wrong on the server side!", null);
        }
    }

    private void closeConnection() {
        try {
            clientSocket.close();
            Server.decrementClientCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}