package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.Image;
import cc.lglgl.anisight.domain.data.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lgl
 */
@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public List<Image> getImages() {
        return imageRepository.findAll();
    }

    public Image getImageById(int id) {
        return imageRepository.findById(id).orElse(null);
    }

    public List<Image> getImagesByUserId(int userId) {
        return imageRepository.findByUserId(userId);
    }

    public Image addImage(Image image) {
        return imageRepository.save(image);
    }

    public Image updateImage(Image image) {
        return imageRepository.save(image);
    }

    public void deleteImage(int id) {
        imageRepository.deleteById(id);
    }

}
