package cc.lglgl.anisight.dto;

import lombok.Data;


/**
 * @author lgl
 */
@Data
public class CustomResponse {
    private String status;
    private String message;
    private Object data;

    public CustomResponse(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}


