package com.example.auth_service.controller;

import com.example.auth_service.dto.CdeMonnaiesRequest;
import com.example.auth_service.service.CdeMonnaiesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cde-monnaies")
public class CdeMonnaiesController {

    private final CdeMonnaiesService cdeMonnaiesService;

    public CdeMonnaiesController(CdeMonnaiesService cdeMonnaiesService) {
        this.cdeMonnaiesService = cdeMonnaiesService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CdeMonnaiesRequest request) {
        return cdeMonnaiesService.create(request);
    }

    @GetMapping
    public ResponseEntity<?> listForSameCaisseCentrale(@RequestParam String codeAgence) {
        return cdeMonnaiesService.listForSameCaisseCentrale(codeAgence);
    }

    @GetMapping("/agency")
    public ResponseEntity<?> listForAgency(@RequestParam String codeAgence) {
        return cdeMonnaiesService.listForAgency(codeAgence);
    }
}
