package com.raindetector.clothcollector.controller;

import com.raindetector.clothcollector.dto.CommandDto;
import com.raindetector.clothcollector.dto.DeviceStateDto;
import com.raindetector.clothcollector.entity.Device;
import com.raindetector.clothcollector.enums.OperatingMode;
import com.raindetector.clothcollector.service.DeviceService;
import com.raindetector.clothcollector.simulation.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SimulationService simulationService;

    @GetMapping("/{id}")
    public ResponseEntity<DeviceStateDto> getDeviceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceStateDto(id));
    }

    @PutMapping("/{id}/mode")
    public ResponseEntity<Void> changeMode(@PathVariable Long id, @RequestBody java.util.Map<String, String> payload) {
        String modeStr = payload.get("mode");
        deviceService.changeMode(id, OperatingMode.valueOf(modeStr.toUpperCase()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/commands")
    public ResponseEntity<String> sendCommand(@PathVariable Long id, @RequestBody CommandDto command) {
        try {
            simulationService.handleManualCommand(id, command.getCommand());
            return ResponseEntity.ok("Command accepted");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
