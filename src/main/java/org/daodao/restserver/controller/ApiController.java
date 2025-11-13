package org.daodao.restserver.controller;

import org.daodao.restserver.dto.QueryRequest;
import org.daodao.restserver.dto.QueryResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @PostMapping("/queryData")
    public QueryResponse queryData(@RequestBody QueryRequest request) {
        // 模拟数据库查询结果
        String result = "Query executed successfully for user: " + request.getUsername() + 
                       " with SQL: " + request.getSql();
        
        return new QueryResponse("success", result, null);
    }
}