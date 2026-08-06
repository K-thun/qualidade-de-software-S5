import httpClient from './httpClient'

export const getUser = (userId) => {
  return httpClient.get(`/users/${userId}`)
}

export const updateAdopterProfile = (userId, profileData) => {
  return httpClient.put(`/users/${userId}`, profileData)
}
