package cl.duoc.levelup.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class SimpleController {

    @GetMapping("/")
    @CrossOrigin(origins = "*")
    public String root() {
        return "LevelUp Gamer API is running!";
    }

    @GetMapping("/health") 
    @CrossOrigin(origins = "*")
    public String health() {
        return "OK";
    }

    @PostMapping("/test/register")
    @CrossOrigin(origins = "*")
    public String testRegister(@RequestBody String data) {
        return "{\"message\": \"Test registration endpoint working\", \"received\": \"" + data + "\"}";
    }

    @PostMapping("/test/login")
    @CrossOrigin(origins = "*") 
    public String testLogin(@RequestBody String data) {
        return "{\"message\": \"Test login endpoint working\", \"received\": \"" + data + "\"}";
    }
}