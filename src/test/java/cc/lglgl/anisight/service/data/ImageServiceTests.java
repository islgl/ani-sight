package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
@RestController
public class ImageServiceTests {
    @Autowired
    private ImageService imageService;

    @Test
    void testAddImage() {
        Image image = new Image();
        image.setUserId(1);
        image.setName("test.jpg");
        image.setTimestamp(new Timestamp(System.currentTimeMillis()));
        System.out.println(imageService.addImage(image));
    }

    @Test
    void testGetImageByName(){
        Image image = imageService.getImageByName("test.jpg");
        System.out.println(image);
    }

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
