package cc.lglgl.anisight.service.data;

import cc.lglgl.anisight.domain.data.History;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

@SpringBootTest
public class HistoryServiceTests {
    @Autowired
    private HistoryService historyService;

    @Test
    public void testAddHistory() {
        History history = new History(1, 1, new Timestamp(System.currentTimeMillis()), "mask", "label", "bboxes", "caption");
        History newHistory = historyService.addHistory(history);
        System.out.println(newHistory);
    }

    @Test
    public void testGetHistories() {
        System.out.println(historyService.getHistories());
    }

    @Test
    public void testGetHistoryById() {
        System.out.println(historyService.getHistoryById(1));
    }

    @Test
    public void testGetHistoryByUserId() {
        System.out.println(historyService.getHistoriesByUserId(1));
    }

    @Test
    public void testGetHistoryByImageId() {
        System.out.println(historyService.getHistoryByImageId(3));
    }

    @Test
    public void testDeleteHistoryByUserId() {
        System.out.println(historyService.deleteHistoriesByUserId(1));
    }

    @Test
    public void testDeleteHistory(){
        System.out.println(historyService.deleteHistory(15));
    }

    @Test
    public void testDeleteHistoryByImageId() {
        System.out.println(historyService.deleteHistoriesByImageId(6));
    }

    @Test
    public void testDeleteAllHistories() {
        System.out.println(historyService.deleteAllHistories());
    }
}
