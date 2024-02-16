package cc.lglgl.anisight.domain.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Time;

/**
 * @author lgl
 * AI 模型推理历史记录
 */
@Data
@Entity
@Table(name = "t_history")
public class History {
    @Id
    private int id;
    private int userId;
    // 是否收藏
    private int star;
    private int imageId;
    private Time timestamp;
    // 掩码图片的OSS路径
    private String mask;
    // 带标注图片的OSS路径
    private String labeled;
    // 边界框CSV文件的OSS路径
    private String bboxes;
    // 图像文本描述
    private String caption;


}
