package com.furreverhome.Furrever_Home.dto.petadopter;

import com.furreverhome.Furrever_Home.entities.Shelter;
import org.springframework.stereotype.Component;

/**
 * Maps {@link Shelter} to {@link ShelterResponseDto}.
 * <p>
 * This mapping used to live inside the {@code Shelter} entity itself
 * ({@code Shelter.getShelterResponseDto()}), coupling the JPA entity to a
 * specific API response shape. Moving it here follows the same pattern as
 * {@code PetMapper} and {@code UserProfileMapper}, and means the entity no
 * longer needs to know how it's presented over HTTP.
 */
@Component
public class ShelterMapper {

    public ShelterResponseDto toResponseDto(Shelter shelter) {
        ShelterResponseDto shelterResponseDto = new ShelterResponseDto();
        shelterResponseDto.setId(shelter.getId());
        shelterResponseDto.setName(shelter.getName());
        shelterResponseDto.setCapacity(shelter.getCapacity());
        shelterResponseDto.setAddress(shelter.getAddress());
        shelterResponseDto.setCity(shelter.getCity());
        shelterResponseDto.setCountry(shelter.getCountry());
        shelterResponseDto.setZipcode(shelter.getZipcode());
        shelterResponseDto.setContact(shelter.getContact());
        shelterResponseDto.setImage(shelter.getImageBase64());
        shelterResponseDto.setLicense(shelter.getLicense());
        shelterResponseDto.setEmail(shelter.getUser().getEmail());
        shelterResponseDto.setUser(shelter.getUser());
        shelterResponseDto.setVerified(shelter.getUser().getVerified());
        shelterResponseDto.setAccepted(shelter.isAccepted());
        shelterResponseDto.setRejected(shelter.isRejected());
        return shelterResponseDto;
    }
}
