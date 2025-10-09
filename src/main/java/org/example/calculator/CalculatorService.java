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
}
