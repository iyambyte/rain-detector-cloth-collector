package com.raindetector.clothcollector.simulation;

import com.raindetector.clothcollector.entity.Device;
import com.raindetector.clothcollector.entity.DeviceState;
import com.raindetector.clothcollector.enums.ClothesPosition;
import com.raindetector.clothcollector.enums.EventType;
import com.raindetector.clothcollector.enums.MotorStatus;
import com.raindetector.clothcollector.enums.OperatingMode;
import com.raindetector.clothcollector.service.DeviceService;
import com.raindetector.clothcollector.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SimulationService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private EventService eventService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final int SIMULATION_DELAY_SECONDS = 5; // Simulates motor moving time

    public void simulateRain(Long deviceId, boolean rainDetected) {
        Device device = deviceService.getDevice(deviceId);
        DeviceState state = deviceService.getDeviceState(deviceId);

        if (state.isRainDetected() == rainDetected) return; // No change

        state.setRainDetected(rainDetected);
        deviceService.saveDeviceState(state);

        EventType eventType = rainDetected ? EventType.RAIN_DETECTED : EventType.RAIN_STOPPED;
        eventService.logEvent(device, eventType, rainDetected ? "Rain has been detected." : "Rain has stopped.");

        // If in AUTO mode, trigger clothes movement based on rain
        if (device.getMode() == OperatingMode.AUTO) {
            if (rainDetected) {
                // Auto Retract
                startMovement(deviceId, "RETRACT", true);
            } else {
                // Auto Extend
                startMovement(deviceId, "EXTEND", true);
            }
        }
    }

    public void handleManualCommand(Long deviceId, String command) {
        Device device = deviceService.getDevice(deviceId);
        if (device.getMode() == OperatingMode.AUTO) {
            throw new IllegalStateException("Cannot accept manual commands in AUTO mode.");
        }
        startMovement(deviceId, command, false);
    }

    private void startMovement(Long deviceId, String command, boolean isAuto) {
        Device device = deviceService.getDevice(deviceId);
        DeviceState state = deviceService.getDeviceState(deviceId);

        if ("STOP".equalsIgnoreCase(command)) {
            if (state.getMotorStatus() == MotorStatus.RUNNING) {
                state.setMotorStatus(MotorStatus.STOPPED);
                state.setClothesPosition(ClothesPosition.PARTIAL);
                deviceService.saveDeviceState(state);
                eventService.logEvent(device, EventType.MOTOR_STOPPED, "Motor stopped mid-transit.");
            }
            return;
        }

        if ("RETRACT".equalsIgnoreCase(command)) {
            if (state.getClothesPosition() == ClothesPosition.INDOOR || state.getClothesPosition() == ClothesPosition.MOVING_INSIDE) {
                return; // Already indoor or moving indoor
            }
            state.setMotorStatus(MotorStatus.RUNNING);
            state.setClothesPosition(ClothesPosition.MOVING_INSIDE);
            deviceService.saveDeviceState(state);
            eventService.logEvent(device, EventType.MOTOR_STARTED, "Motor started retracting clothes.");

            // Schedule finish movement
            scheduleFinishMovement(deviceId, ClothesPosition.INDOOR, "RETRACT");
        } else if ("EXTEND".equalsIgnoreCase(command)) {
            if (state.getClothesPosition() == ClothesPosition.OUTSIDE || state.getClothesPosition() == ClothesPosition.MOVING_OUTSIDE) {
                return; // Already outside or moving outside
            }
            state.setMotorStatus(MotorStatus.RUNNING);
            state.setClothesPosition(ClothesPosition.MOVING_OUTSIDE);
            deviceService.saveDeviceState(state);
            eventService.logEvent(device, EventType.MOTOR_STARTED, "Motor started extending clothes.");

            // Schedule finish movement
            scheduleFinishMovement(deviceId, ClothesPosition.OUTSIDE, "EXTEND");
        }
    }

    private void scheduleFinishMovement(Long deviceId, ClothesPosition targetPosition, String commandContext) {
        scheduler.schedule(() -> {
            try {
                // Re-fetch state in case it was STOPPED midway
                DeviceState state = deviceService.getDeviceState(deviceId);
                if (state.getMotorStatus() == MotorStatus.RUNNING) {
                    // Check if the current moving state matches the expected context
                    if ((commandContext.equals("RETRACT") && state.getClothesPosition() == ClothesPosition.MOVING_INSIDE) ||
                        (commandContext.equals("EXTEND") && state.getClothesPosition() == ClothesPosition.MOVING_OUTSIDE)) {
                        
                        state.setMotorStatus(MotorStatus.STOPPED);
                        state.setClothesPosition(targetPosition);
                        deviceService.saveDeviceState(state);

                        Device device = deviceService.getDevice(deviceId);
                        EventType type = targetPosition == ClothesPosition.INDOOR ? EventType.CLOTHES_RETRACTED : EventType.CLOTHES_EXTENDED;
                        eventService.logEvent(device, type, "Movement completed. Clothes are now " + targetPosition);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log error (in real prod, use proper logger)
            }
        }, SIMULATION_DELAY_SECONDS, TimeUnit.SECONDS);
    }
}
