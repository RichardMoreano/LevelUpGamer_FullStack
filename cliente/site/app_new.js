/**
 * Level-Up Gamer - Frontend JavaScript Principal
 * Versión con integración Backend Spring Boot
 */

import { productosBase, regiones, categorias } from "./datos.js";
import * as apiService from "../admin-react/src/services/apiService.js";

// =============== CONFIGURACIÓN GLOBAL ===============
window.levelUpGamer = {
  usuario: null,
  productos: [],
  carrito: [],
  inicializado: false
};

// =============== INICIALIZACIÓN ===============
async function inicializarApp() {
  if (window.levelUpGamer.inicializado) return;
  
  console.log('🎮 Inicializando Level-Up Gamer...');
  
  try {
    // Cargar datos iniciales
    await Promise.all([
      cargarUsuarioActual(),
      cargarProductos(),
      inicializarCarrito()
    ]);
    
    // Inicializar interfaz
    actualizarNavegacion();
    popularCategorias();
    
    window.levelUpGamer.inicializado = true;
    console.log('✅ Level-Up Gamer inicializado correctamente');
    
  } catch (error) {
    console.error('❌ Error inicializando aplicación:', error);
    // Usar fallback con localStorage
    inicializarFallbackLocal();
  }
}

async function cargarUsuarioActual() {
  try {
    const usuario = await apiService.usuarioActual();
    window.levelUpGamer.usuario = usuario;
    return usuario;
  } catch (error) {
    console.warn('Usuario no autenticado o error de API');
    return null;
  }
}

async function cargarProductos() {
  try {
    const productos = await apiService.obtenerProductos();
    window.levelUpGamer.productos = productos || [];
    return productos;
  } catch (error) {
    console.warn('Error cargando productos, usando datos base');
    window.levelUpGamer.productos = productosBase || [];
    return window.levelUpGamer.productos;
  }
}

function inicializarCarrito() {
  const carrito = apiService.obtenerCarrito();
  window.levelUpGamer.carrito = carrito || [];
  actualizarContadorCarrito();
}

// =============== FUNCIONES DE USUARIO ===============
async function usuarioActual() {
  if (!window.levelUpGamer.usuario) {
    await cargarUsuarioActual();
  }
  return window.levelUpGamer.usuario;
}

async function esAdmin() {
  const usuario = await usuarioActual();
  return !!(usuario && usuario.tipoUsuario === "admin");
}

async function esVendedor() {
  const usuario = await usuarioActual();
  return !!(usuario && usuario.tipoUsuario === "vendedor");
}

// =============== AUTENTICACIÓN ===============
async function iniciarSesion(correo, password) {
  try {
    const response = await apiService.login(correo, password);
    
    if (response && response.accessToken) {
      window.levelUpGamer.usuario = response.usuario;
      actualizarNavegacion();
      return { success: true, usuario: response.usuario };
    }
    
    throw new Error('Credenciales incorrectas');
    
  } catch (error) {
    console.error('Error en login:', error);
    return { success: false, error: error.message };
  }
}

async function registrarUsuario(userData) {
  try {
    const response = await apiService.register(userData);
    
    if (response) {
      return { success: true, usuario: response };
    }
    
    throw new Error('Error en el registro');
    
  } catch (error) {
    console.error('Error en registro:', error);
    return { success: false, error: error.message };
  }
}

function cerrarSesion() {
  apiService.logout();
  window.levelUpGamer.usuario = null;
  window.levelUpGamer.carrito = [];
  actualizarNavegacion();
  
  // Redirigir según contexto
  if (location.pathname.includes("/admin/")) {
    window.location.href = "../cliente/index.html";
  } else {
    window.location.href = "index.html";
  }
}

// =============== PRODUCTOS ===============
function obtenerProductos() {
  return window.levelUpGamer.productos || [];
}

async function obtenerProductoPorCodigo(codigo) {
  try {
    return await apiService.obtenerProductoPorCodigo(codigo);
  } catch (error) {
    // Fallback local
    const productos = obtenerProductos();
    return productos.find(p => p.codigo === codigo) || null;
  }
}

function precioConDescuento(precio) {
  const usuario = window.levelUpGamer.usuario;
  const esDuoc = !!(usuario && usuario.correo?.toLowerCase().endsWith("@duoc.cl"));
  return esDuoc ? Math.round(precio * 0.8) : precio; // 20% off
}

// =============== CARRITO ===============
function obtenerCarrito() {
  return window.levelUpGamer.carrito || [];
}

function agregarAlCarrito(codigo, cantidad = 1) {
  const productos = obtenerProductos();
  const producto = productos.find(p => p.codigo === codigo);
  
  if (!producto) {
    console.error('Producto no encontrado:', codigo);
    return;
  }

  const stock = Number(producto.stock) || 0;
  if (stock <= 0) {
    mostrarMensaje("Sin stock disponible", "warning");
    return;
  }

  const carrito = obtenerCarrito();
  const itemExistente = carrito.find(item => item.codigo === codigo);
  const cantidadActual = itemExistente ? itemExistente.cantidad : 0;
  
  if (cantidadActual + cantidad > stock) {
    mostrarMensaje(`Solo hay ${stock - cantidadActual} unidades disponibles`, "warning");
    return;
  }

  if (itemExistente) {
    itemExistente.cantidad += cantidad;
  } else {
    carrito.push({ codigo, cantidad, precio: producto.precio });
  }

  window.levelUpGamer.carrito = carrito;
  apiService.guardarCarrito(carrito);
  actualizarContadorCarrito();
  renderCarrito();
  
  mostrarMensaje("Producto agregado al carrito", "success");
}

function quitarDelCarrito(codigo) {
  const carrito = obtenerCarrito().filter(item => item.codigo !== codigo);
  window.levelUpGamer.carrito = carrito;
  apiService.guardarCarrito(carrito);
  actualizarContadorCarrito();
  renderCarrito();
}

function cambiarCantidad(codigo, nuevaCantidad) {
  const cantidad = Math.max(1, parseInt(nuevaCantidad) || 1);
  const carrito = obtenerCarrito();
  const item = carrito.find(item => item.codigo === codigo);
  
  if (item) {
    const productos = obtenerProductos();
    const producto = productos.find(p => p.codigo === codigo);
    const stock = Number(producto?.stock) || 0;
    
    if (cantidad > stock) {
      mostrarMensaje(`Solo hay ${stock} unidades disponibles`, "warning");
      item.cantidad = Math.min(item.cantidad, stock);
    } else {
      item.cantidad = cantidad;
    }
    
    window.levelUpGamer.carrito = carrito;
    apiService.guardarCarrito(carrito);
    actualizarContadorCarrito();
    renderCarrito();
  }
}

function limpiarCarrito() {
  window.levelUpGamer.carrito = [];
  apiService.limpiarCarrito();
  actualizarContadorCarrito();
  renderCarrito();
}

// =============== INTERFAZ ===============
async function actualizarNavegacion() {
  const usuario = await usuarioActual();
  
  // Enlaces de navegación
  const elementosNav = {
    linkRegistro: document.getElementById("linkRegistro"),
    linkLogin: document.getElementById("linkLogin"),
    linkSalir: document.getElementById("linkSalir"),
    linkAdmin: document.getElementById("linkAdmin"),
    linkVendedor: document.getElementById("linkVendedor"),
    linkMiCuenta: document.getElementById("linkMiCuenta"),
    btnPerfilDesk: document.getElementById("btnPerfilDesk")
  };

  Object.values(elementosNav).forEach(elemento => {
    if (elemento) {
      elemento.classList.remove("oculto");
    }
  });

  if (usuario) {
    // Usuario logueado
    if (elementosNav.linkRegistro) elementosNav.linkRegistro.classList.add("oculto");
    if (elementosNav.linkLogin) elementosNav.linkLogin.classList.add("oculto");
    if (elementosNav.linkMiCuenta) elementosNav.linkMiCuenta.classList.remove("oculto");
    if (elementosNav.btnPerfilDesk) elementosNav.btnPerfilDesk.classList.remove("oculto");
    
    // Mostrar enlaces según rol
    const esAdminUsuario = usuario.tipoUsuario === "admin";
    const esVendedorUsuario = usuario.tipoUsuario === "vendedor";
    
    if (elementosNav.linkAdmin) {
      elementosNav.linkAdmin.classList.toggle("oculto", !esAdminUsuario);
    }
    if (elementosNav.linkVendedor) {
      elementosNav.linkVendedor.classList.toggle("oculto", !(esAdminUsuario || esVendedorUsuario));
    }
  } else {
    // Usuario no logueado
    if (elementosNav.linkSalir) elementosNav.linkSalir.classList.add("oculto");
    if (elementosNav.linkAdmin) elementosNav.linkAdmin.classList.add("oculto");
    if (elementosNav.linkVendedor) elementosNav.linkVendedor.classList.add("oculto");
    if (elementosNav.linkMiCuenta) elementosNav.linkMiCuenta.classList.add("oculto");
    if (elementosNav.btnPerfilDesk) elementosNav.btnPerfilDesk.classList.add("oculto");
  }

  actualizarContadorCarrito();
}

function actualizarContadorCarrito() {
  const total = obtenerCarrito().reduce((sum, item) => sum + (item.cantidad || 0), 0);
  const contador = document.getElementById("contadorCarrito");
  if (contador) {
    contador.textContent = total;
    contador.style.display = total > 0 ? "inline" : "none";
  }
}

// =============== PRODUCTOS - INTERFAZ ===============
function popularCategorias() {
  const selectores = [
    document.getElementById("filtroCategoria"),
    document.getElementById("categoriaProducto")
  ];

  const categoriasDisponibles = Array.from(new Set(
    obtenerProductos().map(p => p.categoria).filter(Boolean)
  )).sort();

  selectores.forEach(selector => {
    if (selector) {
      if (selector.id === "filtroCategoria") {
        selector.innerHTML = '<option value="">Todas las categorías</option>' +
          categoriasDisponibles.map(c => `<option value="${c}">${c}</option>`).join("");
      } else {
        selector.innerHTML = categoriasDisponibles.map(c => `<option value="${c}">${c}</option>`).join("");
      }
    }
  });
}

function renderProductos() {
  const grid = document.getElementById("gridProductos");
  if (!grid) return;

  const texto = norm(document.getElementById("buscador")?.value || "");
  const categoria = document.getElementById("filtroCategoria")?.value || "";

  let productos = obtenerProductos().filter(producto => {
    const cumpleCategoria = !categoria || producto.categoria === categoria;
    const cumpleTexto = !texto || 
      norm(producto.nombre).includes(texto) || 
      norm(producto.codigo).includes(texto);
    
    return cumpleCategoria && cumpleTexto;
  });

  if (productos.length === 0) {
    grid.innerHTML = `
      <div class="mensaje-vacio">
        <h3>No se encontraron productos</h3>
        <p>Intenta con otros términos de búsqueda o categoría.</p>
      </div>`;
    return;
  }

  grid.innerHTML = productos.map(producto => `
    <article class="tarjeta-producto" data-codigo="${producto.codigo}">
      <div class="imagen-producto">
        <img src="${producto.imagen || 'img/placeholder.jpg'}" 
             alt="${producto.nombre}" 
             onerror="this.src='img/placeholder.jpg'">
      </div>
      <div class="contenido-producto">
        <h3>${producto.nombre}</h3>
        <p class="categoria">${producto.categoria}</p>
        <p class="precio">${formatoPrecio(precioConDescuento(producto.precio))}</p>
        <div class="stock-info">
          ${Number(producto.stock) > 0 
            ? `<span class="stock-disponible">Stock: ${producto.stock}</span>`
            : `<span class="sin-stock">Sin stock</span>`
          }
        </div>
        <div class="acciones-producto">
          <a href="producto.html?codigo=${encodeURIComponent(producto.codigo)}" 
             class="btn btn-secundario">Ver detalles</a>
          <button onclick="agregarAlCarrito('${producto.codigo}')" 
                  class="btn btn-primario"
                  ${Number(producto.stock) <= 0 ? 'disabled' : ''}>
            ${Number(producto.stock) > 0 ? 'Agregar al carrito' : 'Sin stock'}
          </button>
        </div>
      </div>
    </article>
  `).join("");

  // Agregar event listeners
  grid.querySelectorAll('[onclick]').forEach(button => {
    const onclick = button.getAttribute('onclick');
    if (onclick.includes('agregarAlCarrito')) {
      button.addEventListener('click', (e) => {
        e.preventDefault();
        const codigo = onclick.match(/'([^']+)'/)[1];
        agregarAlCarrito(codigo);
      });
      button.removeAttribute('onclick');
    }
  });
}

function renderCarrito() {
  const contenedor = document.getElementById("listaCarrito");
  if (!contenedor) return;

  const carrito = obtenerCarrito();
  const productos = obtenerProductos();

  if (carrito.length === 0) {
    contenedor.innerHTML = `
      <div class="carrito-vacio">
        <h3>Tu carrito está vacío</h3>
        <p>¡Agrega algunos productos geniales!</p>
        <a href="productos.html" class="btn btn-primario">Ver productos</a>
      </div>`;
    return;
  }

  let total = 0;
  const items = carrito.map(item => {
    const producto = productos.find(p => p.codigo === item.codigo);
    if (!producto) return '';

    const precio = precioConDescuento(producto.precio);
    const subtotal = precio * item.cantidad;
    total += subtotal;

    return `
      <div class="item-carrito" data-codigo="${item.codigo}">
        <div class="info-producto">
          <img src="${producto.imagen || 'img/placeholder.jpg'}" 
               alt="${producto.nombre}" 
               class="imagen-mini">
          <div>
            <h4>${producto.nombre}</h4>
            <small>${producto.codigo}</small>
          </div>
        </div>
        <div class="precio-unitario">${formatoPrecio(precio)}</div>
        <div class="cantidad-control">
          <input type="number" 
                 min="1" 
                 max="${producto.stock || 1}"
                 value="${item.cantidad}" 
                 onchange="cambiarCantidad('${item.codigo}', this.value)">
        </div>
        <div class="subtotal">${formatoPrecio(subtotal)}</div>
        <div class="acciones">
          <button onclick="quitarDelCarrito('${item.codigo}')" 
                  class="btn btn-peligro btn-pequeno">
            Quitar
          </button>
        </div>
      </div>`;
  }).join("");

  contenedor.innerHTML = `
    <div class="carrito-contenido">
      ${items}
      <div class="carrito-total">
        <div class="total-linea">
          <strong>Total: ${formatoPrecio(total)}</strong>
        </div>
        <div class="carrito-acciones">
          <button onclick="limpiarCarrito()" class="btn btn-secundario">
            Vaciar carrito
          </button>
          <button onclick="procesarPago()" class="btn btn-primario">
            Proceder al pago
          </button>
        </div>
      </div>
    </div>`;

  // Event listeners para inputs de cantidad
  contenedor.querySelectorAll('input[type="number"]').forEach(input => {
    input.addEventListener('change', (e) => {
      const codigo = e.target.closest('.item-carrito').dataset.codigo;
      cambiarCantidad(codigo, e.target.value);
    });
  });

  // Event listeners para botones
  contenedor.querySelectorAll('[onclick]').forEach(button => {
    const onclick = button.getAttribute('onclick');
    button.addEventListener('click', (e) => {
      e.preventDefault();
      if (onclick.includes('quitarDelCarrito')) {
        const codigo = onclick.match(/'([^']+)'/)[1];
        quitarDelCarrito(codigo);
      } else if (onclick === 'limpiarCarrito()') {
        limpiarCarrito();
      } else if (onclick === 'procesarPago()') {
        procesarPago();
      }
    });
    button.removeAttribute('onclick');
  });
}

// =============== PAGO ===============
async function procesarPago() {
  const usuario = await usuarioActual();
  
  if (!usuario) {
    mostrarMensaje("Debes iniciar sesión para proceder al pago", "warning");
    window.location.href = "login.html";
    return;
  }

  const carrito = obtenerCarrito();
  if (carrito.length === 0) {
    mostrarMensaje("Tu carrito está vacío", "warning");
    return;
  }

  try {
    const pedidoData = {
      items: carrito,
      usuario: usuario,
      total: calcularTotal()
    };

    const response = await apiService.crearPedido(pedidoData);
    
    if (response) {
      limpiarCarrito();
      mostrarMensaje("¡Pedido creado exitosamente!", "success");
      
      // Redirigir a mis compras
      setTimeout(() => {
        window.location.href = "misCompras.html";
      }, 2000);
    }
    
  } catch (error) {
    console.error('Error procesando pago:', error);
    mostrarMensaje("Error procesando el pago. Intenta nuevamente.", "error");
  }
}

function calcularTotal() {
  const carrito = obtenerCarrito();
  const productos = obtenerProductos();
  
  return carrito.reduce((total, item) => {
    const producto = productos.find(p => p.codigo === item.codigo);
    if (producto) {
      return total + (precioConDescuento(producto.precio) * item.cantidad);
    }
    return total;
  }, 0);
}

// =============== UTILIDADES ===============
function formatoPrecio(precio) {
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
    maximumFractionDigits: 0
  }).format(precio);
}

function norm(texto) {
  return (texto || '')
    .toString()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim();
}

function mostrarMensaje(mensaje, tipo = "info") {
  // Crear o reutilizar contenedor de mensajes
  let contenedor = document.getElementById("mensajes-sistema");
  if (!contenedor) {
    contenedor = document.createElement("div");
    contenedor.id = "mensajes-sistema";
    contenedor.className = "mensajes-sistema";
    document.body.appendChild(contenedor);
  }

  const mensajeElement = document.createElement("div");
  mensajeElement.className = `mensaje mensaje-${tipo}`;
  mensajeElement.innerHTML = `
    <span>${mensaje}</span>
    <button onclick="this.parentElement.remove()" aria-label="Cerrar">×</button>
  `;

  contenedor.appendChild(mensajeElement);

  // Auto-remover después de 5 segundos
  setTimeout(() => {
    if (mensajeElement.parentElement) {
      mensajeElement.remove();
    }
  }, 5000);
}

// =============== FALLBACK LOCAL ===============
function inicializarFallbackLocal() {
  console.warn('🔄 Usando modo fallback con localStorage');
  
  // Inicializar datos base si no existen
  if (!localStorage.getItem("productos") && window.productosBase) {
    localStorage.setItem("productos", JSON.stringify(window.productosBase));
  }
  
  ['usuarios', 'carrito', 'pedidos', 'resenas'].forEach(key => {
    if (!localStorage.getItem(key)) {
      localStorage.setItem(key, JSON.stringify(key === 'resenas' ? {} : []));
    }
  });

  // Cargar datos locales
  window.levelUpGamer.productos = JSON.parse(localStorage.getItem("productos") || "[]");
  window.levelUpGamer.carrito = JSON.parse(localStorage.getItem("carrito") || "[]");
  
  // Verificar sesión local
  const sesion = JSON.parse(localStorage.getItem("sesion") || "null");
  if (sesion) {
    const usuarios = JSON.parse(localStorage.getItem("usuarios") || "[]");
    window.levelUpGamer.usuario = usuarios.find(u => 
      u.correo?.toLowerCase() === sesion.correo?.toLowerCase()
    );
  }

  window.levelUpGamer.inicializado = true;
  actualizarNavegacion();
}

// =============== EXPOSICIÓN GLOBAL ===============
// Exponer funciones necesarias globalmente
window.inicializarApp = inicializarApp;
window.usuarioActual = usuarioActual;
window.esAdmin = esAdmin;
window.esVendedor = esVendedor;
window.iniciarSesion = iniciarSesion;
window.registrarUsuario = registrarUsuario;
window.cerrarSesion = cerrarSesion;
window.agregarAlCarrito = agregarAlCarrito;
window.quitarDelCarrito = quitarDelCarrito;
window.cambiarCantidad = cambiarCantidad;
window.limpiarCarrito = limpiarCarrito;
window.procesarPago = procesarPago;
window.renderProductos = renderProductos;
window.renderCarrito = renderCarrito;
window.actualizarNavegacion = actualizarNavegacion;

// =============== INICIALIZACIÓN AUTOMÁTICA ===============
document.addEventListener('DOMContentLoaded', inicializarApp);

// Debug para desarrollo
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
  window.levelUpGamerDebug = {
    app: window.levelUpGamer,
    api: apiService,
    reiniciar: inicializarApp
  };
  console.log('🔧 Modo debug activado. Usa window.levelUpGamerDebug para inspeccionar.');
}