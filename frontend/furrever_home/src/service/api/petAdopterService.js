import httpClient from './httpClient'

export const getPetAdopterProfile = (userId) => {
  return httpClient.get(`/petadopter/${userId}`)
}

export const getAllShelters = () => {
  return httpClient.get('/petadopter/shelters')
}

export const getShelterById = (shelterId) => {
  return httpClient.get(`/shelters/${shelterId}`)
}

export const getPetsForShelter = (shelterId) => {
  return httpClient.get(`/petadopter/${shelterId}/pets`)
}

export const searchShelter = (searchShelterDto) => {
  return httpClient.post('/petadopter/searchshelter', searchShelterDto)
}

export const searchPet = (searchPetDto) => {
  return httpClient.post('/petadopter/searchpet', searchPetDto)
}

export const getPetInfo = (petId) => {
  return httpClient.get(`/petadopter/pets/${petId}`)
}

export const adoptPetRequest = (petAdoptionRequestDto) => {
  return httpClient.post('/petadopter/pet/adopt', petAdoptionRequestDto)
}

export const adoptionRequestExists = (petId, petAdopterId) => {
  return httpClient.get('/petadopter/pet/adopt/requestexists', {
    params: { petID: petId, petAdopterID: petAdopterId },
  })
}
