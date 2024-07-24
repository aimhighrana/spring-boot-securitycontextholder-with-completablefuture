package com.concurrency.org.concurrency.service;

import com.concurrency.org.concurrency.config.QueueConfig;
import com.concurrency.org.concurrency.dto.MessageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service @Slf4j @RequiredArgsConstructor
public class ConcurrencyService {

    private final RabbitTemplate rabbitTemplate;

    private final ConcurrencyAsyncService concurrencyAsyncService;

    private ObjectMapper om = new ObjectMapper();

    public String userDetails(String requestId) throws JsonProcessingException {
         String name = SecurityContextHolder.getContext().getAuthentication().getName();
        rabbitTemplate.convertAndSend(QueueConfig.queueName,om.writeValueAsString(new MessageData(name, requestId,"Inside userDetails", Thread.currentThread().getName())));

        // Call in Future
        runInFuture(requestId);

        String afFName = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!name.equalsIgnoreCase(afFName)) {
            log.info("User not found : {} and afName : {} - Future", name, afFName);
        }

        rabbitTemplate.convertAndSend(QueueConfig.queueName,om.writeValueAsString(
                new MessageData(afFName, requestId,"After runInFuture", Thread.currentThread().getName())));

         // call async
        concurrencyAsyncService.runMeInAsync(requestId);

        String afName = SecurityContextHolder.getContext().getAuthentication().getName();

        rabbitTemplate.convertAndSend(QueueConfig.queueName,om.writeValueAsString(
                new MessageData(afName, requestId,"After runMeInAsync", Thread.currentThread().getName())));

        if(!name.equalsIgnoreCase(afName)) {
            log.info("User not found : {} and afName : {}", name, afName);
        }
        return name;
    }


    public void runInFuture(String requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CompletableFuture.runAsync(()->{
            if(SecurityContextHolder.getContext().getAuthentication() !=null && !authentication.getName().equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName())) {
                log.info("Found wrong context here as current user : {} and found for this : {} , thread name : {}",
                        authentication.getName(), SecurityContextHolder.getContext().getAuthentication().getName(), Thread.currentThread().getName());
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            try {
                if(name.equalsIgnoreCase("user1"))
                    Thread.sleep(800);
                rabbitTemplate.convertAndSend(QueueConfig.queueName,om.writeValueAsString(
                        new MessageData(name, requestId,"Inside runInFuture", Thread.currentThread().getName())));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            // change the above with
            /*
           try {
               if(SecurityContextHolder.getContext().getAuthentication() !=null && !authentication.getName().equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName())) {
                   log.info("Found wrong context here as current user : {} and found for this : {} , thread name : {}",
                           authentication.getName(), SecurityContextHolder.getContext().getAuthentication().getName(), Thread.currentThread().getName());
               }
               SecurityContextHolder.getContext().setAuthentication(authentication);
               String name = SecurityContextHolder.getContext().getAuthentication().getName();
               try {
                   if(name.equalsIgnoreCase("user1"))
                       Thread.sleep(800);
                   rabbitTemplate.convertAndSend(QueueConfig.queueName,om.writeValueAsString(
                           new MessageData(name, requestId,"Inside runInFuture", Thread.currentThread().getName())));
               } catch (JsonProcessingException e) {
                   throw new RuntimeException(e);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           } finally {
               SecurityContextHolder.clearContext();
           }
           */

        });
    }
}
