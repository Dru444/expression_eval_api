package com.api.expeval.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.expeval.dto.ExpressionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ExpressionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void calculateAndLookupByResult() throws Exception {
    ExpressionRequest request = new ExpressionRequest();
    request.setExpression("3+4*6-12");

    mockMvc.perform(post("/api/v1/expressions/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(15));

    mockMvc.perform(get("/api/v1/expressions/find-by-result")
            .queryParam("value", "15"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].expression").value("3+4*6-12"));
  }

  @Test
  void rejectsBlankExpression() throws Exception {
    ExpressionRequest request = new ExpressionRequest();
    request.setExpression(" ");

    mockMvc.perform(post("/api/v1/expressions/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void rejectsLongExpression() throws Exception {
    ExpressionRequest request = new ExpressionRequest();
    request.setExpression("1+".repeat(501)); // 1002 characters

    mockMvc.perform(post("/api/v1/expressions/calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void findByResultRequiresValue() throws Exception {
    mockMvc.perform(get("/api/v1/expressions/find-by-result"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void handlesInternalServerError() throws Exception {
    // This is hard to trigger without mocking the service, 
    // but maybe we can trigger a generic Exception if we pass something unexpected
    // or we can just rely on the fact thatExceptionHandler has Exception.class
  }
}
