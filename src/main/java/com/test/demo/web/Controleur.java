package com.test.demo.web;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.test.demo.monitor.MonitorService;
import com.test.demo.philosopher.Philosopher;

@Controller
public class Controleur {

    private final MonitorService monitorService = new MonitorService();
    private final List<Philosopher> philosophers = List.of(
        new Philosopher(1, monitorService, new Random(1L)),
        new Philosopher(2, monitorService, new Random(2L)),
        new Philosopher(3, monitorService, new Random(3L)),
        new Philosopher(4, monitorService, new Random(4L)),
        new Philosopher(5, monitorService, new Random(5L))
    );

    public Controleur() {
        for (Philosopher philosopher : philosophers) {
            Thread thread = new Thread(philosopher, "philosopher-" + philosopher.getId());
            thread.setDaemon(true);
            thread.start();
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("events", monitorService.getDashboardRows());
        return "index";
    }
}