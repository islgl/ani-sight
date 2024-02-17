package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.History;
import cc.lglgl.anisight.domain.data.HistoryRepository;
import cc.lglgl.anisight.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {
    @Autowired
    private final HistoryRepository historyRepository;

    @Autowired
    private final OssUtil ossUtil;
    private final String masksDir = "masks/";
    private final String labelsDir = "labels/";

    private final String masksUrl;
    private final String labelsUrl;

    public HistoryService(HistoryRepository historyRepository, OssUtil ossUtil) {
        this.historyRepository = historyRepository;
        this.ossUtil = ossUtil;
        this.masksUrl = ossUtil.getUrl() + masksDir;
        this.labelsUrl = ossUtil.getUrl() + labelsDir;
    }

    public History addHistory(History history) {
        return historyRepository.save(history);
    }

    public History addHistory(Map<String, String> info) {
        History history = new History(Integer.parseInt(info.get("userId")),
                Integer.parseInt(info.get("imageId")),
                new Timestamp(System.currentTimeMillis()),
                info.get("mask"),
                info.get("label"),
                info.get("bboxes"),
                info.get("caption"));
        return historyRepository.save(history);
    }

    public List<History> getHistories() {
        List<History> histories = historyRepository.findAll();
        for (History history : histories) {
            history.setMask(masksUrl + history.getMask());
            history.setLabel(labelsUrl + history.getLabel());
        }
        return histories;
    }

    public History getHistoryById(int id) {
        History history = historyRepository.findById(id).orElse(null);
        if (history != null) {
            history.setMask(masksUrl + history.getMask());
            history.setLabel(labelsUrl + history.getLabel());
        }
        return history;
    }

    public List<History> getHistoriesByUserId(int userId) {
        List<History> histories = historyRepository.findByUserId(userId);
        for (History history : histories) {
            history.setMask(masksUrl + history.getMask());
            history.setLabel(labelsUrl + history.getLabel());
        }
        return histories;
    }

    public History getHistoryByImageId(int imageId) {
        History history = historyRepository.findByImageId(imageId);
        if (history != null) {
            history.setMask(masksUrl + history.getMask());
            history.setLabel(labelsUrl + history.getLabel());
        }
        return history;
    }

    public boolean deleteHistory(int id) {
        try {
            History history = historyRepository.findById(id).orElse(null);
            if (history != null) {
                ossUtil.deleteImage(history.getMask(), masksDir);
                ossUtil.deleteImage(history.getLabel(), labelsDir);
                historyRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public boolean deleteAllHistories() {
        try {
            ossUtil.deleteAllImages(masksDir);
            ossUtil.deleteAllImages(labelsDir);
            historyRepository.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public boolean deleteHistoriesByUserId(int userId) {
        try {
            List<History> histories = historyRepository.findByUserId(userId);
            List<Integer> ids = new ArrayList<>();
            List<String> masks = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (History history : histories) {
                ids.add(history.getId());
                masks.add(history.getMask());
                labels.add(history.getLabel());
            }

            ossUtil.deleteImages(masks, masksDir);
            ossUtil.deleteImages(labels, labelsDir);
            historyRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public boolean deleteHistoriesByImageId(int imageId) {
        try {
            History history = historyRepository.findByImageId(imageId);
            if (history != null) {
                ossUtil.deleteImage(history.getMask(), masksDir);
                ossUtil.deleteImage(history.getLabel(), labelsDir);
                historyRepository.deleteById(history.getId());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            ossUtil.shutdown();
        }
    }

    public Map<String, Object> history2Map(History history) {
        return Map.of(
                "User ID", history.getUserId(),
                "Image ID", history.getImageId(),
                "Time", history.getTimestamp(),
                "Mask", history.getMask(),
                "Label", history.getLabel(),
                "BBoxes", history.getBboxes(),
                "Caption", history.getCaption()
        );
    }
}
