package com.atdxt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;



@Configuration
public class AmazonS3Config {

    @Bean
    public S3Client s3Client() {
        Region region = Region.EU_NORTH_1;
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .build();
    }
}