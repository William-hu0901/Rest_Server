package org.daodao.restserver.controller;

import org.daodao.restserver.dto.QueryRequest;
import org.daodao.restserver.security.JwtTokenProvider;
import org.daodao.restserver.service.MySqlService;
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
    "spring.datasource-mysql.password=${MYSQL_PASS}",
})
class MySqlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MySqlService mySqlService;

    private String getJwtToken() {
        return jwtTokenProvider.generateToken("admin");
    }

    @Test
    void testQuery_Success() throws Exception {
        when(mySqlService.executeQuery(anyString())).thenReturn("{\"rows\":[{\"id\":1,\"name\":\"test\"}]}");
        QueryRequest request = new QueryRequest();
        request.setUsername("user_admin");
        request.setPassword("user_admin");
        request.setSql("SELECT * FROM users");
        String token = getJwtToken();

        mockMvc.perform(post("/api/mysql/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void testQuery_EmptyResult() throws Exception {
        when(mySqlService.executeQuery(anyString())).thenReturn("{\"rows\":[]}");
        QueryRequest request = new QueryRequest();
        String token = getJwtToken();

        mockMvc.perform(post("/api/mysql/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testQuery_InvalidJson() throws Exception {
        String invalidJson = "{invalid json}";
        String token = getJwtToken();

        mockMvc.perform(post("/api/mysql/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testQuery_WithSpecialCharacters() throws Exception {
        when(mySqlService.executeQuery(anyString())).thenReturn("{\"rows\":[{\"name\":\"test\"}]}");
        QueryRequest request = new QueryRequest();
        request.setUsername("user_admin");
        request.setPassword("user_admin");
        request.setSql("SELECT * FROM table WHERE name = 'test'");
        String token = getJwtToken();

        mockMvc.perform(post("/api/mysql/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testQuery_Error() throws Exception {
        when(mySqlService.executeQuery(anyString())).thenThrow(new Exception("Table not found"));
        QueryRequest request = new QueryRequest();
        request.setUsername("user_admin");
        request.setPassword("user_admin");
        request.setSql("SELECT * FROM nonexistent");
        String token = getJwtToken();

        mockMvc.perform(post("/api/mysql/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testQuery_Unauthorized() throws Exception {
        QueryRequest request = new QueryRequest();
        request.setUsername("user_admin");
        request.setPassword("user_admin");
        request.setSql("SELECT * FROM users");

        mockMvc.perform(post("/api/mysql/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTestConnection_Success() throws Exception {
        when(mySqlService.executeQuery(anyString())).thenReturn("{\"rows\":[{\"test\":1}]}");
        String token = getJwtToken();

        mockMvc.perform(get("/api/mysql/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testTestConnection_Error() throws Exception {
        when(mySqlService.executeQuery(anyString())).thenThrow(new Exception("Connection failed"));
        String token = getJwtToken();

        mockMvc.perform(get("/api/mysql/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.error").exists());
    }
}
