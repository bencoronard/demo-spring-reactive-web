package dev.hireben.spring.reactive.web.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import software.amazon.awssdk.services.s3.S3AsyncClient;

@SpringBootTest
@ActiveProfiles("test")
final class DemoApplicationTests {

  @MockitoBean
  S3AsyncClient s3AsyncClient;

  @Test
  void contextLoads() {
  }

}
