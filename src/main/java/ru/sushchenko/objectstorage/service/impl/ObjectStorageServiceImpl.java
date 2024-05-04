package ru.sushchenko.objectstorage.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sushchenko.objectstorage.config.ObjectStorageConfig;
import ru.sushchenko.objectstorage.service.ObjectStorageService;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService {
    private final ObjectStorageConfig objectStorageConfig;
    @Override
    public Set<String> uploadAttachments(List<byte[]> attachments) {
        Set<String> urls = new HashSet<>();
        AmazonS3 s3Client = getS3Client();
        final String bucketName = objectStorageConfig.getBucketName();

        List<CompletableFuture<String>> futures = attachments.stream()
                .map(attachment -> CompletableFuture.supplyAsync(() -> saveAndGetUrl(attachment, s3Client, bucketName)))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<String>> allUrls = allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        urls.addAll(allUrls.join());
        return urls;
    }
    private String saveAndGetUrl(byte[] attachment, AmazonS3 s3Client, String bucketName) {
        String fileName = generateUniqueName();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(attachment.length);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment);
        s3Client.putObject(bucketName, fileName, inputStream, metadata);
        return s3Client.getUrl(bucketName, fileName).toExternalForm();
    }
    private AmazonS3 getS3Client() {
        final String accessKeyId = objectStorageConfig.getAccessKeyId();
        final String secretAccessKey = objectStorageConfig.getSecretAccessKey();

        final AmazonS3 s3Client;
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    "https://storage.yandexcloud.net",
                                    "ru-central1"
                            )
                    )
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                    .build();
        } catch (SdkClientException e) {
            throw new SdkClientException(e.getMessage());
        }
        return s3Client;
    }
    private String generateUniqueName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
