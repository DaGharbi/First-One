package com.example.notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping
    public ResponseEntity<?> sendNotification(@RequestBody String message) {
        System.out.println("📢 Notification: " + message);
        return ResponseEntity.ok("Notification sent");
    }

    @GetMapping
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Notification service working");
    }
}