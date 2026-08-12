package com.furreverhome.Furrever_Home.dto.Pet;

import com.furreverhome.Furrever_Home.dto.petadopter.PetResponseDto;
import com.furreverhome.Furrever_Home.dto.shelter.RegisterPetRequest;
import com.furreverhome.Furrever_Home.entities.Pet;
import com.furreverhome.Furrever_Home.entities.Shelter;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * Same as {@link #toDto(Pet)}, but also sets the vaccine name list.
     * Extracted from {@code PetServiceImpl}, which had its own copy of the
     * base field-by-field mapping (identical to {@link #toDto(Pet)}) just to
     * additionally attach the pet's vaccine names.
     */
    public PetDto toDto(Pet pet, List<String> vaccineNameList) {
        PetDto petDto = toDto(pet);
        petDto.setVaccineNameList(vaccineNameList);
        return petDto;
    }

    /**
     * Maps to the adopter-facing {@link PetResponseDto} (shelter name/city/contact
     * flattened in, unlike {@link #toDto(Pet)} which keeps the full {@code Shelter}).
     * Extracted from {@code Pet.getPetResponseDto()}, which coupled the JPA
     * entity to this specific API response shape.
     */
    public PetResponseDto toResponseDto(Pet pet) {
        PetResponseDto petResponseDto = new PetResponseDto();

        petResponseDto.setPetId(pet.getPetID());
        petResponseDto.setAge(pet.getAge());
        petResponseDto.setBreed(pet.getBreed());
        petResponseDto.setType(pet.getType());
        petResponseDto.setPetImage(pet.getPetImage());
        petResponseDto.setShelterName(pet.getShelter().getName());
        petResponseDto.setShelterCity(pet.getShelter().getCity());
        petResponseDto.setShelterContact(pet.getShelter().getContact());
        petResponseDto.setColor(pet.getColour());
        petResponseDto.setGender(pet.getGender());
        petResponseDto.setAdopted(pet.isAdopted());

        return petResponseDto;
    }
}
