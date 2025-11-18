/**
 * API Service para Level-Up Gamer
 * Reemplaza todas las funciones de localStorage con llamadas al backend
 */

// Configuración de la API
const API_CONFIG = {
  BASE_URL: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:8080/api/v1'
    : 'https://levelup-gamer-backend.up.railway.app/api/v1',
  TIMEOUT: 10000
};

// Cache local para mejorar performance
const cache = {
  productos: null,
  usuario: null,
  timestamp: 0
};

const CACHE_DURATION = 5 * 60 * 1000; // 5 minutos

// Utilidades HTTP
class ApiError extends Error {
  constructor(message, status, data = null) {
    super(message);
    this.status = status;
    this.data = data;
    this.name = 'ApiError';
  }
}

async function makeRequest(endpoint, options = {}) {
  const url = `${API_CONFIG.BASE_URL}${endpoint}`;
  const token = localStorage.getItem('authToken');
  
  const config = {
    timeout: API_CONFIG.TIMEOUT,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    ...options
  };

  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), config.timeout);

    const response = await fetch(url, {
      ...config,
      signal: controller.signal
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new ApiError(
        errorData.message || `HTTP ${response.status}`,
        response.status,
        errorData
      );
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return await response.json();
    }
    return await response.text();
  } catch (error) {
    if (error.name === 'AbortError') {
      throw new ApiError('Request timeout', 408);
    }
    if (error instanceof ApiError) {
      throw error;
    }
    // Fallback para cuando backend no está disponible
    console.warn('API not available, using localStorage fallback:', error.message);
    return null;
  }
}

// === AUTENTICACIÓN ===
export async function login(correo, password) {
  try {
    const response = await makeRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ correo, password })
    });

    if (response && response.accessToken) {
      localStorage.setItem('authToken', response.accessToken);
      localStorage.setItem('sesion', JSON.stringify({
        correo: response.usuario.correo,
        tipo: response.usuario.tipoUsuario
      }));
      cache.usuario = response.usuario;
      return response;
    }
    throw new ApiError('Login failed', 401);
  } catch (error) {
    // Fallback a localStorage para desarrollo
    return loginLocalStorage(correo, password);
  }
}

export async function register(userData) {
  try {
    const response = await makeRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData)
    });
    return response;
  } catch (error) {
    // Fallback a localStorage
    return registerLocalStorage(userData);
  }
}

export function logout() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('sesion');
  cache.usuario = null;
  // No necesitamos llamar al backend para logout con JWT
}

// === USUARIO ACTUAL ===
export async function usuarioActual() {
  // Verificar cache primero
  if (cache.usuario && (Date.now() - cache.timestamp) < CACHE_DURATION) {
    return cache.usuario;
  }

  const sesion = getSesionLocal();
  if (!sesion) return null;

  try {
    const response = await makeRequest('/usuarios/me');
    if (response) {
      cache.usuario = response;
      cache.timestamp = Date.now();
      return response;
    }
  } catch (error) {
    console.warn('Failed to fetch user from API, using localStorage');
  }

  // Fallback a localStorage
  return getUserFromLocalStorage(sesion.correo);
}

export async function esAdmin() {
  const usuario = await usuarioActual();
  return !!(usuario && usuario.tipoUsuario === "admin");
}

export async function esVendedor() {
  const usuario = await usuarioActual();
  return !!(usuario && usuario.tipoUsuario === "vendedor");
}

// === PRODUCTOS ===
export async function obtenerProductos() {
  // Verificar cache
  if (cache.productos && (Date.now() - cache.timestamp) < CACHE_DURATION) {
    return cache.productos;
  }

  try {
    const response = await makeRequest('/productos');
    if (response && Array.isArray(response)) {
      cache.productos = response;
      cache.timestamp = Date.now();
      return response;
    }
  } catch (error) {
    console.warn('Failed to fetch products from API, using localStorage');
  }

  // Fallback a localStorage
  return getFromLocalStorage('productos', []);
}

export async function obtenerProductoPorCodigo(codigo) {
  try {
    const response = await makeRequest(`/productos/${encodeURIComponent(codigo)}`);
    return response;
  } catch (error) {
    // Fallback
    const productos = await obtenerProductos();
    return productos.find(p => p.codigo === codigo) || null;
  }
}

export async function crearProducto(producto) {
  try {
    const response = await makeRequest('/productos', {
      method: 'POST',
      body: JSON.stringify(producto)
    });
    // Limpiar cache
    cache.productos = null;
    return response;
  } catch (error) {
    // Fallback a localStorage
    return createProductLocalStorage(producto);
  }
}

export async function actualizarProducto(codigo, producto) {
  try {
    const response = await makeRequest(`/productos/${encodeURIComponent(codigo)}`, {
      method: 'PUT',
      body: JSON.stringify(producto)
    });
    // Limpiar cache
    cache.productos = null;
    return response;
  } catch (error) {
    return updateProductLocalStorage(codigo, producto);
  }
}

// === CARRITO ===
export function obtenerCarrito() {
  // El carrito se mantiene en localStorage por ahora (sesión temporal)
  return getFromLocalStorage('carrito', []);
}

export function guardarCarrito(carrito) {
  localStorage.setItem('carrito', JSON.stringify(carrito));
}

export function agregarAlCarrito(codigo, cantidad = 1) {
  const carrito = obtenerCarrito();
  const index = carrito.findIndex(item => item.codigo === codigo);
  
  if (index >= 0) {
    carrito[index].cantidad = (carrito[index].cantidad || 0) + cantidad;
  } else {
    carrito.push({ codigo, cantidad });
  }
  
  guardarCarrito(carrito);
  return carrito;
}

export function quitarDelCarrito(codigo) {
  const carrito = obtenerCarrito().filter(item => item.codigo !== codigo);
  guardarCarrito(carrito);
  return carrito;
}

export function limpiarCarrito() {
  localStorage.removeItem('carrito');
}

// === PEDIDOS ===
export async function obtenerPedidos() {
  try {
    const response = await makeRequest('/pedidos');
    return Array.isArray(response) ? response : [];
  } catch (error) {
    return getFromLocalStorage('pedidos', []);
  }
}

export async function crearPedido(pedidoData) {
  try {
    const response = await makeRequest('/pedidos', {
      method: 'POST',
      body: JSON.stringify(pedidoData)
    });
    return response;
  } catch (error) {
    return createPedidoLocalStorage(pedidoData);
  }
}

export async function actualizarEstadoPedido(pedidoId, nuevoEstado) {
  try {
    const response = await makeRequest(`/pedidos/${pedidoId}/estado`, {
      method: 'PUT',
      body: JSON.stringify({ estado: nuevoEstado })
    });
    return response;
  } catch (error) {
    return updatePedidoLocalStorage(pedidoId, nuevoEstado);
  }
}

// === FUNCIONES AUXILIARES ===
function getSesionLocal() {
  try {
    const sesion = localStorage.getItem('sesion');
    return sesion ? JSON.parse(sesion) : null;
  } catch {
    return null;
  }
}

function getFromLocalStorage(key, defaultValue) {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch {
    return defaultValue;
  }
}

function getUserFromLocalStorage(correo) {
  const usuarios = getFromLocalStorage('usuarios', []);
  return usuarios.find(u => 
    u.correo && u.correo.toLowerCase() === correo.toLowerCase()
  ) || null;
}

// === FALLBACKS LOCALSTORAGE ===
function loginLocalStorage(correo, password) {
  const usuarios = getFromLocalStorage('usuarios', []);
  const usuario = usuarios.find(u => 
    u.correo && u.correo.toLowerCase() === correo.toLowerCase() && u.pass === password
  );

  if (!usuario) {
    throw new ApiError('Credenciales incorrectas', 401);
  }

  localStorage.setItem('sesion', JSON.stringify({
    correo: usuario.correo,
    tipo: usuario.tipoUsuario
  }));

  return {
    accessToken: 'fake-token-' + Date.now(),
    usuario: usuario
  };
}

function registerLocalStorage(userData) {
  const usuarios = getFromLocalStorage('usuarios', []);
  
  // Verificar si ya existe
  if (usuarios.some(u => u.correo.toLowerCase() === userData.correo.toLowerCase())) {
    throw new ApiError('El correo ya está registrado', 409);
  }

  const nuevoUsuario = {
    ...userData,
    id: Date.now(),
    fechaRegistro: new Date().toISOString(),
    tipoUsuario: userData.tipoUsuario || 'cliente'
  };

  usuarios.push(nuevoUsuario);
  localStorage.setItem('usuarios', JSON.stringify(usuarios));
  
  return nuevoUsuario;
}

function createProductLocalStorage(producto) {
  const productos = getFromLocalStorage('productos', []);
  
  // Verificar si ya existe
  if (productos.some(p => p.codigo === producto.codigo)) {
    throw new ApiError('El código de producto ya existe', 409);
  }

  const nuevoProducto = {
    ...producto,
    id: Date.now(),
    fechaCreacion: new Date().toISOString()
  };

  productos.push(nuevoProducto);
  localStorage.setItem('productos', JSON.stringify(productos));
  
  return nuevoProducto;
}

function updateProductLocalStorage(codigo, productoData) {
  const productos = getFromLocalStorage('productos', []);
  const index = productos.findIndex(p => p.codigo === codigo);
  
  if (index === -1) {
    throw new ApiError('Producto no encontrado', 404);
  }

  productos[index] = {
    ...productos[index],
    ...productoData,
    fechaModificacion: new Date().toISOString()
  };

  localStorage.setItem('productos', JSON.stringify(productos));
  return productos[index];
}

function createPedidoLocalStorage(pedidoData) {
  const pedidos = getFromLocalStorage('pedidos', []);
  
  const nuevoPedido = {
    ...pedidoData,
    id: 'PED-' + Date.now(),
    fecha: new Date().toISOString(),
    estado: 'pendiente'
  };

  pedidos.push(nuevoPedido);
  localStorage.setItem('pedidos', JSON.stringify(pedidos));
  
  return nuevoPedido;
}

function updatePedidoLocalStorage(pedidoId, nuevoEstado) {
  const pedidos = getFromLocalStorage('pedidos', []);
  const index = pedidos.findIndex(p => p.id === pedidoId);
  
  if (index === -1) {
    throw new ApiError('Pedido no encontrado', 404);
  }

  pedidos[index].estado = nuevoEstado;
  pedidos[index].fechaActualizacion = new Date().toISOString();
  
  localStorage.setItem('pedidos', JSON.stringify(pedidos));
  return pedidos[index];
}

// === FUNCIONES DE COMPATIBILIDAD ===
// Para mantener compatibilidad con el código existente
export function obtener(key, defecto) {
  return getFromLocalStorage(key, defecto);
}

export function guardar(key, valor) {
  localStorage.setItem(key, JSON.stringify(valor));
}

export function guardarPedidos(pedidos) {
  guardar('pedidos', pedidos);
}

// Exportar la configuración para debug
export { API_CONFIG };