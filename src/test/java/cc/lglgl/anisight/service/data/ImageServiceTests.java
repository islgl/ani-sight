package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootTest
@RestController
public class ImageServiceTests {
    @Autowired
    private ImageService imageService;

    @Test
    void testGetImages() {
        List<Image> images = imageService.getImages();
        for (Image image : images) {
            System.out.println(image);
        }
    }
}
