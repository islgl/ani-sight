package cc.lglgl.anisight.domain.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author lgl
 * AI 模型推理历史记录
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "t_history")
public class History {
    @Id
    private int id;
    private int uid;
    // 是否收藏
    private int star;
    private int imageId;
    private Timestamp timestamp;
    // 掩码图片的OSS路径
    private String mask;
    // 带标注图片的OSS路径
    private String label;
    // 边界框CSV文件的OSS路径
    private String bboxes;
    // 图像文本描述
    private String caption;

    public History(int userId, int imageId, Timestamp timestamp, String mask, String label, String bboxes, String caption) {
        this.uid = userId;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.mask = mask;
        this.label = label;
        this.bboxes = bboxes;
        this.caption = caption;
    }
}
