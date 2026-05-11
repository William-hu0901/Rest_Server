package org.daodao.restserver.controller;

import org.daodao.restserver.dto.QueryRequest;
import org.daodao.restserver.dto.QueryResponse;
import org.daodao.restserver.security.JwtTokenProvider;
import org.daodao.restserver.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.user.password=${SECURE_PASS}",
    "spring.dataource.password=${POSTGRES_PASS}"
})
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private QueryService queryService;

    private String getJwtToken() {
        return jwtTokenProvider.generateToken("admin");
    }

    @Test
    void testHelloEndpoint() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));
    }

    @Test
    void testQueryData_Success() throws Exception {
        when(queryService.executeQuery(anyString())).thenReturn("{\"rows\":[{\"id\":1,\"name\":\"test\"}]}");
        QueryRequest request = new QueryRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setSql("SELECT * FROM users");
        String token = getJwtToken();

        mockMvc.perform(post("/api/queryData")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void testQueryData_EmptyRequest() throws Exception {
        when(queryService.executeQuery(anyString())).thenReturn("{\"rows\":[]}");
        QueryRequest request = new QueryRequest();
        String token = getJwtToken();

        mockMvc.perform(post("/api/queryData")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testQueryData_InvalidJson() throws Exception {
        String invalidJson = "{invalid json}";
        String token = getJwtToken();

        mockMvc.perform(post("/api/queryData")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testQueryData_WithSpecialCharacters() throws Exception {
        when(queryService.executeQuery(anyString())).thenReturn("{\"rows\":[{\"name\":\"test\"}]}");
        QueryRequest request = new QueryRequest();
        request.setUsername("user@domain.com");
        request.setPassword("p@ssw0rd!");
        request.setSql("SELECT * FROM table WHERE name = 'test'");
        String token = getJwtToken();

        mockMvc.perform(post("/api/queryData")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}