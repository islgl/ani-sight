package cc.lglgl.anisight.service.user;

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
