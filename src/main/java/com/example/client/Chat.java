package com.example.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class Chat {
    private final RestTemplate restTemplate;
    private boolean isFault = false;

    public Chat(RestTemplate rest) {
        this.restTemplate = rest;
    }

    @HystrixCommand(fallbackMethod = "fallbackServerGetMessages")
    public String getMessages() {
        URI uri = URI.create("http://localhost:9000/chat_main/all");

        return this.restTemplate.getForObject(uri, String.class);
    }

    @HystrixCommand(fallbackMethod = "fallbackServerSendMessage")
    public String sendMessage(String from, String content) {
        String formattedname = from.replace(" ", "+");
        String formattedMsg = content.replace(" ", "+");
        URI uri = URI.create("http://localhost:9000/chat_main/send?name=" + formattedname + "&message=" + formattedMsg);

        return this.restTemplate.getForObject(uri, String.class);
    }

    @HystrixCommand(fallbackMethod = "finalErrorGetFallback")
    public String fallbackServerGetMessages() {
        URI uri = URI.create("http://localhost:9001/chat_main/all");

        return this.restTemplate.getForObject(uri, String.class);
    }
    public String fallbackServerSendMessage(String from, String content) {
        isFault = false;
        String formattedname = from.replace(" ", "+");
        String formattedMsg = content.replace(" ", "+");
        URI uri = URI.create("http://localhost:9001/chat_main/send?name=" + formattedname + "&message=" + formattedMsg);

        return this.restTemplate.getForObject(uri, String.class);
    }

    public String finalErrorGetFallback() {
        return "SERVERS ARE UNAVAILABLE, TRY LATER...";
    }

    public String finalErrorSendFallback() {
        isFault = true;
        return "SERVERS ARE UNAVAILABLE, TRY LATER...";
    }

    public boolean getState() {
        return  isFault;
    }
}
