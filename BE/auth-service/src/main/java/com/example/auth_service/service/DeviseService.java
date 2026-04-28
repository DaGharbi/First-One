package com.example.auth_service.service;

import com.example.auth_service.dto.DeviseInfo;
import com.example.auth_service.repository.DeviseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviseService {

    private final DeviseRepository deviseRepository;

    public DeviseService(DeviseRepository deviseRepository) {
        this.deviseRepository = deviseRepository;
    }

    public List<DeviseInfo> listDevises() {
        return deviseRepository.findAllByOrderByCodeDeviseAsc()
                .stream()
                .map(devise -> new DeviseInfo(
                        normalize(devise.getCodeDevise()),
                        normalize(devise.getLibDevise())
                ))
                .filter(devise -> devise.codeDevise() != null && !devise.codeDevise().isEmpty())
                .toList();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
