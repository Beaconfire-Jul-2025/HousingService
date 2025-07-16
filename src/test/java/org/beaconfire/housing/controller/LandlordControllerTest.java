package org.beaconfire.housing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.beaconfire.housing.config.SecurityConfig;
import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.filter.HeaderAuthenticationFilter;
import org.beaconfire.housing.service.LandlordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfig.class, HeaderAuthenticationFilter.class})
@WebMvcTest(LandlordController.class)
public class LandlordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LandlordService landlordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllLandlords() throws Exception {
        Landlord landlord = new Landlord();
        landlord.setId(1);
        landlord.setFirstName("John");
        landlord.setLastName("Doe");

        Page<Landlord> page = new PageImpl<>(Collections.singletonList(landlord));
        when(landlordService.getAllLandlords(eq("John"), eq("Doe"), eq(""), eq(""), any()))
                .thenReturn(page);

        mockMvc.perform(get("/landlord")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "")
                        .param("cellPhone", "")
                        .header("x-User-Id", "1")
                        .header("x-Username", "Admin")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateLandlord() throws Exception {
        Landlord landlord = new Landlord();
        landlord.setFirstName("Jane");
        landlord.setLastName("Doe");
        landlord.setCellPhone("1234567890");

        Landlord saved = new Landlord();
        saved.setId(2);
        saved.setFirstName("Jane");
        saved.setLastName("Doe");

        when(landlordService.createLandlord(any())).thenReturn(saved);

        mockMvc.perform(post("/landlord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(landlord))
                        .header("x-User-Id", "1")
                        .header("x-Username", "Admin")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteLandlord() throws Exception {
        doNothing().when(landlordService).deleteLandlord(1);

        mockMvc.perform(delete("/landlord/1")
                        .header("x-User-Id", "1")
                        .header("x-Username", "Admin")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());

        verify(landlordService).deleteLandlord(1);
    }
    @Test
    void testGetLandlordById() throws Exception {
        Landlord landlord = new Landlord();
        landlord.setId(1);
        landlord.setFirstName("Alice");
        landlord.setLastName("Smith");

        when(landlordService.getLandlordById(1)).thenReturn(landlord);

        mockMvc.perform(get("/landlord/1")
                        .header("x-User-Id", "1")
                        .header("x-Username", "Admin")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateLandlord() throws Exception {
        Landlord updated = new Landlord();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("User");
        updated.setCellPhone("9876543210");

        when(landlordService.updateLandlord(eq(1), any(Landlord.class))).thenReturn(updated);

        mockMvc.perform(put("/landlord/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated))
                        .header("x-User-Id", "1")
                        .header("x-Username", "Admin")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }
}
