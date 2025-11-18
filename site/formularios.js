/**
 * Lógica específica para formularios - Level-Up Gamer
 */

// =============== INICIALIZACIÓN DE FORMULARIOS ===============
document.addEventListener('DOMContentLoaded', () => {
  inicializarFormularioLogin();
  inicializarFormularioRegistro();
});

// =============== FORMULARIO LOGIN ===============
function inicializarFormularioLogin() {
  const formLogin = document.getElementById('formLogin');
  if (!formLogin) return;

  formLogin.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Limpiar errores previos
    limpiarErrores(['errCorreoLogin', 'errPassLogin', 'msgLogin']);
    
    const correo = document.getElementById('correoLogin').value.trim();
    const password = document.getElementById('passwordLogin').value;
    
    // Validaciones básicas
    if (!validarCorreo(correo)) {
      mostrarError('errCorreoLogin', 'Correo inválido');
      return;
    }
    
    if (password.length < 4 || password.length > 10) {
      mostrarError('errPassLogin', 'Contraseña debe tener entre 4 y 10 caracteres');
      return;
    }
    
    // Intentar login
    const botonSubmit = formLogin.querySelector('button[type="submit"]');
    const textoOriginal = botonSubmit.textContent;
    
    try {
      botonSubmit.textContent = 'Ingresando...';
      botonSubmit.disabled = true;
      
      const resultado = await window.iniciarSesion(correo, password);
      
      if (resultado.success) {
        mostrarMensajeExito('msgLogin', `¡Bienvenido/a, ${resultado.usuario.nombres || 'Usuario'}!`);
        
        // Redirigir después de 1 segundo
        setTimeout(() => {
          // Redirigir según el tipo de usuario
          if (resultado.usuario.tipoUsuario === 'admin' || resultado.usuario.tipoUsuario === 'vendedor') {
            window.location.href = '../admin-panel/';
          } else {
            window.location.href = 'productos.html';
          }
        }, 1000);
        
      } else {
        mostrarError('msgLogin', resultado.error || 'Error al iniciar sesión');
      }
      
    } catch (error) {
      console.error('Error en login:', error);
      mostrarError('msgLogin', 'Error de conexión. Intenta nuevamente.');
    } finally {
      botonSubmit.textContent = textoOriginal;
      botonSubmit.disabled = false;
    }
  });
}

// =============== FORMULARIO REGISTRO =============== 
function inicializarFormularioRegistro() {
  const formRegistro = document.getElementById('formRegistro');
  if (!formRegistro) return;

  // Inicializar regiones y comunas
  inicializarRegionesYComunas();

  formRegistro.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Limpiar errores previos
    const errIds = ['errRun', 'errNombres', 'errApellidos', 'errCorreo', 'errFecha', 
                   'errTipo', 'errRegion', 'errComuna', 'errDireccion', 'errPass', 
                   'errPass2', 'msgRegistro'];
    limpiarErrores(errIds);
    
    // Obtener datos del formulario
    const formData = obtenerDatosFormulario();
    
    // Validar datos
    if (!validarFormularioRegistro(formData)) {
      return;
    }
    
    // Intentar registro
    const botonSubmit = formRegistro.querySelector('button[type="submit"]');
    const textoOriginal = botonSubmit.textContent;
    
    try {
      botonSubmit.textContent = 'Registrando...';
      botonSubmit.disabled = true;
      
      const resultado = await window.registrarUsuario(formData);
      
      if (resultado.success) {
        mostrarMensajeExito('msgRegistro', '¡Registro exitoso! Ya puedes iniciar sesión.');
        formRegistro.reset();
        
        // Redirigir al login después de 2 segundos
        setTimeout(() => {
          window.location.href = 'login.html';
        }, 2000);
        
      } else {
        mostrarError('msgRegistro', resultado.error || 'Error en el registro');
      }
      
    } catch (error) {
      console.error('Error en registro:', error);
      mostrarError('msgRegistro', 'Error de conexión. Intenta nuevamente.');
    } finally {
      botonSubmit.textContent = textoOriginal;
      botonSubmit.disabled = false;
    }
  });
}

// =============== REGIONES Y COMUNAS ===============
function inicializarRegionesYComunas() {
  const selectRegion = document.getElementById('region');
  const selectComuna = document.getElementById('comuna');
  
  if (!selectRegion || !selectComuna) return;

  // Datos de regiones (simplificado para el ejemplo)
  const regiones = [
    {
      nombre: "Región Metropolitana",
      comunas: ["Santiago", "Las Condes", "Providencia", "Ñuñoa", "La Florida", "Maipú", "Puente Alto"]
    },
    {
      nombre: "Región de Valparaíso",
      comunas: ["Valparaíso", "Viña del Mar", "Quilpué", "Villa Alemana", "Concón"]
    },
    {
      nombre: "Región del Biobío",
      comunas: ["Concepción", "Talcahuano", "Chillán", "Los Ángeles", "Coronel"]
    }
  ];

  // Poblar select de regiones
  selectRegion.innerHTML = regiones.map(r => 
    `<option value="${r.nombre}">${r.nombre}</option>`
  ).join('');

  // Función para actualizar comunas
  function actualizarComunas() {
    const regionSeleccionada = regiones.find(r => r.nombre === selectRegion.value);
    selectComuna.innerHTML = (regionSeleccionada?.comunas || []).map(c => 
      `<option value="${c}">${c}</option>`
    ).join('');
  }

  // Event listener para cambio de región
  selectRegion.addEventListener('change', actualizarComunas);
  
  // Inicializar comunas
  actualizarComunas();
}

// =============== FUNCIONES DE VALIDACIÓN ===============
function obtenerDatosFormulario() {
  return {
    run: document.getElementById('run')?.value.trim() || '',
    nombres: document.getElementById('nombres')?.value.trim() || '',
    apellidos: document.getElementById('apellidos')?.value.trim() || '',
    correo: document.getElementById('correo')?.value.trim() || '',
    fechaNacimiento: document.getElementById('fechaNacimiento')?.value || '',
    tipoUsuario: document.getElementById('tipoUsuario')?.value || 'cliente',
    region: document.getElementById('region')?.value || '',
    comuna: document.getElementById('comuna')?.value || '',
    direccion: document.getElementById('direccion')?.value.trim() || '',
    password: document.getElementById('password')?.value || '',
    password2: document.getElementById('password2')?.value || '',
    referido: document.getElementById('referido')?.value.trim() || ''
  };
}

function validarFormularioRegistro(data) {
  let esValido = true;

  // Validar RUN
  if (!validarRUN(data.run)) {
    mostrarError('errRun', 'RUN inválido');
    esValido = false;
  }

  // Validar nombres
  if (!data.nombres || data.nombres.length < 2) {
    mostrarError('errNombres', 'Nombres requerido (mín 2 caracteres)');
    esValido = false;
  }

  // Validar apellidos
  if (!data.apellidos || data.apellidos.length < 2) {
    mostrarError('errApellidos', 'Apellidos requerido (mín 2 caracteres)');
    esValido = false;
  }

  // Validar correo
  if (!validarCorreo(data.correo) || data.correo.length > 100) {
    mostrarError('errCorreo', 'Correo inválido o muy largo');
    esValido = false;
  }

  // Validar fecha de nacimiento (mayor de 18 años)
  if (!esMayorDe18(data.fechaNacimiento)) {
    mostrarError('errFecha', 'Debes ser mayor de 18 años');
    esValido = false;
  }

  // Validar región
  if (!data.region) {
    mostrarError('errRegion', 'Selecciona una región');
    esValido = false;
  }

  // Validar comuna
  if (!data.comuna) {
    mostrarError('errComuna', 'Selecciona una comuna');
    esValido = false;
  }

  // Validar dirección
  if (!data.direccion || data.direccion.length < 5 || data.direccion.length > 300) {
    mostrarError('errDireccion', 'Dirección inválida (5-300 caracteres)');
    esValido = false;
  }

  // Validar contraseña
  if (data.password.length < 4 || data.password.length > 10) {
    mostrarError('errPass', 'Contraseña debe tener entre 4 y 10 caracteres');
    esValido = false;
  }

  // Validar confirmación de contraseña
  if (data.password !== data.password2) {
    mostrarError('errPass2', 'Las contraseñas no coinciden');
    esValido = false;
  }

  return esValido;
}

function validarCorreo(correo) {
  const dominiosPermitidos = ['duoc.cl', 'profesor.duoc.cl', 'gmail.com'];
  const regex = /^[\w.+-]+@([\w.-]+)$/i;
  const match = correo.match(regex);
  
  if (!match) return false;
  
  const dominio = match[1].toLowerCase();
  return dominiosPermitidos.some(d => dominio.endsWith(d));
}

function validarRUN(run) {
  const runLimpio = run.toUpperCase().replace(/[.-]/g, '');
  
  if (runLimpio.length < 7 || runLimpio.length > 9) return false;
  
  const cuerpo = runLimpio.slice(0, -1);
  const dv = runLimpio.slice(-1);
  
  let suma = 0;
  let multiplo = 2;
  
  for (let i = cuerpo.length - 1; i >= 0; i--) {
    suma += parseInt(cuerpo[i], 10) * multiplo;
    multiplo = multiplo === 7 ? 2 : multiplo + 1;
  }
  
  const resto = 11 - (suma % 11);
  const dvCalculado = resto === 11 ? '0' : (resto === 10 ? 'K' : String(resto));
  
  return dv === dvCalculado;
}

function esMayorDe18(fechaNacimiento) {
  const fecha = new Date(fechaNacimiento);
  if (isNaN(fecha)) return false;
  
  const hoy = new Date();
  let edad = hoy.getFullYear() - fecha.getFullYear();
  const mes = hoy.getMonth() - fecha.getMonth();
  
  if (mes < 0 || (mes === 0 && hoy.getDate() < fecha.getDate())) {
    edad--;
  }
  
  return edad >= 18;
}

// =============== FUNCIONES DE UI ===============
function limpiarErrores(elementIds) {
  elementIds.forEach(id => {
    const elemento = document.getElementById(id);
    if (elemento) {
      elemento.textContent = '';
      elemento.className = elemento.className.replace(/\b(error|exito)\b/g, '').trim();
    }
  });
}

function mostrarError(elementId, mensaje) {
  const elemento = document.getElementById(elementId);
  if (elemento) {
    elemento.textContent = mensaje;
    elemento.className = 'error';
  }
}

function mostrarMensajeExito(elementId, mensaje) {
  const elemento = document.getElementById(elementId);
  if (elemento) {
    elemento.textContent = mensaje;
    elemento.className = 'exito';
  }
}

// Exponer funciones necesarias globalmente
window.inicializarFormularioLogin = inicializarFormularioLogin;
window.inicializarFormularioRegistro = inicializarFormularioRegistro;