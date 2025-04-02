package com.authservice.dto;

import com.authservice.model.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceListResponse {
    private List<Device> devices;
}
