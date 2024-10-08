package com.example.practice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Organization implements OrganizationInterface{
    private static final String FILENAME = "DepartmentList.ser";
    private List<Department> departments;

    public Organization() {
        departments = new ArrayList<>();
        loadDepartments();
    }
    @Override
    public boolean addDepartment(int id, String name) {
        List<Department> departments = loadDepartmentsFromFile();
        // add condition to check if department already exists
        for (Department d : departments) {
            if (d.getId() == id) {
                System.out.println("Department already exists.");
                return false;
            }
        }
        Department department = new Department(id, name);
        departments.add(department);
        saveDepartmentsToFile(departments);
        return true;
    }
    @Override
    public List<String> showAllDepartments() {
        List<String> departmentList = new ArrayList<>();
        for (Department department : departments) {
            departmentList.add(department.toString());
        }
        return departmentList;
    }

    public List<Department> getDepartments() {
        return departments;
    }
@Override
    public void loadDepartments() {
        File file = new File(FILENAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
                departments = (List<Department>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // If file doesn't exist, create default departments
            for (int i = 1; i <= 30; i++) {
                departments.add(new Department(i, "Department " + i));
            }
            saveDepartments();
        }
    }

    private static List<Department> loadDepartmentsFromFile() {
        List<Department> departments = new ArrayList<>();
        File file = new File(FILENAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
                departments = (List<Department>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // If file doesn't exist, create default departments
            for (int i = 1; i <= 20; i++) {
                departments.add(new Department(i, "Department " + i));
            }
            saveDepartmentsToFile(departments);
        }
        return departments;
    }
@Override
    public void saveDepartments() {
        saveDepartmentsToFile(departments);
    }

    private static void saveDepartmentsToFile(List<Department> departments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(departments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
@Override
    public  boolean isValidDepartmentId(int id) {
        List<Department> departments = loadDepartmentsFromFile();
        for (Department department : departments) {
            if (department.getId() == id) {
                return true;
            }
        }
        return false;
    }
@Override
    public boolean changeEmployeeDepartment(int employeeId, int newDepartmentId, String filename) {

        Set<Employee> employees = Employee.readEmployeesFromFile(filename);
        for (Employee employee : employees) {
            if (employee.getId() == employeeId) {
                if (employee.isManager()) {
                    System.out.println("Managers cannot change their departments.");
                    return false;
                }

                employee.addDepartmentHistory(newDepartmentId);
                employee.setDepartmentId(newDepartmentId);
                Employee.writeEmployeesToFile(employees, filename);
                System.out.println("Employee department changed successfully.");
                return true;
            }
        }
        System.out.println("Employee not found.");
    return false;
}
}
