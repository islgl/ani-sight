package cc.lglgl.anisight.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lgl
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_user")
public class User {
    @Id
    private int id;
    private String username;
    private String password;
    private int role;
    private String email;
    private String avatar;

    public User(int id, String username, String password, int role, String email, String avatar) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.avatar = avatar;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = 0;
        this.avatar = "default.svg";
    }
}
