package org.healeasy;

import org.healeasy.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class HealEasyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealEasyApplication.class, args);
    }

}
