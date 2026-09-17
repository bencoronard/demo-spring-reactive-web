package dev.hireben.spring.reactive.web.demo.storage.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.hireben.spring.reactive.web.demo.storage.property.S3Properties;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
class AwsSdkConfiguration {

  @Bean
  SdkAsyncHttpClient sdkAsyncHttpClient() {
    return AwsCrtAsyncHttpClient.create();
  }

  @Bean
  S3AsyncClient s3AsyncClient(SdkAsyncHttpClient client, S3Properties properties) {
    return S3AsyncClient.builder()
        .httpClient(client)
        .endpointOverride(properties.url())
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build())
        .build();
  }

}
