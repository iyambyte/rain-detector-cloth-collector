package com.raindetector.clothcollector.repository;

import com.raindetector.clothcollector.entity.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceStateRepository extends JpaRepository<DeviceState, Long> {
    Optional<DeviceState> findByDeviceId(Long deviceId);
}
