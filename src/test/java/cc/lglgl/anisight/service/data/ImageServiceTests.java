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

    @Test
    void testDeleteImage() {
        boolean flag = imageService.deleteImage(2);
        System.out.println(flag);
    }

    @Test
    void testDeleteImagesByUserId() {
        boolean flag = imageService.deleteImagesByUserId(2);
        System.out.println(flag);
    }

    @Test
    void testDeleteAllImages() {
        boolean flag = imageService.deleteAllImages();
        System.out.println(flag);
    }


}
