package com.concurrency.org.concurrency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageData  implements Serializable {

    private String userId;

    private String requestId;

    private String message;

    private String threadName;

    public MessageData(String userId, String requestId, String message, String threadName) {
        this.userId = userId;
        this.requestId = requestId;
        this.message = message;
        this.threadName = threadName;
    }

    public MessageData() {
    }
}
