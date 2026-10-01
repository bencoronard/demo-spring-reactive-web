package dev.hireben.spring.reactive.web.demo.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.hireben.spring.reactive.web.demo.storage.property.StorageProperties;
import dev.hireben.spring.reactive.web.demo.storage.repository.api.FileRepository;

@ExtendWith(MockitoExtension.class)
final class FileServiceImplTests {

  @Mock
  private FileRepository repository;

  private FileServiceImpl service;

  @BeforeEach
  void setup() {
    StorageProperties properties = new StorageProperties(null, null);
    service = new FileServiceImpl(properties, repository);
  }

}
