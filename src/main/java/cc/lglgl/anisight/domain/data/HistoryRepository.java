package cc.lglgl.anisight.domain.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lgl
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
    public List<History> findByUserId(int userId);

    public History findByImageId(int imageId);

    public void deleteAllByUserId(int userId);

    public void deleteByImageId(int imageId);
}
