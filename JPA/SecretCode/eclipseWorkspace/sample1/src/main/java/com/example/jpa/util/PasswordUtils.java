package com.example.jpa.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCrypt;

@UtilityClass
public class PasswordUtils {

    public static String encryptedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // 패스워드를 암호화해서 리턴하는 함수
    // 입력한 패스워드를 해시된 패스워드랑 비교하는 함수
    public static boolean equalPassword(String password, String encryptedPassword) {
        try {
            return BCrypt.checkpw(password, encryptedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
