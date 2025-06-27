package me.shinsunyoung.backend.Security.JWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;


@Configuration
public class JwtKey {

    // JWT 서명에 사용할 비밀 키
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Bean
    public SecretKey secretKey(){

        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, "HmacSHA512");

    }





}
