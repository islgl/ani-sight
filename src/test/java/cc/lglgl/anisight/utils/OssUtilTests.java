package cc.lglgl.anisight.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@SpringBootTest
@RestController
public class OssUtilTests {
    @Autowired
    private OssUtil ossUtil;

    @Test
    void testUploadImage() throws FileNotFoundException {
        String filepath = "/Users/lgl/code/ani-sight/ani-sight/src/main/resources/sheep_base64.txt";
        try {
            // 创建文件读取器
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // 逐行读取文件内容
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            // 从文件中读取的Base64编码字符串
            String base64String = stringBuilder.toString();

            ossUtil.uploadImage("sheep.jpg", base64String, "images");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDeleteImage() {
        ossUtil.deleteImage("sheep.jpg", "images");
    }
}
