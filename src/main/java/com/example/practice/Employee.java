package com.example.practice;

import java.io.*;
import java.util.*;

public class Employee implements Serializable {
    private String firstName;
    private String lastName;
    private String socialSecurityNumber;
    private Date birthDate;
    private String userName;
    private String password;
    private int id;
    private int departmentId;
    private boolean isManager;
    private boolean isArchived;
    private Activity status;
    private ArrayList<Salary> salaries;
    private ArrayList<Integer> departmentHistory; // Attribute to store department history
    private double managerBaseSalary;
    private Activity inactiveReason;
    private ArrayList<ArchiveHistory> archiveHistory;




    private static final String FILENAME = "Employees.ser";
    public Employee() {
        this.departmentHistory = new ArrayList<>();
        this.salaries = new ArrayList<>();
        this.archiveHistory = new ArrayList<>();
    }

    public Employee(String firstName, String lastName, String socialSecurityNumber, Date birthDate, String userName,
                    String password, int id, int departmentId, boolean isManager, boolean isArchived, Activity status, double managerBaseSalary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.birthDate = birthDate;
        this.userName = userName;
        this.password = password;
        this.id = id;
        this.departmentId = departmentId;
        this.isManager = isManager;
        this.isArchived = isArchived;
        this.status = status;
        this.salaries = new ArrayList<>();
        this.departmentHistory = new ArrayList<>(); // Initialize the departmentHistory list
        if (isManager) {
            this.managerBaseSalary = managerBaseSalary;
        } else {
            this.managerBaseSalary = 0; // Set default or handle as needed
        }
        this.archiveHistory = new ArrayList<>();

    }

    // Additional constructor that uses java.util.Date for birthDate
    public Employee(String firstName, String lastName, String ssn, java.util.Date birthDate, String username, String password, int departmentId, boolean isManager, String salaryType, double salary1, double salary2, double salary3, int employeeId, java.util.Date salaryStartDate, java.util.Date salaryEndDate, boolean isActive,Activity status,Activity inactiveReason) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = ssn;
        this.birthDate = new Date(birthDate.getDate(), birthDate.getMonth() + 1, birthDate.getYear() + 1900); // Convert java.util.Date to custom Date
        this.userName = username;
        this.password = password;
        this.departmentId = departmentId;
        this.isManager = isManager;
        this.id=employeeId;
        this.salaries = new ArrayList<>();
        this.isArchived = isActive;
        this.status=status;
        this.inactiveReason = inactiveReason;
        this.departmentHistory = new ArrayList<>(); // Initialize departmentHistory
        this.archiveHistory = new ArrayList<>();


        // Initialize salaries based on salaryType
        Date startDate = new Date(salaryStartDate.getDate(), salaryStartDate.getMonth() + 1, salaryStartDate.getYear() + 1900);
        Date endDate = new Date(salaryEndDate.getDate(), salaryEndDate.getMonth() + 1, salaryEndDate.getYear() + 1900);

        switch (salaryType) {
            case "Fixed":
                this.salaries.add(new Fixed(startDate, endDate, isActive, this, salary1));
                break;
            case "Hourly":
                this.salaries.add(new HourlyWage(startDate, endDate, isActive, this, salary1, salary2));
                break;
            case "Commission":
                this.salaries.add(new Commission(startDate, endDate, isActive, this, salary2, salary1));
                break;
            case "Base Plus Commission":
                this.salaries.add(new BasePlusCommission(startDate, endDate, isActive, this, salary1, salary3, salary2));
                break;
        }

        if (isManager) {
            this.managerBaseSalary = salary1; // Assign manager base salary
        } else {
            this.managerBaseSalary = 0; // Set default value or handle as needed
        }
    }
    

    public void addSalary(Salary salary) {
        salaries.add(salary);
    }

    public ArrayList<Salary> getPaymentHistory() {
        return salaries;
    }


    public static double calculateEarnings(int id, String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                double totalEarnings = 0;
                for (Salary salary : employee.getPaymentHistory()) {
                    totalEarnings += salary.getAmount();
                }
                return totalEarnings;
            }
        }
        return 0;
    }
    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isManager() {
        return isManager;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public boolean isArchived(boolean b) {
        return isArchived;
    }
    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public Activity getStatus() {
        return status;
    }
    public void setStatus(Activity status) {
        this.status = status;
    }


    public double getManagerBaseSalary() {
        return managerBaseSalary;
    }
    public void setManagerBaseSalary(double managerBaseSalary) {
        this.managerBaseSalary = managerBaseSalary;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public Activity getInactiveReason() {
        return inactiveReason;
    }

    public void setInactiveReason(Activity inactiveReason) {
        this.inactiveReason = inactiveReason;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public ArrayList<Integer> getDepartmentHistory() {
        return departmentHistory;
    }

    public void addDepartmentHistory(int departmentId) {
        departmentHistory.add(departmentId);
    }
    public void addArchiveHistory(Date date, boolean isArchived) {
        archiveHistory.add(new ArchiveHistory(date, isArchived));
    }

    public ArrayList<ArchiveHistory> getArchiveHistory() {
        return archiveHistory;
    }


    public static Employee findById(int id, String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    public static ArrayList<Employee> searchBySalaryType(Class<? extends Salary> salaryType, String filename) {
        Set<Employee> employees = readEmployeesFromFile(FILENAME);
        ArrayList<Employee> result = new ArrayList<>();
        for (Employee employee : employees) {
            for (Salary salary : employee.getPaymentHistory()) {
                if (salaryType.isInstance(salary) && salary.activeSalary) {
                    result.add(employee);
                    break;
                }
            }
        }
        return result;
    }

    public static List<Employee> showAllArchivedEmployees(String filename) {
        Set<Employee> employees = readEmployeesFromFile(FILENAME);
        List<Employee> archivedEmployeeList = new ArrayList<>();
        int archivedEmployeeCount = 0;
        int archivedManagerCount = 0;

        for (Employee employee : employees) {
            if (employee.isArchived) {
                archivedEmployeeList.add(employee);
                if (employee.isManager) {
                    archivedManagerCount++;
                } else {
                    archivedEmployeeCount++;
                }
            }
        }

        System.out.println("Total archived employees: " + archivedEmployeeCount);
        System.out.println("Total archived managers: " + archivedManagerCount);

        return archivedEmployeeList;
    }

    public static void changeSalary(int id, Salary newSalary, String filename) {
        Set<Employee> employees = readEmployeesFromFile(FILENAME);
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                for (Salary salary : employee.getPaymentHistory()) {
                    salary.activeSalary = false;
                }
                newSalary.activeSalary = true;
                employee.addSalary(newSalary);
                writeEmployeesToFile(employees, filename);
                break;
            }
        }
    }

    // Method to calculate total earnings of a department
    public static double calculateDepartmentEarnings(int departmentId, String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);
        double totalEarnings = 0;
        for (Employee employee : employees) {
            if (employee.getDepartmentId() == departmentId) {
                for (Salary salary : employee.getPaymentHistory()) {
                    totalEarnings += salary.getAmount();
                }
            }
        }
        return totalEarnings;
    }


    // Method to calculate total earnings of all employees
    public static double calculateAllEmployeesEarnings(String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);
        double totalEarnings = 0;
        for (Employee employee : employees) {
            for (Salary salary : employee.getPaymentHistory()) {
                totalEarnings += salary.getAmount();
            }
        }
        return totalEarnings;
    }


    public static Set<Employee> readEmployeesFromFile(String filename) {
        Set<Employee> employees = new HashSet<>();
        File file = new File(filename);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                employees = (Set<Employee>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return employees;
    }

    static void writeEmployeesToFile(Set<Employee> employees, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {

        // Find the active salary for the employee
        Salary activeSalary = null;
        for (Salary salary : salaries) {
            if (salary.activeSalary) {
                activeSalary = salary;
                break;
            }
        }

        // Return the employee details as a string with the active salary
        return "Employee{" +
                "FirstName='" + firstName +
                "\t\tLastName='" + lastName +
                "\t\tUserName='" + userName +
                "\t\tId=" + id +
                "\t\tDepartmentId=" + departmentId +
                "\t\tisManager=" + isManager +
                "\t\tStatus=" + status +
                "\t\t Archived=" + isArchived +
                (isManager ? "\t\tManager Base Salary=" + managerBaseSalary : "") +
                "\nsalaries=" + salaries +
                "\t\tBirthday=" + birthDate +
                "\t\tSSN='" + socialSecurityNumber + '\'' +
                (activeSalary != null ? "\nActive Salary=" + activeSalary : "") +
                "\nDepartment History=" + departmentHistory +
                "\nArchive History=" + archiveHistory +
                "}\n";
    }
}
