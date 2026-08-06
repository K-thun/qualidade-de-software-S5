package com.furreverhome.Furrever_Home.controller;

import com.furreverhome.Furrever_Home.dto.Pet.PetAdoptionRequestDto;
import com.furreverhome.Furrever_Home.dto.Pet.PetDto;
import com.furreverhome.Furrever_Home.dto.petadopter.PetAdopterDto;
import com.furreverhome.Furrever_Home.dto.petadopter.SearchPetDto;
import com.furreverhome.Furrever_Home.dto.petadopter.SearchShelterDto;
import com.furreverhome.Furrever_Home.dto.petadopter.ShelterResponseDto;
import com.furreverhome.Furrever_Home.services.petadopterservices.PetAdopterService;
import com.furreverhome.Furrever_Home.services.shelterService.ShelterService;
import com.furreverhome.Furrever_Home.services.petservice.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Pet-adopter profile, shelter search/browsing, and pet-adoption requests.
 * <p>
 * Lost &amp; found endpoints used to live here too; they now live in
 * {@link LostPetController}, which is a distinct domain (it doesn't require
 * an adopter/shelter relationship at all) that was only bundled into this
 * controller by accident of URL prefix.
 */
@RestController
@RequestMapping("/api/petadopter")
@RequiredArgsConstructor
public class PetAdopterController {

    private final PetAdopterService petAdopterService;
    private final PetService petService;
    private final ShelterService shelterService;

    /**
     * Retrieves all shelters.
     * @return ResponseEntity containing a list of ShelterResponseDto.
     */
    @GetMapping("/shelters")
    public ResponseEntity<List<ShelterResponseDto>> getAllShelters() {
        List<ShelterResponseDto> shelterResponseDtoList = petAdopterService.getAllShelter();
        return ResponseEntity.ok(shelterResponseDtoList);
    }

    /**
     * Retrieves pet adopter details by user ID.
     * @param userId The ID of the user.
     * @return ResponseEntity containing the PetAdopterDto.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<PetAdopterDto> getPetAdopterByUser (@PathVariable Long userId) {
        PetAdopterDto petAdopterDto = petAdopterService.getPetAdopterDetailsById(userId);
        if(petAdopterDto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(petAdopterDto);
    }

    /**
     * Searches for shelters based on search criteria.
     * @param searchShelterDto The search criteria.
     * @return ResponseEntity containing the search results.
     */
    @PostMapping("/searchshelter")
    public ResponseEntity<?> searchShelter(@RequestBody SearchShelterDto searchShelterDto) {
        return ResponseEntity.ok(petAdopterService.searchShelter(searchShelterDto));
    }

    /**
     * Searches for pets based on search criteria.
     * @param searchPetDto The search criteria.
     * @return ResponseEntity containing the search results.
     */
    @PostMapping("/searchpet")
    public ResponseEntity<?> searchPet(@RequestBody SearchPetDto searchPetDto) {
        return ResponseEntity.ok(petAdopterService.searchPet(searchPetDto));
    }

    /**
     * Handles a pet adoption request.
     * @param petAdoptionRequestDto The adoption request details.
     * @return ResponseEntity containing the result of the adoption request.
     */
    @PostMapping("/pet/adopt")
    public ResponseEntity<?> adoptPetRequest(@RequestBody PetAdoptionRequestDto petAdoptionRequestDto){
        return ResponseEntity.ok(petAdopterService.adoptPetRequest(petAdoptionRequestDto));
    }

    /**
     * Retrieves information about a pet by its ID.
     * @param petID The ID of the pet.
     * @return ResponseEntity containing the PetDto.
     */
    @GetMapping("/pets/{petID}")
    public ResponseEntity<PetDto> getPetInfo(@PathVariable Long petID){
        return ResponseEntity.ok(petService.getPetInfo(petID));
    }

    /**
     * Checks if an adoption request exists for a pet and a pet adopter.
     * @param petID The ID of the pet.
     * @param petAdopterID The ID of the pet adopter.
     * @return ResponseEntity indicating the existence of the request.
     */
    @GetMapping("/pet/adopt/requestexists")
    public ResponseEntity<?> requestExists( @RequestParam("petID") Long petID, @RequestParam("petAdopterID") Long petAdopterID){
        boolean success = petAdopterService.requestExists(petID,petAdopterID);
        if(success) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves pets in a shelter.
     * @param shelterID The ID of the shelter.
     * @return ResponseEntity containing the list of pets.
     */
    @GetMapping("/{shelterID}/pets")
    public ResponseEntity<List<PetDto>> getPetInShelter(@PathVariable Long shelterID) {
        return ResponseEntity.ok(shelterService.getPetsForShelter(shelterID));
    }
}
