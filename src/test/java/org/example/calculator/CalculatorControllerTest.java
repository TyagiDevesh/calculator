package org.example.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}

