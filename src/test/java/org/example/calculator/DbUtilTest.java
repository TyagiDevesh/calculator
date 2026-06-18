package org.example.calculator;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DbUtilTest {

    @Test
    void testConstructor() {
        DbUtil dbUtil = new DbUtil();
        assertNotNull(dbUtil);
    }

    @Test
    void testGetConnectionThrowsSqlException() {
        // No MySQL server running; expects connection failure
        assertThrows(SQLException.class, DbUtil::getConnection);
    }

    @Test
    void testGetHardcodedConnectionThrowsSqlException() {
        assertThrows(SQLException.class, DbUtil::getHardcodedConnection);
    }

    @Test
    void testIsEmployeeExistsThrowsSqlException() {
        assertThrows(SQLException.class, () -> DbUtil.isEmployeeExists("testUser"));
    }

    @Test
    void testIsEmployeeExistsWithMockedConnection() throws Exception {
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);

        try (MockedStatic<DbUtil> mockedDbUtil = Mockito.mockStatic(DbUtil.class, Mockito.CALLS_REAL_METHODS)) {
            mockedDbUtil.when(DbUtil::getConnection).thenReturn(mockConn);
            boolean exists = DbUtil.isEmployeeExists("Alice");
            assertTrue(exists);
        }
    }

    @Test
    void testUnsafeDeserializeThrowsException() {
        assertThrows(Exception.class, () -> DbUtil.unsafeDeserialize("nonexistent_xyz_99999.ser"));
    }

    @Test
    void testUnsafeDeserializeSuccess() throws Exception {
        File tempFile = File.createTempFile("test_deser_", ".ser");
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
                oos.writeObject("serializedTestData");
            }
            Object result = DbUtil.unsafeDeserialize(tempFile.getAbsolutePath());
            assertEquals("serializedTestData", result);
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void testLogSensitiveException() {
        // Throws and catches internally; prints to stdout
        assertDoesNotThrow(DbUtil::logSensitiveException);
    }

    @Test
    void testEncryptPasswordWithDES() throws Exception {
        byte[] result = DbUtil.encryptPasswordWithDES();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testSwallowDbException() {
        // Tries bad connection; swallows the resulting exception
        assertDoesNotThrow(DbUtil::swallowDbException);
    }
}
