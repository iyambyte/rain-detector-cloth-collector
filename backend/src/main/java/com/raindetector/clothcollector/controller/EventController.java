package com.raindetector.clothcollector.controller;

import com.raindetector.clothcollector.entity.SystemEvent;
import com.raindetector.clothcollector.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<SystemEvent>> getEvents(@RequestParam(defaultValue = "1") Long deviceId) {
        return ResponseEntity.ok(eventService.getEventsForDevice(deviceId));
    }
}
