package com.raindetector.clothcollector.service;

import com.raindetector.clothcollector.dto.DeviceStateDto;
import com.raindetector.clothcollector.entity.Device;
import com.raindetector.clothcollector.entity.DeviceState;
import com.raindetector.clothcollector.entity.Setting;
import com.raindetector.clothcollector.enums.ClothesPosition;
import com.raindetector.clothcollector.enums.DeviceStatus;
import com.raindetector.clothcollector.enums.MotorStatus;
import com.raindetector.clothcollector.enums.OperatingMode;
import com.raindetector.clothcollector.repository.DeviceRepository;
import com.raindetector.clothcollector.repository.DeviceStateRepository;
import com.raindetector.clothcollector.repository.SettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceStateRepository deviceStateRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void initDefaultDevice() {
        if (deviceRepository.count() == 0) {
            Device device = new Device();
            device.setName("Main Cloth Collector");
            device.setStatus(DeviceStatus.ONLINE);
            device.setMode(OperatingMode.AUTO);
            deviceRepository.save(device);

            DeviceState state = new DeviceState();
            state.setDevice(device);
            state.setRainDetected(false);
            state.setClothesPosition(ClothesPosition.OUTSIDE);
            state.setMotorStatus(MotorStatus.STOPPED);
            deviceStateRepository.save(state);

            Setting setting = new Setting();
            setting.setDevice(device);
            settingRepository.save(setting);
        }
    }

    public Device getDevice(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }

    public DeviceState getDeviceState(Long deviceId) {
        return deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device State not found"));
    }

    @Transactional
    public void saveDeviceState(DeviceState state) {
        deviceStateRepository.save(state);
        // Broadcast the new state to all connected clients
        messagingTemplate.convertAndSend("/topic/device/" + state.getDevice().getId() + "/state", getDeviceStateDto(state.getDevice().getId()));
    }

    public DeviceStateDto getDeviceStateDto(Long deviceId) {
        Device device = getDevice(deviceId);
        DeviceState state = getDeviceState(deviceId);

        DeviceStateDto dto = new DeviceStateDto();
        dto.setDeviceId(device.getId());
        dto.setMode(device.getMode());
        dto.setDeviceStatus(device.getStatus());
        dto.setRainDetected(state.isRainDetected());
        dto.setClothesPosition(state.getClothesPosition());
        dto.setMotorStatus(state.getMotorStatus());
        dto.setLastUpdated(state.getUpdatedAt());

        return dto;
    }

    @Transactional
    public void changeMode(Long deviceId, OperatingMode mode) {
        Device device = getDevice(deviceId);
        device.setMode(mode);
        deviceRepository.save(device);
        
        // Broadcast state change
        messagingTemplate.convertAndSend("/topic/device/" + deviceId + "/state", getDeviceStateDto(deviceId));
    }
}
