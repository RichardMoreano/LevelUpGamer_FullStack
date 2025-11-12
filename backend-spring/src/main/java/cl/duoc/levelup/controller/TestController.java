package cl.duoc.levelup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {"*"})
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is running!");
    }

    @PostMapping("/cors")
    public ResponseEntity<String> corsTest(@RequestBody String data) {
        return ResponseEntity.ok("CORS is working! Received: " + data);
    }
}