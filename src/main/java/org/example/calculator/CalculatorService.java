package org.example.calculator;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    public int add(int a, int b) {
        return a + b;
    }
    public int subtract(int a, int b) {
        return a - b;
    }
    public int multiply(int a, int b) {
        return a * b;
    }
    public double divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Division by zero");
        return (double) a / b;
    }

    // High severity: Unsafe reflection
    public Object unsafeReflect(String className) throws Exception {
        Class<?> clazz = Class.forName(className); // User-controlled class loading
        return clazz.getDeclaredConstructor().newInstance();
    }

    // Medium severity: Weak cryptography
    public String hashWithMd5(String input) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Low severity: Deprecated API usage
    public String deprecatedBase64Encode(String input) {
        return new sun.misc.BASE64Encoder().encode(input.getBytes());
    }

    // High severity: SQL injection
    public String unsafeSql(String userInput) throws Exception {
        java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
        java.sql.Statement stmt = conn.createStatement();
        String query = "SELECT * FROM users WHERE name = '" + userInput + "'"; // SQL injection
        java.sql.ResultSet rs = stmt.executeQuery(query);
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append(rs.getString("name")).append(",");
        }
        rs.close(); stmt.close(); conn.close();
        return sb.toString();
    }

    // Medium severity: XXE
    public String unsafeXmlParse(String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // XXE vulnerability: external entities enabled by default
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
        return doc.getDocumentElement().getNodeName();
    }

    // Low severity: Insecure HTTP client
    public String insecureHttpGet(String url) throws Exception {
        org.apache.http.client.HttpClient client = new org.apache.http.impl.client.DefaultHttpClient();
        org.apache.http.client.methods.HttpGet request = new org.apache.http.client.methods.HttpGet(url);
        org.apache.http.HttpResponse response = client.execute(request);
        java.io.InputStream is = response.getEntity().getContent();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        s.close();
        return result;
    }

    // High severity: Hardcoded credentials
    public String getDbPassword() {
        return "SuperSecretPassword123!"; // Hardcoded password
    }

    // High severity: Path traversal
    public String readFile(String filename) throws Exception {
        java.nio.file.Path path = java.nio.file.Paths.get(filename); // No validation
        return new String(java.nio.file.Files.readAllBytes(path));
    }

    // High severity: Unsafe reflection method invocation
    public Object unsafeInvoke(String className, String methodName) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        java.lang.reflect.Method method = clazz.getMethod(methodName);
        return method.invoke(instance); // User-controlled method invocation
    }

    // Medium severity: Sensitive data in exception
    public String sensitiveException(String secret) {
        try {
            throw new Exception("Sensitive: " + secret);
        } catch (Exception e) {
            return e.getMessage(); // Leaks sensitive data
        }
    }

    // Medium severity: Insecure cipher
    public byte[] encryptWithDES(String data) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES");
        javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("DES");
        java.security.Key key = keyGen.generateKey();
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    // Medium severity: Unvalidated redirect URL construction
    public String buildRedirectUrl(String url) {
        return "https://redirect.com/?next=" + url; // No validation
    }

    // Low severity: Insecure random for security-sensitive operation
    public int insecureRandomPin() {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(10000); // Used for PIN
    }

    // Low severity: Poor error handling
    public void swallowException() {
        try {
            int x = 1 / 0;
        } catch (Exception e) {
            // Exception swallowed
        }
    }

    // Low severity: Deprecated thread method
    public void deprecatedThreadStop(Thread t) {
        t.stop(); // Deprecated and unsafe
    }

    // High severity: Remote code execution
    public String runSystemCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        java.io.InputStream is = process.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String output = s.hasNext() ? s.next() : "";
        s.close();
        return output;
    }

    // Read employees from SQL database
    public java.util.List<Employee> getAllEmployees() throws Exception {
        java.util.List<Employee> employees = new java.util.ArrayList<>();
        try (java.sql.Connection conn = DbUtil.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT employeeid, name, address, age, salary FROM employee")) {
            while (rs.next()) {
                Employee emp = new Employee(
                    rs.getInt("employeeid"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getInt("age"),
                    rs.getDouble("salary")
                );
                employees.add(emp);
            }
        }
        return employees;
    }
}
