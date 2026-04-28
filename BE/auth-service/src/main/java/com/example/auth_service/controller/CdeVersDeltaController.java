package com.example.auth_service.controller;

import com.example.auth_service.dto.CdeVersDeltaRequest;
import com.example.auth_service.service.CdeVersDeltaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cde-vers-delta")
public class CdeVersDeltaController {

    private final CdeVersDeltaService cdeVersDeltaService;

    public CdeVersDeltaController(CdeVersDeltaService cdeVersDeltaService) {
        this.cdeVersDeltaService = cdeVersDeltaService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CdeVersDeltaRequest request) {
        return cdeVersDeltaService.create(request);
    }

    @GetMapping
    public ResponseEntity<?> listForSameCaisseCentrale(@RequestParam String codeAgence) {
        return cdeVersDeltaService.listForSameCaisseCentrale(codeAgence);
    }

    @GetMapping("/agency")
    public ResponseEntity<?> listForAgency(@RequestParam String codeAgence) {
        return cdeVersDeltaService.listForAgency(codeAgence);
    }
}
