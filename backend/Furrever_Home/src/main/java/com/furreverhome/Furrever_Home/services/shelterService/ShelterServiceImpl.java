package com.furreverhome.Furrever_Home.services.shelterService;

import com.furreverhome.Furrever_Home.dto.GenericResponse;
import com.furreverhome.Furrever_Home.dto.Pet.PetAdoptionRequestResponseDto;
import com.furreverhome.Furrever_Home.dto.Pet.PetDto;
import com.furreverhome.Furrever_Home.dto.Pet.PetMapper;
import com.furreverhome.Furrever_Home.dto.petadopter.ShelterResponseDto;
import com.furreverhome.Furrever_Home.dto.shelter.RegisterPetRequest;
import com.furreverhome.Furrever_Home.entities.Pet;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.exception.UserNotFoundException;
import com.furreverhome.Furrever_Home.repository.AdopterPetRequestsRepository;
import com.furreverhome.Furrever_Home.repository.PetRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Shelter-side pet management (register/edit/delete pets, adoption
 * requests) and shelter profile lookup.
 * <p>
 * Entity&lt;-&gt;DTO mapping for {@link Pet} used to be duplicated inline
 * here (a private {@code mapPetToDto}/{@code updatePetFields} pair); it now
 * lives in {@link PetMapper}, following the same pattern already used for
 * user profiles in {@code UserProfileMapper}.
 */
@Service
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {

    private final PetRepository petRepository;
    private final ShelterRepository shelterRepository;
    private final AdopterPetRequestsRepository adopterPetRequestsRepository;
    private final UserRepository userRepository;
    private final PetMapper petMapper;

    /**
     * Registers a new pet with the provided details.
     *
     * @param registerPetRequest The request object containing the details of the pet to be registered.
     * @return The DTO representation of the registered pet.
     */
    public PetDto registerPet(RegisterPetRequest registerPetRequest) {
        Shelter shelter = shelterRepository.findById(registerPetRequest.getShelter()).orElse(null);
        Pet pet = petMapper.toEntity(registerPetRequest, shelter);
        petRepository.save(pet);

        return petMapper.toDto(pet);
    }

    /**
     * Edits the details of an existing pet.
     *
     * @param petID            The ID of the pet to be edited.
     * @param updatePetRequest The request object containing the updated details of the pet.
     * @return The DTO representation of the edited pet.
     * @throws RuntimeException If the pet with the specified ID is not found.
     */
    public PetDto editPet(Long petID, RegisterPetRequest updatePetRequest) {
        Optional<Pet> optionalPet = petRepository.findById(petID);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            petMapper.updateEntity(pet, updatePetRequest);
            petRepository.save(pet);
            return petMapper.toDto(pet);
        } else {
            throw new RuntimeException("Pet with ID " + petID + " not found");
        }
    }

    /**
     * Deletes a pet with the specified ID.
     *
     * @param petId The ID of the pet to be deleted.
     * @return A generic response indicating the outcome of the deletion operation.
     * @throws RuntimeException If the pet with the specified ID is not found.
     */
    public GenericResponse deletePet(Long petId) {
        if (petRepository.existsById(petId)) {
            petRepository.deleteById(petId);
            return new GenericResponse("Pet deleted.");
        } else {
            throw new RuntimeException("Pet with ID " + petId + " not found");
        }
    }

    /**
     * Retrieves all pets associated with the specified shelter.
     *
     * @param shelterID The ID of the shelter whose pets are to be retrieved.
     * @return A list of DTO representations of pets associated with the shelter.
     * @throws RuntimeException If the shelter with the specified ID is not found.
     */
    public List<PetDto> getPetsForShelter(Long shelterID) {
        Optional<Shelter> optionalShelter = shelterRepository.findById(shelterID);
        if (optionalShelter.isPresent()) {
            Shelter shelter = optionalShelter.get();
            List<Pet> pets = petRepository.findByShelter(shelter);
            return pets.stream()
                    .map(petMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Shelter with ID " + shelterID + " not found");
        }
    }

    /**
     * Changes the adoption status of a pet.
     *
     * @param petId  The ID of the pet whose adoption status is to be changed.
     * @param status The new adoption status ("Adopted" or "Not Adopted").
     * @return True if the adoption status is successfully updated, false otherwise.
     */
    @Override
    public boolean changeAdoptedStatus(Long petId, String status) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            if (Objects.equals(status, "Adopted")) {
                pet.setAdopted(Boolean.TRUE);
            } else {
                pet.setAdopted(Boolean.FALSE);
            }
            petRepository.save(pet);
            return true;
        }
        return false;
    }

    /**
     * Retrieves all adoption requests for a pet.
     *
     * @param petID The ID of the pet for which adoption requests are to be retrieved.
     * @return The DTO representation of adoption requests for the specified pet.
     * @throws RuntimeException If no adoption requests are found for the specified pet.
     */
    public PetAdoptionRequestResponseDto getPetAdoptionRequests(Long petID) {
        Optional<Pet> optionalPet = petRepository.findById(petID);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            List<PetAdopter> petAdopters = adopterPetRequestsRepository.findPetAdoptersByPet(pet);
            PetAdoptionRequestResponseDto petAdoptionRequestResponseDto = new PetAdoptionRequestResponseDto();
            petAdoptionRequestResponseDto.setPetID(petID);
            petAdoptionRequestResponseDto.setPetAdopters(petAdopters);
            return petAdoptionRequestResponseDto;
        } else {
            throw new RuntimeException("Requests with petID " + petID + " not found");
        }
    }

    /**
     * Retrieves the details of a shelter by its ID.
     *
     * @param userId The ID of the user associated with the shelter.
     * @return The DTO representation of the shelter details.
     * @throws UserNotFoundException If the user or shelter is not found.
     */
    @Override
    public ShelterResponseDto getShelterDetailsById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Shelter> optionalShelter = shelterRepository.findByUserId(user.getId());

            if (optionalShelter.isPresent()) {
                Shelter shelter = optionalShelter.get();
                return shelter.getShelterResponseDto();
            } else {
                throw new UserNotFoundException("Shelter Not Found");
            }
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }
}
