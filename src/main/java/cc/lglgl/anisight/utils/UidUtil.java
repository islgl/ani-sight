package cc.lglgl.anisight.utils;

import org.springframework.stereotype.Component;


/**
 * @author lgl
 */
@Component
public class UidUtil {
    public static int generateUid(long count) {
        return 10000 + (int) count;
    }
}

