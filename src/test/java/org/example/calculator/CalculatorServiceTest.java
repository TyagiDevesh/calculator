package org.example.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CalculatorServiceTest {

    private CalculatorService service;

    @BeforeEach
    void setUp() {
        service = new CalculatorService();
    }

    @Test
    void testAdd() {
        assertEquals(7, service.add(3, 4));
    }

    @Test
    void testSubtract() {
        assertEquals(3, service.subtract(8, 5));
    }

    @Test
    void testMultiply() {
        assertEquals(20, service.multiply(4, 5));
    }

    @Test
    void testDivide() {
        assertEquals(2.5, service.divide(5, 2));
    }

    @Test
    void testDivideByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> service.divide(10, 0));
    }

    @Test
    void testUnsafeReflect() throws Exception {
        Object result = service.unsafeReflect("java.util.ArrayList");
        assertNotNull(result);
        assertInstanceOf(java.util.ArrayList.class, result);
    }

    @Test
    void testHashWithMd5() throws Exception {
        String hash = service.hashWithMd5("hello");
        assertNotNull(hash);
        assertEquals(32, hash.length());
        assertTrue(hash.matches("[0-9a-f]{32}"));
    }

    @Test
    void testDeprecatedBase64Encode() {
        String encoded = service.deprecatedBase64Encode("hello");
        assertEquals("aGVsbG8=", encoded);
    }

    @Test
    void testGetDbPassword() {
        assertEquals("SuperSecretPassword123!", service.getDbPassword());
    }

    @Test
    void testReadFileNotFound() {
        assertThrows(Exception.class, () -> service.readFile("nonexistent_xyz_99999_file.txt"));
    }

    @Test
    void testUnsafeInvoke() throws Exception {
        Object result = service.unsafeInvoke("java.util.ArrayList", "size");
        assertEquals(0, result);
    }

    @Test
    void testSensitiveException() {
        String result = service.sensitiveException("secretValue");
        assertTrue(result.contains("secretValue"));
    }

    @Test
    void testEncryptWithDES() throws Exception {
        byte[] result = service.encryptWithDES("plaintext");
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testBuildRedirectUrl() {
        assertEquals("https://redirect.com/?next=example.com", service.buildRedirectUrl("example.com"));
    }

    @Test
    void testInsecureRandomPin() {
        int pin = service.insecureRandomPin();
        assertTrue(pin >= 0 && pin < 10000);
    }

    @Test
    void testSwallowException() {
        assertDoesNotThrow(() -> service.swallowException());
    }

    @Test
    void testDeprecatedThreadStop() {
        // Thread.stop() is deprecated; on Java < 20 it completes without throwing on a new thread
        Thread t = new Thread(() -> {});
        assertDoesNotThrow(() -> service.deprecatedThreadStop(t));
    }

    @Test
    void testUnsafeXmlParse() throws Exception {
        String xml = "<?xml version=\"1.0\"?><root><child>value</child></root>";
        String result = service.unsafeXmlParse(xml);
        assertEquals("root", result);
    }

    @Test
    void testUnsafeSqlThrowsException() {
        // No DB available in test environment; expects connection failure
        assertThrows(Exception.class, () -> service.unsafeSql("test_input"));
    }

    @Test
    void testInsecureHttpGetThrowsException() {
        // Port 1 is unused; connection refused triggers exception
        assertThrows(Exception.class, () -> service.insecureHttpGet("http://localhost:1/no-endpoint"));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testInsecureHttpGetWithMockedClient() throws Exception {
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        HttpEntity mockEntity = mock(HttpEntity.class);
        InputStream mockStream = new ByteArrayInputStream("response body".getBytes());

        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(mockStream);

        try (MockedConstruction<DefaultHttpClient> ignored =
                Mockito.mockConstruction(DefaultHttpClient.class, (mock, context) ->
                        doReturn(mockResponse).when(mock).execute(any()))) {
            String result = service.insecureHttpGet("http://example.com/test");
            assertEquals("response body", result);
        }
    }

    @Test
    void testUnsafeSqlWithMockedDriver() throws Exception {
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getString("name")).thenReturn("Alice");

        try (MockedStatic<DriverManager> mockDM = Mockito.mockStatic(DriverManager.class)) {
            mockDM.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                  .thenReturn(mockConn);
            String result = service.unsafeSql("test_user");
            assertEquals("Alice,", result);
        }
    }

    @Test
    void testRunSystemCommand() throws Exception {
        String result = service.runSystemCommand("cmd /c echo hello");
        assertNotNull(result);
    }

    @Test
    void testGetAllEmployeesThrowsException() {
        // No DB available in test environment
        assertThrows(Exception.class, () -> service.getAllEmployees());
    }

    @Test
    void testGetAllEmployeesWithMockedDb() throws Exception {
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true).thenReturn(false);
        when(mockRs.getInt("employeeid")).thenReturn(1);
        when(mockRs.getString("name")).thenReturn("Alice");
        when(mockRs.getString("address")).thenReturn("123 Main St");
        when(mockRs.getInt("age")).thenReturn(30);
        when(mockRs.getDouble("salary")).thenReturn(50000.0);

        try (MockedStatic<DbUtil> mockedDbUtil = Mockito.mockStatic(DbUtil.class)) {
            mockedDbUtil.when(DbUtil::getConnection).thenReturn(mockConn);
            List<Employee> employees = service.getAllEmployees();
            assertEquals(1, employees.size());
            assertEquals("Alice", employees.get(0).getName());
            assertEquals(1, employees.get(0).getEmployeeId());
        }
    }
}
