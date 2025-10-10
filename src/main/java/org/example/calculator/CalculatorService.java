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
}
