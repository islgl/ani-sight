package cc.lglgl.anisight.utils;

import cc.lglgl.anisight.dto.CustomResponse;

/**
 * @author lgl
 */
public class CustomResponseFactory {

    public static CustomResponse success(String msg) {
        return new CustomResponse("success", msg, null);
    }

    public static CustomResponse success(String msg, Object data) {
        return new CustomResponse("success", msg, data);
    }

    public static CustomResponse error(String msg) {
        return new CustomResponse("error", msg, null);
    }

    public static CustomResponse error(String msg, Object data) {
        return new CustomResponse("error", msg, data);
    }
}