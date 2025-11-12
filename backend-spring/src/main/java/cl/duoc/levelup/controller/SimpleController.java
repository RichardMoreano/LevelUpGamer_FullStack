package cl.duoc.levelup.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class SimpleController {

    @GetMapping("/")
    public String root() {
        return "LevelUp Gamer API is running!";
    }

    @GetMapping("/health") 
    public String health() {
        return "OK";
    }

    @PostMapping("/api/v1/auth/register")
    @CrossOrigin(origins = "*")
    public String register(@RequestBody String data) {
        return "{\"message\": \"Registration endpoint working\", \"received\": \"" + data + "\"}";
    }

    @PostMapping("/api/v1/auth/login")
    @CrossOrigin(origins = "*") 
    public String login(@RequestBody String data) {
        return "{\"message\": \"Login endpoint working\", \"received\": \"" + data + "\"}";
    }
}