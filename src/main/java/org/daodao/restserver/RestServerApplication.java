package org.daodao.restserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Rest Server API", version = "1.0", description = "Rest Server API"))
public class RestServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServerApplication.class, args);
    }
}