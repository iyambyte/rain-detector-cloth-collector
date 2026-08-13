package com.raindetector.clothcollector.repository;

import com.raindetector.clothcollector.entity.SystemEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemEventRepository extends JpaRepository<SystemEvent, Long> {
    List<SystemEvent> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);
    Page<SystemEvent> findByDeviceIdOrderByCreatedAtDesc(Long deviceId, Pageable pageable);
}
