package com.example.jpa.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserLoginToken;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Date;

@UtilityClass
public class JWTUtils {

    private static final String KEY = "jikim";
    private static final String CLAIM_USER_ID = "user_id";

    public static UserLoginToken createToken(User user) {
        if (user == null) {
            return null;
        }
        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String token = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim(CLAIM_USER_ID, user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512(KEY.getBytes()));

        return UserLoginToken.builder()
                .token(token)
                .build();
    }

    public static String getIssuer(String token) {
        String issuer = JWT.require(Algorithm.HMAC512(KEY.getBytes()))
                .build()
                .verify(token)
                .getIssuer();
        return issuer;
    }
}
