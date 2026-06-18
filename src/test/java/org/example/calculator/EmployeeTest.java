package org.example.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testConstructorAndGetters() {
        Employee emp = new Employee(1, "Alice", "100 Main St", 28, 60000.0);
        assertEquals(1, emp.getEmployeeId());
        assertEquals("Alice", emp.getName());
        assertEquals("100 Main St", emp.getAddress());
        assertEquals(28, emp.getAge());
        assertEquals(60000.0, emp.getSalary());
    }

    @Test
    void testSetters() {
        Employee emp = new Employee(0, "", "", 0, 0.0);
        emp.setEmployeeId(42);
        emp.setName("Bob");
        emp.setAddress("999 Oak Ave");
        emp.setAge(35);
        emp.setSalary(90000.0);
        assertEquals(42, emp.getEmployeeId());
        assertEquals("Bob", emp.getName());
        assertEquals("999 Oak Ave", emp.getAddress());
        assertEquals(35, emp.getAge());
        assertEquals(90000.0, emp.getSalary());
    }
}
