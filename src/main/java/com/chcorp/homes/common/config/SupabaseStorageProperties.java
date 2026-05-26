package com.chcorp.homes.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supabase")
public record SupabaseStorageProperties(
        String url,
        String secretKey,
        Storage storage
) {
    public record Storage(
            String privateFileBucket,
            long signedDownloadTtlSeconds
    ) {
    }
}
