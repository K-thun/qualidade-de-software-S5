import httpClient from './httpClient'

export const getChatFrom = (userId, shelterId) => {
  return httpClient.get(`/chats/from/${userId}/to/${shelterId}`)
}

export const getChatHistory = (userId) => {
  return httpClient.get(`/chats/history/${userId}`)
}

export const getSingleShelter = (userId) => {
  return httpClient.get(`/shelter/single/${userId}`)
}
