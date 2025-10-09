package org.example.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {
    @Autowired
    private CalculatorService calculatorService;

    @GetMapping("/add")
    public int add(@RequestParam int a, @RequestParam int b) {
        return calculatorService.add(a, b);
    }

    @GetMapping("/subtract")
    public int subtract(@RequestParam int a, @RequestParam int b) {
        return calculatorService.subtract(a, b);
    }

    @GetMapping("/multiply")
    public int multiply(@RequestParam int a, @RequestParam int b) {
        return calculatorService.multiply(a, b);
    }

    @GetMapping("/divide")
    public double divide(@RequestParam int a, @RequestParam int b) {
        return calculatorService.divide(a, b);
    }

    @PostMapping("/deserialize")
    public String deserialize(@RequestBody String base64SerializedObject) throws Exception {
        byte[] data = java.util.Base64.getDecoder().decode(base64SerializedObject);
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data));
        Object o = ois.readObject(); // Unsafe deserialization
        ois.close();
        return o.toString();
    }

    @PostMapping("/exec")
    public String exec(@RequestBody String command) throws Exception {
        // High severity: Remote code execution
        Process process = Runtime.getRuntime().exec(command);
        java.io.InputStream is = process.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String output = s.hasNext() ? s.next() : "";
        s.close();
        return output;
    }

    @GetMapping("/redirect")
    public void redirect(@RequestParam String url, javax.servlet.http.HttpServletResponse response) throws java.io.IOException {
        // Medium severity: Open redirect
        response.sendRedirect(url);
    }

    @PostMapping("/log")
    public void logInput(@RequestBody String input) {
        // Low severity: Insecure logging
        System.out.println("User input: " + input);
    }
}
package org.example.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalculatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }
}
