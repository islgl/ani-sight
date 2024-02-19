package cc.lglgl.anisight.domain.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lgl
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByUid(int uid);

    Image findByName(String name);

}
