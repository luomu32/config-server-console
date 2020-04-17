package xyz.luomu32.config.server.console.web.response;

import lombok.Data;

import java.util.Map;

@Data
public class BatchResponse {

    private Long total;

    private Long successCount;

    private Long failedCount;

    private Map<String/*id*/, String/*message*/> errorMessages;

    public BatchResponse() {
    }

    public BatchResponse(Long successCount, Long failedCount) {
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.total = this.successCount + this.failedCount;
    }

}
