// =============== CONFIGURACIÓN ===============
const API_BASE_URL = 'https://levelupgamer-fullstack-production.up.railway.app/api';

// =============== UTILIDADES ===============
function obtener(key, defecto) {
  try { 
    return JSON.parse(localStorage.getItem(key)) ?? defecto; 
  } catch { 
    return defecto; 
  }
}

function guardar(key, valor) {
  localStorage.setItem(key, JSON.stringify(valor));
}

function usuarioActual() { 
  return obtener("sesionActual", null); 
}

function formatoPrecio(precio) {
  return new Intl.NumberFormat('es-CL', { 
    style: 'currency', 
    currency: 'CLP' 
  }).format(precio);
}

function norm(texto) {
  return texto.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}

// =============== API FUNCTIONS ===============
async function cargarProductosDesdeAPI() {
  try {
    console.log('Cargando productos desde API...');
    const response = await fetch(`${API_BASE_URL}/productos/publicos`);
    if (response.ok) {
      const productos = await response.json();
      console.log('Productos cargados desde API:', productos);
      guardar("productos", productos);
      return productos;
    } else {
      console.warn('Error al cargar productos desde API, usando datos locales');
      return obtener("productos", window.productosBase || []);
    }
  } catch (error) {
    console.warn('Error de conexión con API, usando datos locales:', error);
    return obtener("productos", window.productosBase || []);
  }
}

async function registrarUsuario(userData) {
  try {
    const response = await fetch(`${API_BASE_URL}/usuarios/registro`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData)
    });
    
    if (response.ok) {
      const result = await response.json();
      return { success: true, data: result };
    } else {
      const error = await response.text();
      return { success: false, error };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
}

async function loginUsuario(credentials) {
  try {
    const response = await fetch(`${API_BASE_URL}/usuarios/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials)
    });
    
    if (response.ok) {
      const result = await response.json();
      if (result.token) {
        guardar("sesionActual", result.usuario);
        guardar("token", result.token);
      }
      return { success: true, data: result };
    } else {
      const error = await response.text();
      return { success: false, error };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
}

// =============== CARRITO ===============
function obtenerCarrito() {
  return obtener("carrito", []);
}

function guardarCarrito(carrito) {
  guardar("carrito", carrito);
}

function agregarAlCarrito(codigo) {
  console.log("Agregando producto al carrito:", codigo);
  const productos = obtener("productos", []);
  const producto = productos.find(p => p.codigo === codigo);
  if (!producto) {
    console.error("Producto no encontrado:", codigo);
    alert("Producto no encontrado");
    return;
  }

  const carrito = obtenerCarrito();
  const item = carrito.find(i => i.codigo === codigo);
  
  if (item) {
    item.cantidad++;
  } else {
    carrito.push({ ...producto, cantidad: 1 });
  }
  
  guardarCarrito(carrito);
  actualizarContadorCarrito();
  alert(`${producto.nombre} agregado al carrito`);
}

function actualizarContadorCarrito() {
  const carrito = obtenerCarrito();
  const total = carrito.reduce((sum, item) => sum + item.cantidad, 0);
  const contador = document.getElementById("contadorCarrito");
  if (contador) {
    contador.textContent = total;
  }
}

// =============== PRODUCTOS DESTACADOS ===============
async function renderDestacados() {
  const cont = document.getElementById("gridDestacados");
  if (!cont) return;

  cont.innerHTML = '<div style="text-align: center; padding: 20px;">Cargando productos...</div>';
  
  try {
    const productos = await cargarProductosDesdeAPI();
    const destacados = productos.slice(0, 6);
    
    cont.innerHTML = destacados.map(p => `
      <article class="tarjeta tarjeta-producto">
        <img src="${p.imagen}" alt="${p.nombre}" onerror="this.src='../img/placeholder.jpg'">
        <div class="contenido">
          <h3>${p.nombre}</h3>
          <p class="precio">${formatoPrecio(p.precio)}</p>
          <div class="acciones">
            <a class="btn secundario" href="producto.html?codigo=${encodeURIComponent(p.codigo)}">Ver</a>
            <button class="btn primario" data-agregar="${p.codigo}">Añadir</button>
          </div>
        </div>
      </article>
    `).join("");

    // Agregar event listeners
    cont.addEventListener("click", (e) => {
      const btn = e.target.closest("[data-agregar]");
      if (btn) {
        agregarAlCarrito(btn.getAttribute("data-agregar"));
      }
    });
  } catch (error) {
    console.error('Error al cargar productos destacados:', error);
    cont.innerHTML = '<div style="text-align: center; padding: 20px;">Error al cargar productos</div>';
  }
}

// =============== PRODUCTOS (página productos) ===============
async function renderProductos() {
  const grid = document.getElementById("gridProductos");
  if (!grid) return;

  const texto = norm(document.getElementById("buscador")?.value || "");
  const cat = document.getElementById("filtroCategoria")?.value || "";

  grid.innerHTML = '<div style="text-align: center; padding: 20px;">Cargando productos...</div>';

  try {
    const productos = await cargarProductosDesdeAPI();
    const filtrados = productos.filter(p => {
      const okCat = !cat || p.categoria === cat;
      const okTxt = !texto || norm(p.nombre).includes(texto) || norm(p.codigo).includes(texto);
      return okCat && okTxt;
    });

    if (filtrados.length === 0) {
      grid.innerHTML = `
        <div class="tarjeta" style="padding:16px; text-align:center;">
          <p class="info">Sin resultados. Prueba otra búsqueda o categoría.</p>
        </div>`;
      return;
    }

    grid.innerHTML = filtrados.map(p => `
      <article class="tarjeta tarjeta-producto">
        <img src="${p.imagen}" alt="${p.nombre}" onerror="this.src='../img/placeholder.jpg'">
        <div class="contenido">
          <h3>${p.nombre}</h3>
          <p class="precio">${formatoPrecio(p.precio)}</p>
          <div class="acciones">
            <a class="btn secundario" href="producto.html?codigo=${encodeURIComponent(p.codigo)}">Ver</a>
            <button class="btn primario" data-add="${p.codigo}">Añadir</button>
          </div>
        </div>
      </article>
    `).join("");

    // Agregar event listeners
    grid.addEventListener("click", (e) => {
      const btn = e.target.closest("[data-add]");
      if (btn) {
        agregarAlCarrito(btn.getAttribute("data-add"));
      }
    });
  } catch (error) {
    console.error('Error al cargar productos:', error);
    grid.innerHTML = '<div style="text-align: center; padding: 20px;">Error al cargar productos</div>';
  }
}

// =============== CATEGORÍAS ===============
function popularCategorias() {
  const select = document.getElementById("filtroCategoria");
  if (!select || !window.categorias) return;

  select.innerHTML = '<option value="">Todas las categorías</option>';
  window.categorias.forEach(cat => {
    select.innerHTML += `<option value="${cat}">${cat}</option>`;
  });
}

// =============== REGIONES Y COMUNAS ===============
function inicializarRegiones() {
  const selRegion = document.getElementById("region");
  const selComuna = document.getElementById("comuna");
  
  if (!selRegion || !selComuna || !window.regiones) return;

  // Llenar regiones
  selRegion.innerHTML = '<option value="">Seleccionar región</option>';
  window.regiones.forEach(region => {
    selRegion.innerHTML += `<option value="${region.nombre}">${region.nombre}</option>`;
  });

  // Manejar cambio de región
  selRegion.addEventListener("change", () => {
    const regionSeleccionada = window.regiones.find(r => r.nombre === selRegion.value);
    
    selComuna.innerHTML = '<option value="">Seleccionar comuna</option>';
    if (regionSeleccionada) {
      regionSeleccionada.comunas.forEach(comuna => {
        selComuna.innerHTML += `<option value="${comuna}">${comuna}</option>`;
      });
    }
  });
}

// =============== REGISTRO ===============
function inicializarRegistro() {
  console.log("Inicializando formulario de registro...");
  const form = document.getElementById("formRegistro");
  if (!form) {
    console.warn("Formulario de registro no encontrado");
    return;
  }

  console.log("Formulario de registro encontrado, configurando eventos...");
  inicializarRegiones();

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    console.log("Formulario de registro enviado");
    
    // Obtener valores directamente por ID
    const run = document.getElementById("run").value.trim();
    const nombres = document.getElementById("nombres").value.trim();
    const apellidos = document.getElementById("apellidos").value.trim();
    const correo = document.getElementById("correo").value.trim();
    const fechaNacimiento = document.getElementById("fechaNacimiento").value;
    const tipoUsuario = document.getElementById("tipoUsuario").value;
    const region = document.getElementById("region").value;
    const comuna = document.getElementById("comuna").value;
    const direccion = document.getElementById("direccion").value.trim();
    const password = document.getElementById("password").value;
    const password2 = document.getElementById("password2").value;

    // Validaciones básicas
    if (!run || !nombres || !apellidos || !correo || !password) {
      alert("Por favor completa todos los campos obligatorios");
      return;
    }

    if (password !== password2) {
      alert("Las contraseñas no coinciden");
      return;
    }

    const userData = {
      run,
      nombres,
      apellidos,
      correo,
      fechaNacimiento,
      tipoUsuario: tipoUsuario || "cliente",
      region,
      comuna,
      direccion,
      password,
      descuentoDuoc: correo.toLowerCase().endsWith("@duoc.cl"),
      puntosLevelUp: 0,
      codigoReferido: "REF" + Math.random().toString(36).substring(2,8).toUpperCase()
    };

    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = "Registrando...";
    submitBtn.disabled = true;

    try {
      const result = await registrarUsuario(userData);
      
      if (result.success) {
        alert("Registro exitoso. Ahora puedes iniciar sesión.");
        window.location.href = "login.html";
      } else {
        alert(`Error en el registro: ${result.error}`);
      }
    } catch (error) {
      alert(`Error: ${error.message}`);
    } finally {
      submitBtn.textContent = originalText;
      submitBtn.disabled = false;
    }
  });
}

// =============== LOGIN ===============
function inicializarLogin() {
  console.log("Inicializando formulario de login...");
  const form = document.getElementById("formLogin");
  if (!form) {
    console.warn("Formulario de login no encontrado");
    return;
  }

  console.log("Formulario de login encontrado, configurando eventos...");
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    console.log("Formulario de login enviado");
    
    const correo = document.getElementById("correoLogin").value.trim();
    const password = document.getElementById("passwordLogin").value;

    if (!correo || !password) {
      alert("Por favor ingresa email y contraseña");
      return;
    }

    const credentials = {
      correo,
      password
    };

    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = "Iniciando sesión...";
    submitBtn.disabled = true;

    try {
      const result = await loginUsuario(credentials);
      
      if (result.success) {
        alert("Inicio de sesión exitoso");
        actualizarNavegacion();
        window.location.href = "index.html";
      } else {
        alert(`Error: ${result.error}`);
      }
    } catch (error) {
      alert(`Error: ${error.message}`);
    } finally {
      submitBtn.textContent = originalText;
      submitBtn.disabled = false;
    }
  });
}

// =============== NAVEGACIÓN ===============
function actualizarNavegacion() {
  const usuario = usuarioActual();
  
  const linkRegistro = document.getElementById("linkRegistro");
  const linkLogin = document.getElementById("linkLogin");
  const linkAdmin = document.getElementById("linkAdmin");
  const linkSalir = document.getElementById("linkSalir");

  if (usuario) {
    // Usuario logueado
    if (linkRegistro) linkRegistro.style.display = "none";
    if (linkLogin) linkLogin.style.display = "none";
    
    if (usuario.tipoUsuario === "admin" && linkAdmin) {
      linkAdmin.style.display = "inline";
      linkAdmin.href = "/admin";
    }
    
    if (linkSalir) linkSalir.style.display = "inline";
  } else {
    // Usuario no logueado
    if (linkRegistro) linkRegistro.style.display = "inline";
    if (linkLogin) linkLogin.style.display = "inline";
    if (linkAdmin) linkAdmin.style.display = "none";
    if (linkSalir) linkSalir.style.display = "none";
  }
}

function cerrarSesion() {
  localStorage.removeItem("sesionActual");
  localStorage.removeItem("token");
  actualizarNavegacion();
  window.location.href = "index.html";
}

// =============== FILTROS ===============
function configurarFiltros() {
  const buscador = document.getElementById("buscador");
  const filtroCategoria = document.getElementById("filtroCategoria");

  if (buscador) {
    buscador.addEventListener("input", renderProductos);
  }

  if (filtroCategoria) {
    filtroCategoria.addEventListener("change", renderProductos);
  }
}

// =============== MENÚ MÓVIL ===============
function configurarMenuMovil() {
  const btnMenu = document.getElementById("btnMenu");
  const menuLateral = document.getElementById("menuLateral");
  
  if (btnMenu && menuLateral) {
    btnMenu.addEventListener("click", () => {
      const isOpen = menuLateral.classList.contains("activo");
      menuLateral.classList.toggle("activo", !isOpen);
      btnMenu.setAttribute("aria-expanded", !isOpen);
    });
  }
}

// =============== INICIALIZACIÓN ===============
function inicializarDatos() {
  // Inicializar datos locales si no existen
  if (!localStorage.getItem("productos") && window.productosBase) {
    guardar("productos", window.productosBase);
  }
  if (!localStorage.getItem("carrito")) guardar("carrito", []);
}

// =============== INICIALIZACIÓN PRINCIPAL ===============
document.addEventListener("DOMContentLoaded", async () => {
  console.log("Inicializando aplicación...");
  
  // Verificar que los datos estén cargados
  if (!window.productosBase || !window.regiones || !window.categorias) {
    console.warn("Datos no cargados correctamente");
  }

  // Inicializar datos básicos
  inicializarDatos();
  
  // Actualizar navegación
  actualizarNavegacion();
  
  // Configurar menú móvil
  configurarMenuMovil();
  
  // Actualizar contador del carrito
  actualizarContadorCarrito();

  // Configurar salir de sesión
  const linkSalir = document.getElementById("linkSalir");
  if (linkSalir) {
    linkSalir.addEventListener("click", (e) => {
      e.preventDefault();
      cerrarSesion();
    });
  }

  // Inicializar según la página actual
  const path = window.location.pathname;
  
  if (path.includes("index.html") || path.endsWith("/") || path.endsWith("/cliente/")) {
    // Página principal - cargar productos destacados
    await renderDestacados();
  }
  
  if (path.includes("productos.html")) {
    // Página de productos
    popularCategorias();
    configurarFiltros();
    await renderProductos();
  }
  
  if (path.includes("registro.html")) {
    // Página de registro
    inicializarRegistro();
  }
  
  if (path.includes("login.html")) {
    // Página de login
    inicializarLogin();
  }

  console.log("Aplicación inicializada correctamente");
});