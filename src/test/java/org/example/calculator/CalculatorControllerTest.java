package org.example.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
public class CalculatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculatorService calculatorService;

    @Test
    void testAdd() throws Exception {
        when(calculatorService.add(2, 3)).thenReturn(5);
        mockMvc.perform(get("/api/calculator/add?a=2&b=3"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testSubtract() throws Exception {
        when(calculatorService.subtract(5, 2)).thenReturn(3);
        mockMvc.perform(get("/api/calculator/subtract?a=5&b=2"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void testMultiply() throws Exception {
        when(calculatorService.multiply(4, 3)).thenReturn(12);
        mockMvc.perform(get("/api/calculator/multiply?a=4&b=3"))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));
    }

    @Test
    void testDivide() throws Exception {
        when(calculatorService.divide(10, 2)).thenReturn(5.0);
        mockMvc.perform(get("/api/calculator/divide?a=10&b=2"))
                .andExpect(status().isOk())
                .andExpect(content().string("5.0"));
    }

    @Test
    void testLogInput() throws Exception {
        mockMvc.perform(post("/api/calculator/log")
                .contentType(MediaType.TEXT_PLAIN)
                .content("test input"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEnv() throws Exception {
        mockMvc.perform(get("/api/calculator/env").param("name", "PATH"))
                .andExpect(status().isOk());
    }

    @Test
    void testErrorLeakNormal() throws Exception {
        mockMvc.perform(get("/api/calculator/error").param("a", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testErrorLeakDivByZero() throws Exception {
        mockMvc.perform(get("/api/calculator/error").param("a", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void testInsecureRandom() throws Exception {
        mockMvc.perform(get("/api/calculator/random"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeserialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject("testObject");
        oos.close();
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        mockMvc.perform(post("/api/calculator/deserialize")
                .contentType(MediaType.TEXT_PLAIN)
                .content(base64))
                .andExpect(status().isOk())
                .andExpect(content().string("testObject"));
    }

    @Test
    void testExec() throws Exception {
        mockMvc.perform(post("/api/calculator/exec")
                .contentType(MediaType.TEXT_PLAIN)
                .content("cmd /c echo hello"))
                .andExpect(status().isOk());
    }

    @Test
    void testGreet() throws Exception {
        mockMvc.perform(get("/api/calculator/greet").param("greeting", "cmd /c echo hello"))
                .andExpect(status().isOk());
    }
}
