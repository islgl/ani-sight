package cc.lglgl.anisight.domain.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lgl
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
    @SuppressWarnings("null")
    List<History> findAll();
    List<History> findAllByUid(int uid);
    History findByImageId(int imageId);
    
}
