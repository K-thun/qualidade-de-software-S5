package com.furreverhome.Furrever_Home.dto.Pet;

import com.furreverhome.Furrever_Home.dto.shelter.RegisterPetRequest;
import com.furreverhome.Furrever_Home.entities.Pet;
import com.furreverhome.Furrever_Home.entities.Shelter;
import org.springframework.stereotype.Component;

/**
 * Maps between the {@link Pet} entity and its DTOs / requests.
 * Extracted from {@code ShelterServiceImpl}, which previously mixed this
 * field-by-field mapping logic in with persistence and business rules,
 * following the same pattern already used by {@code UserProfileMapper}.
 */
@Component
public class PetMapper {

    public Pet toEntity(RegisterPetRequest request, Shelter shelter) {
        Pet pet = new Pet();
        pet.setType(request.getType());
        pet.setBreed(request.getBreed());
        pet.setColour(request.getColour());
        pet.setGender(request.getGender());
        pet.setBirthdate(request.getBirthdate());
        pet.setPetImage(request.getPetImage());
        if (shelter != null) {
            pet.setShelter(shelter);
        }
        return pet;
    }

    /**
     * Applies only the non-null fields present in the update request onto the
     * existing pet entity (partial update semantics).
     */
    public void updateEntity(Pet pet, RegisterPetRequest updatePetRequest) {
        if (updatePetRequest.getType() != null) {
            pet.setType(updatePetRequest.getType());
        }
        if (updatePetRequest.getBreed() != null) {
            pet.setBreed(updatePetRequest.getBreed());
        }
        if (updatePetRequest.getColour() != null) {
            pet.setColour(updatePetRequest.getColour());
        }
        if (updatePetRequest.getGender() != null) {
            pet.setGender(updatePetRequest.getGender());
        }
        if (updatePetRequest.getBirthdate() != null) {
            pet.setBirthdate(updatePetRequest.getBirthdate());
        }
        if (updatePetRequest.getPetImage() != null) {
            pet.setPetImage(updatePetRequest.getPetImage());
        }
        if (updatePetRequest.getPetMedicalHistory() != null) {
            pet.setPetMedicalHistory(updatePetRequest.getPetMedicalHistory());
        }
        if (updatePetRequest.isAdopted()) {
            pet.setAdopted(true);
        }
    }

    public PetDto toDto(Pet pet) {
        PetDto petDto = new PetDto();
        petDto.setPetID(pet.getPetID());
        petDto.setType(pet.getType());
        petDto.setBreed(pet.getBreed());
        petDto.setColour(pet.getColour());
        petDto.setGender(pet.getGender());
        petDto.setBirthdate(pet.getBirthdate());
        petDto.setPetImage(pet.getPetImage());
        petDto.setPetMedicalHistory(pet.getPetMedicalHistory());
        petDto.setShelter(pet.getShelter());
        petDto.setAdopted(pet.isAdopted());
        return petDto;
    }
}
