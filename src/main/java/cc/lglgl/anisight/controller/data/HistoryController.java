package cc.lglgl.anisight.controller.data;

import cc.lglgl.anisight.domain.data.History;
import cc.lglgl.anisight.dto.CustomResponse;
import cc.lglgl.anisight.service.data.HistoryService;
import cc.lglgl.anisight.service.data.ImageService;
import cc.lglgl.anisight.utils.CustomResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/histories")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ImageService imageService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public CustomResponse getHistories(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "uid", required = false) Integer uid) {
        List<History> histories = null;

        if (id != null) {
            History history = historyService.getHistoryById(id);
            if (history == null) {
                return CustomResponseFactory.error("No history found");
            }
            histories = List.of(history);
        } else if (uid != null) {
            histories = historyService.getHistoriesByUid(uid);
        } else {
            histories = historyService.getHistories();
        }

        if (histories == null || histories.isEmpty()) {
            return CustomResponseFactory.error("No histories found");
        } else {
            String historiesNum = String.valueOf(histories.size());
            return CustomResponseFactory.success(historiesNum + " histories found", histories);
        }
    }

    @PostMapping
    public CustomResponse addHistory(
            @RequestParam("uid") int uid,
            @RequestParam("image") String imageName,
            @RequestParam("caption") String caption) {
        try {
            int imageId = imageService.getImageByName(imageName).getId();
            History history = new History(uid, imageId, caption);
            historyService.addHistory(history);
            return CustomResponseFactory.success("History added", historyService.history2Map(history));
        } catch (Exception e) {
            return CustomResponseFactory.error("Failed to add history");
        }
    }

    @DeleteMapping
    public CustomResponse deleteHistories(@RequestParam(value="ids",required = false) List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                historyService.deleteAllHistories();
            } else {
                historyService.deleteHistories(ids);
            }
            return CustomResponseFactory.success("Histories deleted");
        } catch (Exception e) {
            return CustomResponseFactory.error("Failed to delete histories");
        }
    }

    @DeleteMapping("/{id}")
    public CustomResponse deleteHistory(@PathVariable("id") int id) {
        try {
            historyService.deleteHistory(id);
            return CustomResponseFactory.success("History deleted");
        } catch (Exception e) {
            return CustomResponseFactory.error("Failed to delete history");
        }
    }

    @PutMapping
    public CustomResponse updateHistories(@RequestParam(value = "ids") List<Integer> ids,
                                          @RequestParam("star") int star) {
        try {
            if (ids == null || ids.isEmpty()) {
                return CustomResponseFactory.error("Please provide ids");
            }
            historyService.starHistories(ids, star);
            return CustomResponseFactory.success("Histories updated");
        } catch (Exception e) {
            return CustomResponseFactory.error("Failed to update histories");
        }
    }

    @PutMapping("/{id}")
    public CustomResponse updateHistory(@PathVariable("id") int id,
                                        @RequestParam("star") int star) {
        try {
            historyService.starHistory(id, star);
            return CustomResponseFactory.success("History updated");
        } catch (Exception e) {
            return CustomResponseFactory.error("Failed to update history");
        }
    }

}
