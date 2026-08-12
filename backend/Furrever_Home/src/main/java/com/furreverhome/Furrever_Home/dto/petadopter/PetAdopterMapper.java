package com.furreverhome.Furrever_Home.dto.petadopter;

import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.enums.Role;
import org.springframework.stereotype.Component;

/**
 * Maps {@link PetAdopter} to {@link PetAdopterDto}.
 * <p>
 * This mapping used to live inside the {@code PetAdopter} entity itself
 * ({@code PetAdopter.getPetAdopterDto()}), coupling the JPA entity to a
 * specific API response shape — the same smell already fixed for
 * {@code Shelter}, {@code Pet}, and {@code LostPet}.
 */
@Component
public class PetAdopterMapper {

    public PetAdopterDto toDto(PetAdopter petAdopter) {
        PetAdopterDto petAdopterDto = new PetAdopterDto();
        petAdopterDto.setId(petAdopter.getId());
        petAdopterDto.setFirstname(petAdopter.getFirstname());
        petAdopterDto.setLastname(petAdopter.getLastname());
        petAdopterDto.setUserRole(Role.PETADOPTER);
        petAdopterDto.setEmail(petAdopter.getUser().getEmail());
        petAdopterDto.setAddress(petAdopter.getAddress());
        petAdopterDto.setCity(petAdopter.getCity());
        petAdopterDto.setZipcode(petAdopter.getZipcode());

        return petAdopterDto;
    }
}
