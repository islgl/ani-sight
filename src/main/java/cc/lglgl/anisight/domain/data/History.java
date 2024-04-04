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
    private String imageName;
    private Timestamp timestamp;
    private String caption;
    private String species;


    public History(int uid, String imageName, Timestamp timestamp, String caption,String species) {
        this.uid = uid;
        this.imageName = imageName;
        this.timestamp = timestamp;
        this.caption = caption;
        this.species=species;

    }

    public History(int uid, String imageName, String caption,String species) {
        this.uid = uid;
        this.imageName = imageName;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.caption = caption;
        this.species=species;
    }
}
