package com.example.auth_service.controller;

import com.example.auth_service.dto.DeviseInfo;
import com.example.auth_service.service.DeviseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/devises")
public class DeviseController {

    private final DeviseService deviseService;

    public DeviseController(DeviseService deviseService) {
        this.deviseService = deviseService;
    }

    @GetMapping
    public ResponseEntity<List<DeviseInfo>> listDevises() {
        return ResponseEntity.ok(deviseService.listDevises());
    }
}
