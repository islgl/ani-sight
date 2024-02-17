package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.Image;
import cc.lglgl.anisight.domain.data.ImageRepository;
import cc.lglgl.anisight.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author lgl
 */
@Service
public class ImageService {
    @Autowired
    private final ImageRepository imageRepository;

    @Autowired
    private final OssUtil ossUtil;
    private final String ossDir = "images/";
    private final String ossUrl;

    public ImageService(ImageRepository imageRepository, OssUtil ossUtil) {
        this.imageRepository = imageRepository;
        this.ossUtil = ossUtil;
        this.ossUrl = ossUtil.getUrl() + ossDir;
    }

    public List<Image> getImages() {
        List<Image> images = imageRepository.findAll();
        for (Image image : images) {
            image.setName(ossUrl + image.getName());
        }
        return images;
    }

    public Image getImageById(int id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            image.setName(ossUrl + image.getName());
        }
        return image;
    }

    public List<Image> getImagesByUserId(int userId) {
        List<Image> images = imageRepository.findByUserId(userId);
        for (Image image : images) {
            image.setName(ossUrl + image.getName());
        }
        return images;
    }

    public Image addImage(Image image) {
        return imageRepository.save(image);
    }

    public boolean deleteImage(int id) {
        try {
            Image image = imageRepository.findById(id).orElse(null);
            String name = null;
            if (image != null) {
                name = image.getName();
                imageRepository.deleteById(id);
                return ossUtil.deleteImage(name, ossDir);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAllImages() {
        try {
            imageRepository.deleteAll();
            return ossUtil.deleteAllImages(ossDir);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteImagesByUserId(int userId) {
        try {
            List<Image> images = imageRepository.findByUserId(userId);
            List<Integer> ids = images.stream().map(Image::getId).toList();
            List<String> names = images.stream().map(Image::getName).toList();

            imageRepository.deleteAllById(ids);
            return ossUtil.deleteImages(names, ossDir);
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String,Object> image2Map(Image image) {
        return Map.of(
                "User ID", image.getUserId(),
                "Filename", ossUrl + image.getName(),
                "Timestamp", image.getTimestamp()
        );
    }

}
