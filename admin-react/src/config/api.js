// Configuración de API para Level-Up Gamer
// Detecta automáticamente si está en producción o desarrollo

const isProd = import.meta.env.PROD;
const isDev = import.meta.env.DEV;

// URLs de API según el entorno
export const API_CONFIG = {
  // Backend Spring Boot
  BASE_URL: isProd 
    ? 'https://levelup-gamer-backend-production.up.railway.app/api/v1'  // Cambiar por tu URL de Railway
    : 'http://localhost:8080/api/v1',
  
  // Timeout para requests
  TIMEOUT: 10000,
  
  // Headers por defecto
  DEFAULT_HEADERS: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
};

// Endpoints de la API
export const API_ENDPOINTS = {
  // Autenticación
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout'
  },
  
  // Usuarios
  USERS: {
    ME: '/usuarios/me',
    UPDATE_PROFILE: '/usuarios/me',
    CHANGE_PASSWORD: '/usuarios/me/cambiar-password',
    GET_POINTS: '/usuarios/puntos',
    LIST: '/usuarios',
    BY_ID: (run) => `/usuarios/${run}`,
    ACTIVATE: (run) => `/usuarios/${run}/activar`,
    DEACTIVATE: (run) => `/usuarios/${run}/desactivar`,
    ADD_POINTS: (run) => `/usuarios/${run}/puntos`
  },
  
  // Productos
  PRODUCTS: {
    LIST: '/productos',
    ACTIVE: '/productos/activos',
    BY_ID: (codigo) => `/productos/${codigo}`,
    BY_CATEGORY: (categoria) => `/productos/categoria/${categoria}`,
    SEARCH: '/productos/buscar',
    CATEGORIES: '/productos/categorias',
    CRITICAL_STOCK: '/productos/stock-critico',
    CREATE: '/productos',
    UPDATE: (codigo) => `/productos/${codigo}`,
    DELETE: (codigo) => `/productos/${codigo}`,
    UPDATE_STOCK: (codigo) => `/productos/${codigo}/stock`,
    CHECK_AVAILABILITY: (codigo) => `/productos/${codigo}/disponibilidad`
  }
};

// Utilitario para construir URLs completas
export const buildApiUrl = (endpoint) => {
  return `${API_CONFIG.BASE_URL}${endpoint}`;
};

// Utilitario para obtener headers con autenticación
export const getAuthHeaders = () => {
  const token = localStorage.getItem('authToken');
  return {
    ...API_CONFIG.DEFAULT_HEADERS,
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

// Configuración para fetch requests
export const createFetchConfig = (method = 'GET', body = null, includeAuth = true) => {
  const config = {
    method,
    headers: includeAuth ? getAuthHeaders() : API_CONFIG.DEFAULT_HEADERS,
    timeout: API_CONFIG.TIMEOUT
  };
  
  if (body) {
    config.body = JSON.stringify(body);
  }
  
  return config;
};

// Debug info (solo en desarrollo)
if (isDev) {
  console.log('🔧 API Configuration:', {
    baseUrl: API_CONFIG.BASE_URL,
    environment: isProd ? 'production' : 'development',
    endpoints: Object.keys(API_ENDPOINTS)
  });
}