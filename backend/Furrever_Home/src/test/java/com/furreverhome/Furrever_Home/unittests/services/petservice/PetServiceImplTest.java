package com.furreverhome.Furrever_Home.unittests.services.petservice;

import com.furreverhome.Furrever_Home.dto.Pet.PetDto;
import com.furreverhome.Furrever_Home.dto.Pet.PetMapper;
import com.furreverhome.Furrever_Home.entities.Pet;
import com.furreverhome.Furrever_Home.entities.PetVaccination;
import com.furreverhome.Furrever_Home.repository.PetRepository;
import com.furreverhome.Furrever_Home.repository.PetVaccinationInfoRepository;
import com.furreverhome.Furrever_Home.repository.PetVaccinationRepository;
import com.furreverhome.Furrever_Home.services.petservice.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private PetVaccinationRepository petVaccinationRepository;
    @Mock
    private PetVaccinationInfoRepository petVaccinationInfoRepository;
    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetServiceImpl petService;

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setPetID(1L);
        pet.setType("Dog");
    }

    @Test
    void getPetInfo_whenPetExists_returnsDtoWithVaccineNames() {
        PetVaccination rabies = new PetVaccination();
        rabies.setVaccineName("Rabies");

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petVaccinationRepository.findByPet(pet)).thenReturn(List.of(rabies));

        PetDto expectedDto = new PetDto();
        expectedDto.setPetID(1L);
        when(petMapper.toDto(pet, List.of("Rabies"))).thenReturn(expectedDto);

        PetDto result = petService.getPetInfo(1L);

        assertEquals(expectedDto, result);
    }

    @Test
    void getPetInfo_whenPetDoesNotExist_throwsRuntimeException() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> petService.getPetInfo(99L));
    }
}
