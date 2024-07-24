package com.concurrency.org.concurrency.config;

import com.concurrency.org.concurrency.dto.MessageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component @Slf4j
public class Receiver {

    public void receiveMessage(String m) throws JsonProcessingException {
        MessageData message = new ObjectMapper().readValue(m, MessageData.class);
        try(FileWriter pw = new FileWriter("output.csv", true)) {
            pw.append(message.getRequestId());
            pw.append(",");
            pw.append(message.getMessage());
            pw.append(",");
            pw.append(message.getUserId());
            pw.append(",");
            pw.append(message.getThreadName());
            pw.append("\n");
            pw.flush();
        } catch (IOException e) {
            log.error("Error : {}", e);
        }
    }

}
