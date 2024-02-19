package cc.lglgl.anisight.service.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.dto.CustomResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Test
    void testGetUserByUsername() {
        String username = "admin";
        try {
            User user = userService.getUserByUsername(username);
            System.out.println("User: " + user);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGenerateCode() {
        String code = userService.generateCode(6);
        System.out.println("Code: " + code);
    }

    @Test
    void testSendVerifyCode(){
        String code=userService.sendVerifyCode("liuguoli19@163.com");
        System.out.println("Code: "+code);
    }
}
