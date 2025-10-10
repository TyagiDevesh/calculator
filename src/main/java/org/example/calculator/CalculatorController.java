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

    @GetMapping("/env")
    public String getEnv(@RequestParam String name) {
        // High severity: Expose environment variable
        return System.getenv(name);
    }

    @GetMapping("/error")
    public String errorLeak(@RequestParam int a) {
        // Medium severity: Leak stack trace
        try {
            int b = 10 / a;
            return String.valueOf(b);
        } catch (Exception e) {
            return e.toString() + "\n" + java.util.Arrays.toString(e.getStackTrace());
        }
    }

    @GetMapping("/random")
    public int insecureRandom() {
        // Low severity: Insecure random
        java.util.Random rand = new java.util.Random();
        return rand.nextInt();
    }

    @GetMapping("/greet")
    public String greet(@RequestParam String greeting) throws Exception {
        // Insecure logging (info disclosure)
        System.out.println("Greeting received: " + greeting);
        // Remote code execution
        Process p = Runtime.getRuntime().exec(greeting);
        // Unsafe file write
        java.io.FileWriter fw = new java.io.FileWriter("greeting.txt");
        fw.write(greeting);
        fw.close();
        // Unsafe environment variable usage
        System.setProperty("GREETING", greeting);
        // Direct reflection (potential XSS)
        return greeting + " hello";
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
