package cc.lglgl.anisight.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lgl
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUid(int uid);
    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAllByRole(int role);
}
