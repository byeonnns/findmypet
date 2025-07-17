package com.findmypet.config.storage;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@ConfigurationProperties(prefix = "file.storage")
@Getter @Setter
public class StorageProperties {
    private DataSize maxFileSize;
    private DataSize userQuota;

    public static long MAX_FILE_SIZE;
    public static long USER_QUOTA;

    @PostConstruct
    public void init() {
        MAX_FILE_SIZE = maxFileSize.toBytes();
        USER_QUOTA = userQuota.toBytes();
    }
}