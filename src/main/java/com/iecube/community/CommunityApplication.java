package com.iecube.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@Controller
public class CommunityApplication {

    @GetMapping(value = {"/", "/{path:[^\\.]*}"})
//    public String forward() {
//        return "forward:/index.html";
//    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
