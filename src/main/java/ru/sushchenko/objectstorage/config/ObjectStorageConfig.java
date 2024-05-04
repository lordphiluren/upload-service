package ru.sushchenko.objectstorage.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ObjectStorageConfig {
    @Value("${yandex-cloud.bucket-name}")
    private String bucketName;
    @Value("${yandex-cloud.access-key}")
    private String accessKeyId;
    @Value("${yandex-cloud.secret-key}")
    private String secretAccessKey;
}
