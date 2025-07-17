package org.beaconfire.housing.controller;

import org.beaconfire.housing.config.SecurityConfig;
import org.beaconfire.housing.dto.request.FacilityRequest;
import org.beaconfire.housing.filter.HeaderAuthenticationFilter;
import org.beaconfire.housing.service.HouseService;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.service.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacilityController.class)
@Import({SecurityConfig.class, HeaderAuthenticationFilter.class})
public class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private HouseService houseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllFacilities() throws Exception {
        Page<Facility> page = new PageImpl<>(Collections.singletonList(new Facility()));
        when(facilityService.getFacilities(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/facilities")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("sortDir", "asc")
                .header("x-User-Id", "1")
                .header("x-Username", "Admin")
                .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateFacility() throws Exception {
        FacilityRequest facilityRequest = new FacilityRequest();
        facilityRequest.setType("Wi-Fi");
        facilityRequest.setQuantity(1);
        facilityRequest.setHouseId(1);
        facilityRequest.setDescription("Shared Wi-Fi router");

        Facility facility = new Facility();
        facility.setType("Wi-Fi");

        when(facilityService.createFacility(any(Facility.class))).thenReturn(facility);

        mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facilityRequest))
                .header("x-User-Id", "1")
                .header("x-Username", "Admin")
                .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFacilityById() throws Exception {
        Facility facility = new Facility();
        facility.setId(1);
        facility.setType("Wi-Fi");

        when(facilityService.getFacilityById(1)).thenReturn(facility);

        mockMvc.perform(get("/facilities/1")
                .header("x-User-Id", "1")
                .header("x-Username", "Admin")
                .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateFacility() throws Exception {
        FacilityRequest request = new FacilityRequest();
        request.setType("Washer");
        request.setQuantity(2);
        request.setDescription("Updated washer");
        request.setHouseId(1);

        Facility facility = new Facility();
        facility.setId(1);
        facility.setType("Washer");
        facility.setQuantity(2);
        facility.setDescription("Updated washer");

        when(facilityService.updateFacility(eq(1), any(Facility.class))).thenReturn(facility);

        mockMvc.perform(put("/facilities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("x-User-Id", "1")
                .header("x-Username", "Admin")
                .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFacility() throws Exception {
        doNothing().when(facilityService).deleteFacility(1);

        mockMvc.perform(delete("/facilities/1")
                .header("x-User-Id", "1")
                .header("x-Username", "Admin")
                .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());

        verify(facilityService).deleteFacility(1);
    }
}
