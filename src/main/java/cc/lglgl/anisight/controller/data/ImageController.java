package cc.lglgl.anisight.controller.data;

import cc.lglgl.anisight.domain.data.Image;
import cc.lglgl.anisight.dto.CustomResponse;
import cc.lglgl.anisight.service.data.ImageService;
import cc.lglgl.anisight.utils.CustomResponseFactory;
import cc.lglgl.anisight.utils.JwtUtil;
import cc.lglgl.anisight.utils.StsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author lgl
 */
@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private final ImageService imageService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StsUtil stsUtil;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public CustomResponse getImages(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "uid", required = false) Integer uid) {
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
        try {
            Image image = new Image();
            image.setUid(uid);
            image.setName(name);
            Timestamp time = new Timestamp(System.currentTimeMillis());
            image.setTimestamp(time);
            imageService.addImage(image);
            return CustomResponseFactory.success("Image uploaded", imageService.image2Map(image));
        } catch (Exception e) {
            e.printStackTrace();
            return CustomResponseFactory.error("Failed to write image record");
        }
    }

    /**
     * 获取阿里云OSS的临时凭证
     *
     * @return
     */
    @GetMapping("/sts-credentials")
    public CustomResponse getCredentials(@RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam("uid") int uid) {
        if (token == null || token.isEmpty()) {
            return CustomResponseFactory.error("Please provide a token");
        }
        if (!jwtUtil.validateToken(token, uid)) {
            return CustomResponseFactory.error("Invalid token");
        }
        try {
            Map<String, String> credentials = stsUtil.getStsCredential(String.valueOf(uid));
            return CustomResponseFactory.success("STS credentials obtained", credentials);
        } catch (Exception e) {
            e.printStackTrace();
            return CustomResponseFactory.error("Failed to get STS credentials");
        }

    }
}
