package com.raindetector.clothcollector.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "settings")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "auto_retract")
    private boolean autoRetract = true;

    @Column(name = "auto_extend")
    private boolean autoExtend = true;

    @Column(name = "simulation_duration_ms")
    private int simulationDurationMs = 5000;

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

    public boolean isAutoRetract() { return autoRetract; }
    public void setAutoRetract(boolean autoRetract) { this.autoRetract = autoRetract; }

    public boolean isAutoExtend() { return autoExtend; }
    public void setAutoExtend(boolean autoExtend) { this.autoExtend = autoExtend; }

    public int getSimulationDurationMs() { return simulationDurationMs; }
    public void setSimulationDurationMs(int simulationDurationMs) { this.simulationDurationMs = simulationDurationMs; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
