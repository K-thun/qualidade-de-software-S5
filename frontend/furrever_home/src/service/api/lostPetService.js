import httpClient from './httpClient'

export const getAllLostPets = () => {
  return httpClient.get('/petadopter/lostpets')
}

export const getLostPetsByUser = (userId) => {
  return httpClient.get(`/petadopter/lostpet/${userId}`)
}

export const registerLostPet = (registerLostPetDto) => {
  return httpClient.post('/petadopter/lostpet', registerLostPetDto)
}

export const updateLostPet = (lostPetDto) => {
  return httpClient.post('/petadopter/lostpet/update', lostPetDto)
}
