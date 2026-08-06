import httpClient from './httpClient'

const adminPath = (path) => `/admin${path}`

export const getAllSheltersForAdmin = () => {
  return httpClient.get(adminPath('/shelters'))
}

export const approveShelter = (email) => {
  return httpClient.get(adminPath(`/shelter/${email}/Approve`))
}

export const rejectShelter = (email) => {
  return httpClient.get(adminPath(`/shelter/${email}/Reject`))
}
