package org.healeasy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Data
public class JwtConfig {
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration accessTokenExpiration;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration refreshTokenExpiration;
}