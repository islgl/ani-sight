package cc.lglgl.anisight.controller.data;

import cc.lglgl.anisight.domain.data.Image;
import cc.lglgl.anisight.dto.CustomResponse;
import cc.lglgl.anisight.service.data.ImageService;
import cc.lglgl.anisight.utils.CustomResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author lgl
 */
@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public CustomResponse getImages(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "uid", required = false) Integer uid
    ) {
        List<Image> images = null;

        if (id != null) {
            images = List.of(imageService.getImageById(id));
        } else if (uid != null) {
            images = imageService.getImagesByUid(uid);
        } else {
            images = imageService.getImages();
        }

        if (images == null) {
            return CustomResponseFactory.error("No images found");
        } else {
            String imagesNum = String.valueOf(images.size());
            return CustomResponseFactory.success(imagesNum + " images found", images);
        }
    }


    @PostMapping
    public CustomResponse uploadImage(@RequestParam("uid") int uid, @RequestParam("name") String name) {
        Image image = new Image();
        image.setUid(uid);
        image.setName(name);
        // 获取当前时间
        Timestamp time = new Timestamp(System.currentTimeMillis());
        image.setTimestamp(time);
        imageService.addImage(image);
        return CustomResponseFactory.success("Image uploaded", imageService.image2Map(image));
    }
}
