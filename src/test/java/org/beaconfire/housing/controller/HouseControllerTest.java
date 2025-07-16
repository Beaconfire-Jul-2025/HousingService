package org.beaconfire.housing.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.beaconfire.housing.config.SecurityConfig;
import org.beaconfire.housing.dto.HouseDTO;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.filter.HeaderAuthenticationFilter;
import org.beaconfire.housing.service.HouseService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HouseController.class)
@Import({SecurityConfig.class, HeaderAuthenticationFilter.class})
class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HouseService houseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllHouses_asHR_shouldReturnPage() throws Exception {
        Page<House> page = new PageImpl<>(Collections.singletonList(new House()));
        when(houseService.getHouses(any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/house")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc")
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void getHouseById_shouldReturnHouse() throws Exception {
        House house = new House();
        house.setId(1);
        when(houseService.getHouseById(1)).thenReturn(house);

        mockMvc.perform(get("/house/1")
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void createHouse_shouldSucceed() throws Exception {
        HouseDTO dto = new HouseDTO();
        dto.setAddress("123");
        dto.setLandlordId(2);
        dto.setMaxOccupant(5);

        when(houseService.createHouse(any())).thenReturn(new House());

        mockMvc.perform(post("/house")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void updateHouse_shouldSucceed() throws Exception {
        HouseDTO dto = new HouseDTO();
        dto.setAddress("Updated");
        dto.setLandlordId(2);
        dto.setMaxOccupant(4);

        when(houseService.houseExists(1)).thenReturn(true);
        when(houseService.updateHouse(any())).thenReturn(new House());

        mockMvc.perform(put("/house/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteHouse_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/house/1")
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());

        verify(houseService).deleteHouseById(1);
    }

    @Test
    void getCurrentOccupant_shouldReturnInt() throws Exception {
        when(houseService.getCurrentOccupant(1)).thenReturn(3);

        mockMvc.perform(get("/house/1/current-occupant")
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void increaseOccupant_shouldReturnNewCount() throws Exception {
        when(houseService.incrementOccupant(1)).thenReturn(4);

        mockMvc.perform(post("/house/1/current-occupant/increase")
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }

    @Test
    void decreaseOccupant_shouldReturnNewCount() throws Exception {
        when(houseService.decrementOccupant(1)).thenReturn(2);

        mockMvc.perform(post("/house/1/current-occupant/decrease")
                        .header("x-User-Id", "1")
                        .header("x-Username", "HR")
                        .header("x-Roles", "ROLE_HR"))
                .andExpect(status().isOk());
    }
}
