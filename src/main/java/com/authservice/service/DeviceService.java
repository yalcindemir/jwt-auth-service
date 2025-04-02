package com.authservice.service;

import com.authservice.model.Device;
import com.authservice.model.User;
import com.authservice.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;

    /**
     * Kullanıcının cihazlarını getirir
     */
    public List<Device> getUserDevices(User user) {
        return deviceRepository.findByUser(user);
    }

    /**
     * Kullanıcının cihazını MAC adresi ile kontrol eder
     */
    public boolean isDeviceRegistered(User user, String macAddress) {
        return deviceRepository.existsByUserAndMacAddress(user, macAddress);
    }

    /**
     * Cihazın engellenip engellenmediğini kontrol eder
     */
    public boolean isDeviceBlocked(User user, String macAddress) {
        Optional<Device> device = deviceRepository.findByUserAndMacAddress(user, macAddress);
        return device.map(Device::isBlocked).orElse(false);
    }

    /**
     * Cihaz kaydı yapar veya günceller
     */
    @Transactional
    public Device registerOrUpdateDevice(User user, String macAddress, String deviceType, String deviceName) {
        Optional<Device> existingDevice = deviceRepository.findByUserAndMacAddress(user, macAddress);
        
        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            device.setDeviceType(deviceType);
            device.setDeviceName(deviceName);
            device.setLastLoginAt(LocalDateTime.now());
            return deviceRepository.save(device);
        }
        
        Device device = Device.builder()
                .user(user)
                .macAddress(macAddress)
                .deviceType(deviceType)
                .deviceName(deviceName)
                .blocked(false)
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        return deviceRepository.save(device);
    }

    /**
     * Cihazı engeller
     */
    @Transactional
    public Device blockDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Cihaz bulunamadı"));
        
        device.setBlocked(true);
        return deviceRepository.save(device);
    }

    /**
     * Cihaz engelini kaldırır
     */
    @Transactional
    public Device unblockDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Cihaz bulunamadı"));
        
        device.setBlocked(false);
        return deviceRepository.save(device);
    }

    /**
     * MAC adresi ile cihazları arar
     */
    public List<Device> findDevicesByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }
}
