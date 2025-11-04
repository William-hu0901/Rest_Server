package org.daodao.restserver;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AppHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // 你可以在这里添加逻辑来检查你的应用是否健康，例如检查数据库连接等。
        int errorCode = check(); // 示例方法，你需要实现它来检查健康状态。
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        // 实现你的检查逻辑，例如数据库连接测试等。
        return 0; // 返回0表示无错误。
    }
}

