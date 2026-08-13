package com.raindetector.clothcollector.service;

import com.raindetector.clothcollector.entity.Device;
import com.raindetector.clothcollector.entity.SystemEvent;
import com.raindetector.clothcollector.enums.EventType;
import com.raindetector.clothcollector.repository.SystemEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private SystemEventRepository systemEventRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void logEvent(Device device, EventType eventType, String description) {
        SystemEvent event = new SystemEvent();
        event.setDevice(device);
        event.setEventType(eventType);
        event.setDescription(description);
        systemEventRepository.save(event);
        
        // Broadcast event via WebSocket to update frontend Toasts/Notifications
        messagingTemplate.convertAndSend("/topic/events", event);
    }

    public List<SystemEvent> getEventsForDevice(Long deviceId) {
        return systemEventRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
    }
}
