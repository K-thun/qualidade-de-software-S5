import httpClient from './httpClient'

export const getShelterProfile = (shelterId) => {
  return httpClient.get(`/shelters/${shelterId}`)
}

export const updateShelterProfile = (userId, profileData) => {
  return httpClient.put(`/shelters/${userId}`, profileData)
}

export const getPetsForShelter = (shelterId) => {
  return httpClient.get(`/shelter/${shelterId}/pets`)
}

export const registerPet = (petData) => {
  return httpClient.post('/shelter/registerPet', petData)
}

export const editPet = (petId, petData) => {
  return httpClient.post(`/shelter/editPet/${petId}`, petData)
}

export const deletePet = (petId) => {
  return httpClient.delete(`/shelter/deletePet/${petId}`)
}

export const getPetById = (petId) => {
  return httpClient.get(`/shelter/${petId}`)
}

export const addVaccine = (petId, vaccineData) => {
  return httpClient.post(`/shelter/${petId}/addvaccine`, vaccineData)
}

export const getAdoptionRequests = (petId) => {
  return httpClient.get(`/shelter/${petId}/adoptionrequests`)
}
