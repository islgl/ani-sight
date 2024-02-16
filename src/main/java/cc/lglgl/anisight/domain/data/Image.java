package cc.lglgl.anisight.domain.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Time;

/**
 * @author lgl
 * 用户上传图像
 */
@Data
@Entity
@Table(name = "t_image")
public class Image {
    @Id
    private int id;
    private int userId;
    // 图像OSS路径
    private String image;
    private Time timestamp;
}
