package org.example.calculator;

public class Employee {
    private int employeeId;
    private String name;
    private String address;
    private int age;
    private double salary;

    public Employee(int employeeId, String name, String address, int age, double salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.address = address;
        this.age = age;
        this.salary = salary;
    }

    public int getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getAge() { return age; }
    public double getSalary() { return salary; }

    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setAge(int age) { this.age = age; }
    public void setSalary(double salary) { this.salary = salary; }
}

