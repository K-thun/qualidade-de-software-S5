package com.furreverhome.Furrever_Home.unittests.dto;

import com.furreverhome.Furrever_Home.dto.Pet.PetDto;
import com.furreverhome.Furrever_Home.dto.Pet.PetMapper;
import com.furreverhome.Furrever_Home.dto.shelter.RegisterPetRequest;
import com.furreverhome.Furrever_Home.entities.Pet;
import com.furreverhome.Furrever_Home.entities.Shelter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PetMapperTest {

    private PetMapper petMapper;

    @BeforeEach
    void setUp() {
        petMapper = new PetMapper();
    }

    @Test
    void toEntity_mapsAllFieldsAndAssignsShelter() {
        RegisterPetRequest request = new RegisterPetRequest();
        request.setType("Dog");
        request.setBreed("Golden Retriever");
        request.setColour("Golden");
        request.setGender("Male");
        Date birthdate = new Date();
        request.setBirthdate(birthdate);
        request.setPetImage("base64Image");

        Shelter shelter = new Shelter();
        shelter.setId(5L);

        Pet pet = petMapper.toEntity(request, shelter);

        assertEquals("Dog", pet.getType());
        assertEquals("Golden Retriever", pet.getBreed());
        assertEquals("Golden", pet.getColour());
        assertEquals("Male", pet.getGender());
        assertEquals(birthdate, pet.getBirthdate());
        assertEquals("base64Image", pet.getPetImage());
        assertEquals(shelter, pet.getShelter());
    }

    @Test
    void toEntity_withNullShelter_doesNotThrow() {
        RegisterPetRequest request = new RegisterPetRequest();
        request.setType("Cat");

        Pet pet = petMapper.toEntity(request, null);

        assertEquals("Cat", pet.getType());
        assertNull(pet.getShelter());
    }

    @Test
    void updateEntity_onlyOverwritesNonNullFields() {
        Pet pet = new Pet();
        pet.setType("Dog");
        pet.setBreed("Labrador");
        pet.setColour("Black");
        pet.setGender("Female");
        pet.setAdopted(false);

        RegisterPetRequest updateRequest = new RegisterPetRequest();
        updateRequest.setBreed("Poodle");
        // type/colour/gender left null on purpose

        petMapper.updateEntity(pet, updateRequest);

        assertEquals("Dog", pet.getType(), "Fields left null in the request must not be overwritten");
        assertEquals("Poodle", pet.getBreed());
        assertEquals("Black", pet.getColour());
        assertEquals("Female", pet.getGender());
        assertFalse(pet.isAdopted());
    }

    @Test
    void updateEntity_whenAdoptedTrue_setsAdoptedTrue() {
        Pet pet = new Pet();
        pet.setAdopted(false);

        RegisterPetRequest updateRequest = new RegisterPetRequest();
        updateRequest.setAdopted(true);

        petMapper.updateEntity(pet, updateRequest);

        assertTrue(pet.isAdopted());
    }

    @Test
    void toDto_mapsAllFields() {
        Pet pet = new Pet();
        pet.setPetID(10L);
        pet.setType("Dog");
        pet.setBreed("Beagle");
        pet.setColour("Brown");
        pet.setGender("Male");
        Date birthdate = new Date();
        pet.setBirthdate(birthdate);
        pet.setPetImage("img");
        pet.setPetMedicalHistory("Healthy");
        pet.setAdopted(true);

        PetDto dto = petMapper.toDto(pet);

        assertEquals(10L, dto.getPetID());
        assertEquals("Dog", dto.getType());
        assertEquals("Beagle", dto.getBreed());
        assertEquals("Brown", dto.getColour());
        assertEquals("Male", dto.getGender());
        assertEquals(birthdate, dto.getBirthdate());
        assertEquals("img", dto.getPetImage());
        assertEquals("Healthy", dto.getPetMedicalHistory());
        assertTrue(dto.isAdopted());
    }

    @Test
    void toDto_withVaccineList_setsVaccineNamesOnTopOfBaseMapping() {
        Pet pet = new Pet();
        pet.setPetID(11L);
        pet.setType("Cat");

        java.util.List<String> vaccines = java.util.List.of("Rabies", "FVRCP");

        PetDto dto = petMapper.toDto(pet, vaccines);

        assertEquals(11L, dto.getPetID());
        assertEquals("Cat", dto.getType());
        assertEquals(vaccines, dto.getVaccineNameList());
    }
}
