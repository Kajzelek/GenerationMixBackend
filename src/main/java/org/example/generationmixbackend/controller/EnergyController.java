package org.example.generationmixbackend.controller;

import org.example.generationmixbackend.dto.DailyMixResponse;
import org.example.generationmixbackend.dto.OptimalWindowResponse;
import org.example.generationmixbackend.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/optimal-window")
    public ResponseEntity<OptimalWindowResponse> getOptimalWindow(@RequestParam int hours) {
        try {
            OptimalWindowResponse response = energyService.getOptimalWindow(hours);
            if (response == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
