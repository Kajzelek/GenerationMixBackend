package org.example.generationmixbackend.controller;

import org.example.generationmixbackend.dto.DailyMixResponse;
import org.example.generationmixbackend.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/energy")
@CrossOrigin(origins = "*")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/daily-mix")
    public ResponseEntity<List<DailyMixResponse>> getDailyMix() {
        return ResponseEntity.ok(energyService.getThreeDayPrediction());
    }
}
