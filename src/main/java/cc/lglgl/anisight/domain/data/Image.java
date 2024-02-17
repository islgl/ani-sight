package cc.lglgl.anisight.domain.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

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
    private String name;
    private Timestamp timestamp;
}
