package com.authservice.repository;

import com.authservice.model.Device;
import com.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    
    List<Device> findByUser(User user);
    
    Optional<Device> findByUserAndMacAddress(User user, String macAddress);
    
    boolean existsByUserAndMacAddress(User user, String macAddress);
    
    List<Device> findByUserAndBlocked(User user, boolean blocked);
    
    List<Device> findByMacAddress(String macAddress);
}
