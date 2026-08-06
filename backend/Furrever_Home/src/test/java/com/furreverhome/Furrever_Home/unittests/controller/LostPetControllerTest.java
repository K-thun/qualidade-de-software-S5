package com.furreverhome.Furrever_Home.unittests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furreverhome.Furrever_Home.controller.LostPetController;
import com.furreverhome.Furrever_Home.dto.lostpet.LostPetDto;
import com.furreverhome.Furrever_Home.dto.lostpet.LostPetResponseDtoListDto;
import com.furreverhome.Furrever_Home.dto.lostpet.RegisterLostPetDto;
import com.furreverhome.Furrever_Home.dto.petadopter.SearchPetDto;
import com.furreverhome.Furrever_Home.entities.LostPet;
import com.furreverhome.Furrever_Home.services.petadopterservices.LostPetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link LostPetController}. These cases used to live in
 * {@code PetAdopterControllerTest} before the lost &amp; found endpoints
 * were split into their own controller.
 */
class LostPetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LostPetService lostPetService;

    @InjectMocks
    private LostPetController lostPetController;

    private LostPet lostPet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lostPetController).build();

        lostPet = new LostPet();
        lostPet.setType("Dog");
        lostPet.setBreed("Golden Retriever");
        lostPet.setColour("Golden");
        lostPet.setGender("Male");
    }

    /**
     * Test case to verify registering a lost pet.
     */
    @Test
    void testRegisterLostPet() throws Exception {
        RegisterLostPetDto registerLostPetDto = new RegisterLostPetDto();

        given(lostPetService.registerLostPet(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(RegisterLostPetDto.class)))
                .willReturn(lostPet);

        mockMvc.perform(post("/api/petadopter/lostpet")
                        .header("Authorization", "Bearer sometoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(registerLostPetDto)))
                .andExpect(status().isOk());
    }

    /**
     * Test case to verify searching for lost pets.
     */
    @Test
    void testSearchLostPet() throws Exception {
        SearchPetDto searchPetDto = new SearchPetDto();

        LostPetResponseDtoListDto lostPetResponseDtoListDto = new LostPetResponseDtoListDto();

        given(lostPetService.searchLostPet(searchPetDto)).willReturn(lostPetResponseDtoListDto);

        mockMvc.perform(post("/api/petadopter/searchlostpet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(searchPetDto)))
                .andExpect(status().isOk());
    }

    /**
     * Test case to verify retrieving lost pets by user ID.
     */
    @Test
    void testGetLostPetBy() throws Exception {
        LostPetResponseDtoListDto lostPetResponseDtoListDto = new LostPetResponseDtoListDto();
        Long userId = 1L;
        given(lostPetService.getLostPetListByUser(userId)).willReturn(lostPetResponseDtoListDto);

        mockMvc.perform(get("/api/petadopter/lostpet/{userId}", userId))
                .andExpect(status().isOk());
    }

    /**
     * Test case to verify updating lost pet details.
     */
    @Test
    void testUpdateLostPetDetails() throws Exception {
        LostPetDto lostPetDto = new LostPetDto();

        given(lostPetService.updateLostPetDetails(lostPetDto)).willReturn(true);

        mockMvc.perform(post("/api/petadopter/lostpet/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(lostPetDto)))
                .andExpect(status().isOk());
    }

    /**
     * Test case to verify retrieving all lost pets.
     */
    @Test
    void testGetAllLostpets() throws Exception {
        List<LostPetDto> lostPetDtoList = new ArrayList<>();
        lostPetDtoList.add(new LostPetDto());

        given(lostPetService.getAllLostPets()).willReturn(lostPetDtoList);

        mockMvc.perform(get("/api/petadopter/lostpets"))
                .andExpect(status().isOk());
    }

    private String toJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
