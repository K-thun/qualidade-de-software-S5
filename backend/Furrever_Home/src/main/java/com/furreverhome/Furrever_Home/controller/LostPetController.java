package com.furreverhome.Furrever_Home.controller;

import com.furreverhome.Furrever_Home.dto.lostpet.LostPetDto;
import com.furreverhome.Furrever_Home.dto.lostpet.LostPetResponseDtoListDto;
import com.furreverhome.Furrever_Home.dto.lostpet.RegisterLostPetDto;
import com.furreverhome.Furrever_Home.dto.petadopter.SearchPetDto;
import com.furreverhome.Furrever_Home.entities.LostPet;
import com.furreverhome.Furrever_Home.services.petadopterservices.LostPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lost &amp; found pet reports: registration, search, retrieval, and updates.
 * <p>
 * Extracted from {@code PetAdopterController}, which previously bundled this
 * unrelated domain in with adopter/shelter/adoption-request endpoints simply
 * because they shared the {@code /api/petadopter} URL prefix. The URL prefix
 * is kept unchanged here so existing frontend calls keep working.
 */
@RestController
@RequestMapping("/api/petadopter")
@RequiredArgsConstructor
public class LostPetController {

    private final LostPetService lostPetService;

    /**
     * Registers a lost pet.
     * @param authorizationHeader The authorization header.
     * @param registerLostPetDto The details of the lost pet.
     * @return ResponseEntity containing the registered LostPet entity.
     */
    @PostMapping("/lostpet")
    public ResponseEntity<LostPet> registerLostPet(@Valid @RequestHeader("Authorization") String authorizationHeader, @RequestBody RegisterLostPetDto registerLostPetDto) {
        return ResponseEntity.ok(lostPetService.registerLostPet(authorizationHeader, registerLostPetDto));
    }

    /**
     * Searches for lost pets based on search criteria.
     * @param searchPetDto The search criteria.
     * @return ResponseEntity containing the search results.
     */
    @PostMapping("/searchlostpet")
    public ResponseEntity<LostPetResponseDtoListDto> searchLostPet(@RequestBody SearchPetDto searchPetDto) {
        return ResponseEntity.ok(lostPetService.searchLostPet(searchPetDto));
    }

    /**
     * Retrieves lost pets by user ID.
     * @param userId The ID of the user.
     * @return ResponseEntity containing the list of lost pets.
     */
    @GetMapping("/lostpet/{userId}")
    public ResponseEntity<LostPetResponseDtoListDto> getLostPetBy(@PathVariable Long userId) {
        return ResponseEntity.ok(lostPetService.getLostPetListByUser(userId));
    }

    /**
     * Updates lost pet details.
     * @param lostPetDto The updated lost pet details.
     * @return ResponseEntity containing the result of the update operation.
     */
    @PostMapping("/lostpet/update")
    public ResponseEntity<?> updateLostPetDetails(@RequestBody LostPetDto lostPetDto) {
        return ResponseEntity.ok(lostPetService.updateLostPetDetails(lostPetDto));
    }

    /**
     * Retrieves all lost pets.
     * @return ResponseEntity containing a list of LostPetDto.
     */
    @GetMapping("/lostpets")
    public ResponseEntity<List<LostPetDto>> getAllLostpets() {
        List<LostPetDto> lostPetDtoList = lostPetService.getAllLostPets();
        return ResponseEntity.ok(lostPetDtoList);
    }
}
