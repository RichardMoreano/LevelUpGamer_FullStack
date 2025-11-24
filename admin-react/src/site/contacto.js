// src/site/contacto.js
import { solicitudesAPI } from "../services/apiService.js";

// Valida que los elementos existan antes de usarlos.
function initContacto() {
  const formulario = document.querySelector(".form-contacto");
  const nombre = document.getElementById("nombre");
  const correo = document.getElementById("correo");
  const contenido = document.getElementById("contenido");
  const botonEnviar = document.getElementById("btnEnviar");

  function validarFormulario() {
    if (!nombre || !correo || !contenido || !botonEnviar) return;
    const ok =
      nombre.value.trim() !== "" &&
      correo.value.trim() !== "" &&
      contenido.value.trim() !== "";
    botonEnviar.disabled = !ok;
  }

  function validarUsuario(vCorreo) {
    const eRegular = /^[a-zA-Z0-9._%+-]+@duoc\.cl$/;
    if (vCorreo === "") {
      alert("El correo no puede estar vacio");
      return false;
    } else if (!eRegular.test(vCorreo)) {
      alert("El correo debe ser del tipo usuario@duoc.cl");
      return false;
    } else if (vCorreo.length > 20) {
      alert("Importante: el correo no puede superar los 20 caracteres.");
      return false;
    } else if (/\s/.test(vCorreo)) {
      alert("El correo no puede contener espacios.");
      return false;
    }
    return true;
  }

  function validarNombre(vNombre) {
    const eRegularNombre = /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/;
    if (!eRegularNombre.test(vNombre)) {
      alert("El nombre debne contener solo caracteres alfabeticos y espacios.");
      return false;
    }
    return true;
  }

  function validarContenidoSeguro(vContenido) {
    const regex = /[<>]/;
    if (regex.test(vContenido)) {
      alert("El mensaje no puede contener < o >");
      return false;
    }
    return true;
  }

  if (nombre) nombre.addEventListener("input", validarFormulario);
  if (correo) correo.addEventListener("input", validarFormulario);
  if (contenido) contenido.addEventListener("input", validarFormulario);

  if (formulario) {
    formulario.addEventListener("submit", async function (e) {
      e.preventDefault();

      if (!nombre || !correo || !contenido) {
        return;
      }

      if (
        !validarUsuario(correo.value) ||
        !validarNombre(nombre.value) ||
        !validarContenidoSeguro(contenido.value)
      ) {
        return;
      }

      try {
        // 👇 Ahora se envía al backend, no a localStorage
        await solicitudesAPI.createPublic({
          nombre: nombre.value.trim(),
          correo: correo.value.trim(),
          descripcion: contenido.value.trim(),
        });

        alert("¡Mensaje enviado correctamente!");
        formulario.reset();
        validarFormulario();
      } catch (error) {
        console.error("❌ Error enviando solicitud de contacto:", error);
        alert("Ocurrió un error al enviar el mensaje. Intenta más tarde.");
      }
    });
  }

  validarFormulario();
}

// Export para tests (si lo usas)
export { initContacto };

if (typeof window !== "undefined") {
  window.__initContacto = initContacto;
}

// Auto-init
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initContacto, { once: true });
} else {
  initContacto();
}
