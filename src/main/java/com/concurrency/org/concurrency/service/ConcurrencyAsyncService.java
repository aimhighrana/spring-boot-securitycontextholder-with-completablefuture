package com.concurrency.org.concurrency.service;

import com.concurrency.org.concurrency.config.QueueConfig;
import com.concurrency.org.concurrency.dto.MessageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service @Slf4j @RequiredArgsConstructor
public class ConcurrencyAsyncService {

    private final RabbitTemplate rabbitTemplate;

    private ObjectMapper om = new ObjectMapper();

    @Async
    public void runMeInAsync(String requestId) throws JsonProcessingException {
        String name = null;
        if(SecurityContextHolder.getContext().getAuthentication() !=null ) {
            name = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        rabbitTemplate.convertAndSend(QueueConfig.queueName,om.writeValueAsString(new MessageData(name, requestId, "Inside runMeInAsync", Thread.currentThread().getName())));
        // String.format("name inside runMeInAsync: %s with requestId : %s", name, requestId)
    }
}
