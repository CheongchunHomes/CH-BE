package com.chcorp.homes.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        String refreshCookieName,
        String refreshCookiePath,
        boolean refreshCookieSecure,
        String refreshCookieSameSite
) {
}
