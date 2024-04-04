package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.History;
import cc.lglgl.anisight.domain.data.HistoryRepository;
import cc.lglgl.anisight.manager.OssUtilManager;
import cc.lglgl.anisight.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {
    @Autowired
    private final HistoryRepository historyRepository;

    @Autowired
    private final ImageService imageService;

    @Autowired
    private final OssUtilManager ossUtilManager;
    private final String masksDir = "masks/";
    private final String labelsDir = "labels/";

    public HistoryService(HistoryRepository historyRepository, ImageService imageService,
                          OssUtilManager ossUtilManager) {
        this.historyRepository = historyRepository;
        this.imageService = imageService;
        this.ossUtilManager = ossUtilManager;
    }

    public History addHistory(History history) {
        return historyRepository.save(history);
    }


    public List<History> getHistories() {
        return historyRepository.findAll();
    }

    public History getHistoryById(int id) {
        return historyRepository.findById(id).orElse(null);
    }

    public List<History> getHistoriesByUid(int uid) {
        return historyRepository.findAllByUid(uid);
    }

    public boolean deleteHistory(int id) {
        try {
            History history = historyRepository.findById(id).orElse(null);
            if (history != null) {
                OssUtil ossUtil = ossUtilManager.getOssUtil();
                String label = history.getImageName();
                String mask = label.replace(".jpg", ".png");
                ossUtil.deleteImage(mask, masksDir);
                ossUtil.deleteImage(label, labelsDir);
                historyRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteHistories(List<Integer> ids) {
        try {
            for (int id : ids) {
                deleteHistory(id);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAllHistories() {
        try {
            OssUtil ossUtil = ossUtilManager.getOssUtil();
            ossUtil.deleteAllImages(masksDir);
            ossUtil.deleteAllImages(labelsDir);
            historyRepository.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteHistoriesByUid(int uid) {
        try {
            List<History> histories = historyRepository.findAllByUid(uid);
            List<Integer> ids = new ArrayList<>();
            List<String> masks = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (History history : histories) {
                ids.add(history.getId());
                String label = history.getImageName();
                String mask = label.replace(".jpg", ".png");
                masks.add(mask);
                labels.add(label);
            }
            OssUtil ossUtil = ossUtilManager.getOssUtil();
            ossUtil.deleteImages(masks, masksDir);
            ossUtil.deleteImages(labels, labelsDir);
            historyRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean starHistories(List<Integer> ids, int star) {
        try {
            for (int id : ids) {
                History history = historyRepository.findById(id).orElse(null);
                if (history != null) {
                    history.setStar(star);
                    historyRepository.save(history);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 归档与取消归档历史记录
     *
     * @param id   历史记录ID
     * @param star 归档状态 0-未归档 1-已归档
     */
    public boolean starHistory(int id, int star) {
        try {
            History history = historyRepository.findById(id).orElse(null);
            if (history != null) {
                history.setStar(star);
                historyRepository.save(history);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> history2Map(History history) {
        return Map.of(
                "UID", history.getUid(),
                "Image ID", history.getImageName(),
                "Time", history.getTimestamp(),
                "Caption", history.getCaption(),
                "Species", history.getSpecies());
    }
}
