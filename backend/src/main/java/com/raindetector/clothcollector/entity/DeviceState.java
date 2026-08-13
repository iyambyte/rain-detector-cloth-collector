package com.raindetector.clothcollector.entity;

import com.raindetector.clothcollector.enums.ClothesPosition;
import com.raindetector.clothcollector.enums.MotorStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_states")
public class DeviceState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "rain_detected")
    private boolean rainDetected;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothes_position")
    private ClothesPosition clothesPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "motor_status")
    private MotorStatus motorStatus;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public boolean isRainDetected() { return rainDetected; }
    public void setRainDetected(boolean rainDetected) { this.rainDetected = rainDetected; }

    public ClothesPosition getClothesPosition() { return clothesPosition; }
    public void setClothesPosition(ClothesPosition clothesPosition) { this.clothesPosition = clothesPosition; }

    public MotorStatus getMotorStatus() { return motorStatus; }
    public void setMotorStatus(MotorStatus motorStatus) { this.motorStatus = motorStatus; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
