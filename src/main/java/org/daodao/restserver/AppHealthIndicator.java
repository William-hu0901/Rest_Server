package org.daodao.restserver;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AppHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        int errorCode = check(); // check health of app。
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        // can implement check logic here.
        return 0; // return 0 if health, non-zero otherwise.
    }
}

