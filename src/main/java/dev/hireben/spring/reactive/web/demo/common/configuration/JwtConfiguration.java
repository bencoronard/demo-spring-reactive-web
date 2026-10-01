package dev.hireben.spring.reactive.web.demo.common.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.hireben.spring.reactive.web.demo.common.property.JwtProperties;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@Configuration
class JwtConfiguration {

  @Bean
  JwtParser jwtParser(JwtProperties properties)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    byte[] keyFile = Files.readAllBytes(Path.of(properties.verify().key().uri()));

    String keyBase64 = new String(keyFile)
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "");

    byte[] key = Base64.getMimeDecoder().decode(keyBase64);
    KeyFactory factory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
    PublicKey publicKey = factory.generatePublic(spec);

    return Jwts.parser().verifyWith(publicKey).build();
  }

}
