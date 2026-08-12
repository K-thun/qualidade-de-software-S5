package com.furreverhome.Furrever_Home.services.shelterService;

import com.furreverhome.Furrever_Home.dto.GenericResponse;
import com.furreverhome.Furrever_Home.dto.Pet.PetAdoptionRequestResponseDto;
import com.furreverhome.Furrever_Home.dto.Pet.PetDto;
import com.furreverhome.Furrever_Home.dto.petadopter.ShelterResponseDto;
import com.furreverhome.Furrever_Home.dto.shelter.RegisterPetRequest;

import java.util.List;

public interface ShelterService {
    PetDto registerPet(RegisterPetRequest registerPetRequest);
    PetDto editPet(Long petID, RegisterPetRequest updatePetRequest);
    GenericResponse deletePet(Long petId);
    boolean changeAdoptedStatus(Long petId, String status);
    List<PetDto> getPetsForShelter(Long shelterID);
    PetAdoptionRequestResponseDto getPetAdoptionRequests(Long petID);
    ShelterResponseDto getShelterDetailsById(Long userId);
}
