package com.raindetector.clothcollector.dto;

import com.raindetector.clothcollector.enums.ClothesPosition;
import com.raindetector.clothcollector.enums.DeviceStatus;
import com.raindetector.clothcollector.enums.MotorStatus;
import com.raindetector.clothcollector.enums.OperatingMode;

import java.time.LocalDateTime;

public class DeviceStateDto {
    private Long deviceId;
    private boolean rainDetected;
    private ClothesPosition clothesPosition;
    private MotorStatus motorStatus;
    private OperatingMode mode;
    private DeviceStatus deviceStatus;
    private LocalDateTime lastUpdated;

    // Getters and Setters
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public boolean isRainDetected() { return rainDetected; }
    public void setRainDetected(boolean rainDetected) { this.rainDetected = rainDetected; }

    public ClothesPosition getClothesPosition() { return clothesPosition; }
    public void setClothesPosition(ClothesPosition clothesPosition) { this.clothesPosition = clothesPosition; }

    public MotorStatus getMotorStatus() { return motorStatus; }
    public void setMotorStatus(MotorStatus motorStatus) { this.motorStatus = motorStatus; }

    public OperatingMode getMode() { return mode; }
    public void setMode(OperatingMode mode) { this.mode = mode; }

    public DeviceStatus getDeviceStatus() { return deviceStatus; }
    public void setDeviceStatus(DeviceStatus deviceStatus) { this.deviceStatus = deviceStatus; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
