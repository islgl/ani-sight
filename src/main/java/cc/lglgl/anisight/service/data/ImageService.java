package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.Image;
import cc.lglgl.anisight.domain.data.ImageRepository;
import cc.lglgl.anisight.utils.OssUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

    public ImageService(ImageRepository imageRepository, OssUtil ossUtil) {
        this.imageRepository = imageRepository;
        this.ossUtil = ossUtil;
    }

    public List<Image> getImages() {
        List<Image> images = imageRepository.findAll();
        for (Image image : images) {
            image.setName(image.getName());
        }
        return images;
    }

    public Image getImageById(int id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            image.setName(image.getName());
        }
        return image;
    }

    public Image getImageByName(String name) {
        Image image = imageRepository.findByName(name);
        if (image != null) {
            image.setName(image.getName());
        }
        return image;
    }

    public List<Image> getImagesByUid(int uid) {
        List<Image> images = imageRepository.findAllByUid(uid);
        for (Image image : images) {
            image.setName(image.getName());
        }
        return images;
    }

    public Image addImage(Image image) {
        return imageRepository.save(image);
    }

    public boolean deleteImage(int id) {
        try {
            Image image = imageRepository.findById(id).orElse(null);
            if (image != null) {
                imageRepository.deleteById(id);
                return ossUtil.deleteImage(image.getName(), ossDir);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public boolean deleteAllImages() {
        try {
            imageRepository.deleteAll();
            return ossUtil.deleteAllImages(ossDir);
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public boolean deleteImagesByUid(int uid) {
        try {
            List<Image> images = imageRepository.findAllByUid(uid);
            List<Integer> ids = new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (Image image : images) {
                ids.add(image.getId());
                names.add(image.getName());
            }

            imageRepository.deleteAllById(ids);
            return ossUtil.deleteImages(names, ossDir);
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public Map<String, Object> image2Map(Image image) {
        return Map.of(
                "id", image.getId(),
                "uid", image.getUid(),
                "filename", image.getName(),
                "timestamp", image.getTimestamp());
    }
}
