package com.mahoni.userservice.controller;

import com.mahoni.schema.UserEventType;
import com.mahoni.userservice.kafka.SampleProducer;
import com.mahoni.userservice.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    @Autowired
    SampleProducer sampleProducer;

    @PostMapping("/test")
    public void test() {
        sampleProducer.sendToKafka(new User(1L, "test", "test@mail.com", "asdfaaaaa"), UserEventType.USER_CREATED);
    }
}
