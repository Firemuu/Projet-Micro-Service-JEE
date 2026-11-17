package com.test.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PhilosopherController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }
}
