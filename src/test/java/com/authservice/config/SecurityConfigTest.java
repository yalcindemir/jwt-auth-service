package com.authservice.config;

import com.authservice.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void publicEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    public void securedEndpoints_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/authorization/check"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void securedEndpoints_shouldBeAccessibleWithAuthentication() throws Exception {
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void adminEndpoints_shouldRequireAdminRole() throws Exception {
        mockMvc.perform(get("/api/devices/search"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminEndpoints_shouldBeAccessibleWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/devices/search"))
                .andExpect(status().isOk());
    }
}
