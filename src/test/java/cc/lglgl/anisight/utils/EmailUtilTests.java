package cc.lglgl.anisight.utils;

import com.aliyun.dm20151123.models.SingleSendMailResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailUtilTests {
    @Test
    void testSendEmail() {
        String receiver = "liuguoli2001@126.com";
        String subject = "AniSight 测试邮件";
        String content = "欢迎使用AniSight！这是一封测试邮件，如果您收到了，说明邮件发送功能正常。";
        SingleSendMailResponse response = EmailUtil.sendEmail(1, receiver, subject, content);
        if (response != null) {
            System.out.println("Request ID: " + response.getBody().getRequestId());
        }
    }
}
