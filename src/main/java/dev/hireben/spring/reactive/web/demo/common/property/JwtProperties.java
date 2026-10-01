package dev.hireben.spring.reactive.web.demo.common.property;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(
    Verify verify) {

  public record Verify(
      Key key) {

    public record Key(
        URI uri) {
    }

  }

}
