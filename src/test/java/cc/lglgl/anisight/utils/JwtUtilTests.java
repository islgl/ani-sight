package cc.lglgl.anisight.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtUtilTests {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGenerateToken() {
        String token = jwtUtil.generateToken(10001,0);
        System.out.println(token);
    }

    @Test
    void testExtractUid() {
        String token = jwtUtil.generateToken(10001,0);
        int uid = jwtUtil.extractUid(token);
        System.out.println(uid);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(10002,0);
        boolean result = jwtUtil.validateToken(token, 10002);
        System.out.println(token);
        System.out.println(result);
    }

    @Test
    void testExtractRole() {
        String token = jwtUtil.generateToken(10003,0);
        int role = jwtUtil.extractRole(token);
        System.out.println(role);
    }
}
