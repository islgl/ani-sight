package cc.lglgl.anisight.manager;

import org.springframework.stereotype.Component;

import cc.lglgl.anisight.utils.OssUtil;
import jakarta.annotation.PreDestroy;

/**
 * @author lgl
 */
@Component
public class OssUtilManager {
    private OssUtil ossUtil;

    public OssUtilManager() {
        this.ossUtil = new OssUtil();
    }

    public OssUtil getOssUtil() {
        return ossUtil;
    }

    @PreDestroy
    public void close() {
        ossUtil.shutdown();
    }

}


