import httpClient from './httpClient'

export const signup = (signupRequest) => {
  return httpClient.post('/auth/signup', signupRequest)
}

export const signin = (email, password) => {
  return httpClient.post('/auth/signin', { email, password })
}

export const forgotPassword = (email) => {
  return httpClient.post('/auth/forgetPassword', { email })
}

export const resetPassword = ({ token, newPassword, verifyNewPassword }) => {
  return httpClient.post('/auth/resetPassword', { token, newPassword, verifyNewPassword })
}
