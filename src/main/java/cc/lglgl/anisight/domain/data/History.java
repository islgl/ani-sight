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
    private int star=0;
    private int imageId;
    private Timestamp timestamp;
    private String caption;


    public History(int uid, int imageId, Timestamp timestamp, String caption) {
        this.uid = uid;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.caption = caption;
    }

    public History(int uid, int imageId, String caption) {
        this.uid = uid;
        this.imageId = imageId;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.caption = caption;
    }
}
