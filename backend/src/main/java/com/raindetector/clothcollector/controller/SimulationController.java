package com.raindetector.clothcollector.controller;

import com.raindetector.clothcollector.simulation.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @PostMapping("/rain")
    public ResponseEntity<String> simulateRain(@RequestBody java.util.Map<String, Object> payload) {
        Long deviceId = payload.get("deviceId") != null ? Long.valueOf(payload.get("deviceId").toString()) : 1L;
        boolean detected = Boolean.parseBoolean(payload.get("detected").toString());
        
        simulationService.simulateRain(deviceId, detected);
        return ResponseEntity.ok("Rain simulation updated: " + detected);
    }
}
