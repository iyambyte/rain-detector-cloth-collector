package com.raindetector.clothcollector.repository;

import com.raindetector.clothcollector.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByDeviceId(Long deviceId);
}
