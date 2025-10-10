package org.example.calculator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;

    static {
        try (InputStream input = DbUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                dbUrl = prop.getProperty("db.url");
                dbUsername = prop.getProperty("db.username");
                dbPassword = prop.getProperty("db.password");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    // High severity: Hardcoded credentials
    public static Connection getHardcodedConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hardcoded_db";
        String user = "hardcoded_user";
        String pass = "hardcoded_pass";
        return DriverManager.getConnection(url, user, pass);
    }

    // High severity: SQL injection
    public static boolean isEmployeeExists(String employeeName) throws SQLException {
        String query = "SELECT * FROM employee WHERE name = '" + employeeName + "'"; // SQL injection
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        }
    }

    // High severity: Unsafe deserialization
    public static Object unsafeDeserialize(String filePath) throws Exception {
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(filePath))) {
            return ois.readObject(); // Unsafe deserialization
        }
    }

    // Medium severity: Sensitive info in exception
    public static void logSensitiveException() {
        try {
            throw new Exception("Database password: " + dbPassword);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Leaks sensitive info
        }
    }

    // Medium severity: Insecure cipher
    public static byte[] encryptPasswordWithDES() throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES");
        javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("DES");
        java.security.Key key = keyGen.generateKey();
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(dbPassword.getBytes());
    }

    // Medium severity: Poor error handling
    public static void swallowDbException() {
        try {
            DriverManager.getConnection("jdbc:mysql://invalid", "user", "pass");
        } catch (Exception e) {
            // Exception swallowed
        }
    }
}
