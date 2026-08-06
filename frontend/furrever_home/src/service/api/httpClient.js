import axios from 'axios'
import { readLocalStorage } from '../../utils/helper'

/**
 * Single axios instance for the whole app.
 *
 * Before this file existed, every component built its own `baseurl` string
 * from `import.meta.env.VITE_BACKEND_BASE_URL` and repeated the same
 * `Authorization: Bearer ${token}` header by hand (~26 call sites). That
 * meant a typo'd header or a backend route rename had to be hunted down
 * file by file. Domain-specific service modules (authService,
 * petAdopterService, etc.) should import this client instead of calling
 * axios directly.
 */
const httpClient = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_BASE_URL,
})

httpClient.interceptors.request.use((config) => {
  const token = readLocalStorage('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default httpClient
