const API = "http://localhost:8080";

// ==================== FUNCIONES DE LOGIN ====================
function login() {
    console.log("=== INICIO LOGIN ===");
    
    let usuario = document.getElementById("usuario").value;
    let password = document.getElementById("password").value;

    if (!usuario || !password) {
        mostrarNotificacion('❌ Por favor completa todos los campos', 'error');
        return;
    }

    fetch(API + "/usuarios/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({nombre: usuario, password: password})
    })
    .then(async response => {
        console.log("Respuesta login status:", response.status);
        if (response.ok) {
            return response.json();
        } else {
            const error = await response.text();
            throw new Error(error);
        }
    })
    .then(data => {
        console.log("Login exitoso:", data);
        if (data && data.id) {
            mostrarNotificacion('✅ Login exitoso! Redirigiendo...', 'success');
            localStorage.setItem('usuarioId', data.id);
            localStorage.setItem('usuarioNombre', data.nombre);
            
            setTimeout(() => {
                window.location.href = "buscar.html";
            }, 1500);
        } else {
            mostrarNotificacion('❌ Usuario o contraseña incorrectos', 'error');
        }
    })
    .catch(err => {
        console.error("Error en login:", err);
        mostrarNotificacion('❌ Error al conectar con el servidor', 'error');
    });
}

// ==================== FUNCIONES DE BÚSQUEDA ====================
function buscarLibro() {
    console.log("=== buscarLibro EJECUTADA ===");
    
    let inputBuscar = document.getElementById("buscar");
    if (!inputBuscar) {
        console.error("ERROR: No se encontró el input con id='buscar'");
        mostrarMensajeEnResultados("Error: No se encuentra el campo de búsqueda", "error");
        return;
    }
    
    let titulo = inputBuscar.value.trim();
    console.log("1. Título a buscar:", titulo);
    
    if (!titulo) {
        mostrarMensajeEnResultados("📝 Por favor ingresa un título", "info");
        return;
    }
    
    // Mostrar indicador de carga
    document.getElementById("resultados").innerHTML = `
        <div class="loading">
            <div class="spinner"></div>
            <p>🔍 Buscando libros...</p>
        </div>
    `;
    
    const url = API + "/libros/buscar/" + encodeURIComponent(titulo);
    console.log("2. URL completa:", url);
    
    fetch(url)
    .then(response => {
        console.log("3. Respuesta recibida. Status:", response.status);
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log("4. Datos recibidos:", data);
        console.log("5. Cantidad de libros encontrados:", data.length);
        
        let resultadosDiv = document.getElementById("resultados");
        if (!resultadosDiv) {
            console.error("ERROR: No se encontró el div con id='resultados'");
            return;
        }
        
        let html = "<h3>📚 Resultados de la búsqueda:</h3>";
        
        if (data.length === 0) {
            html += "<p class='no-results'>No se encontraron libros con ese título</p>";
        } else {
            data.forEach((libro, index) => {
                console.log(`6. Libro ${index + 1}:`, libro.titulo);
                
                // Determinar estado de disponibilidad
                let disponible = libro.cantidadDisponible > 0;
                let estadoClass = disponible ? 'disponible' : 'no-disponible';
                let estadoTexto = disponible ? '✅ Sí' : '❌ No';
                
                html += `
                    <div class="libro-card">
                        <div class="libro-info">
                            <h4>${libro.titulo || 'Sin título'}</h4>
                            <p><strong>Autor:</strong> ${libro.autor || 'No especificado'}</p>
                            <p><strong>Editorial:</strong> ${libro.editorial || 'No especificada'}</p>
                            <p><strong>Disponibilidad:</strong> <span class="${estadoClass}">${estadoTexto}</span></p>
                            <p><strong>Copias disponibles:</strong> ${libro.cantidadDisponible || 0} de ${libro.cantidad || 0}</p>
                        </div>
                        ${libro.cantidadDisponible > 0 ? 
                            `<button class="btn-reservar" onclick="reservarLibro(${libro.id})">
                                📌 Reservar (${libro.cantidadDisponible} disp.)
                            </button>` : 
                            '<button class="btn-reservar" disabled>❌ Agotado</button>'}
                    </div>
                `;
            });
        }
        
        resultadosDiv.innerHTML = html;
        console.log("7. Resultados mostrados en el HTML");
    })
    .catch(error => {
        console.error("ERROR en búsqueda:", error);
        mostrarMensajeEnResultados(`❌ Error al buscar libros: ${error.message}`, "error");
    });
}

// ==================== FUNCIONES DE RESERVA  ====================
function reservarLibro(libroId) {
    console.log("=== reservarLibro EJECUTADA ===");
    
    let usuarioId = localStorage.getItem('usuarioId');
    
    if (!usuarioId) {
        console.error("No hay usuario logueado");
        mostrarNotificacion("⚠️ Debes iniciar sesión primero", "error");
        setTimeout(() => {
            window.location.href = "index.html";
        }, 2000);
        return;
    }
    
    console.log("Usuario ID:", usuarioId);
    console.log("Libro ID:", libroId);
    
    // Mostrar confirmación con estilo
    if (!confirm("📚 ¿Confirmas la reserva de este libro?")) {
        return;
    }
    
    // Mostrar notificación de carga
    mostrarNotificacion("⏳ Procesando reserva...", "info");
    
    // Primero obtenemos el título del libro para mostrarlo en el mensaje
    fetch(API + "/libros/" + libroId)
    .then(response => {
        if (!response.ok) {
            throw new Error("No se pudo obtener información del libro");
        }
        return response.json();
    })
    .then(libro => {
        console.log(`Reservando libro: ${libro.titulo}`);
        
        const reservaData = {
            usuarioId: parseInt(usuarioId),
            libroId: parseInt(libroId)
        };
        
        return fetch(API + "/reservas", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(reservaData)
        })
        .then(async response => {
            if (response.ok) {
                const data = await response.json();
                return { data, libro };
            } else {
                const errorText = await response.text();
                console.error("Error del servidor:", errorText);
                
                // Intentar extraer el mensaje de error
                let mensajeError = errorText;
                
                // Mensajes personalizados según el tipo de error
                if (errorText.includes("límite de 5") || errorText.includes("5 reservas")) {
                    mensajeError = "❌ Has alcanzado el límite de 5 reservas. No puedes reservar más.";
                } else if (errorText.includes("ya has reservado") || errorText.includes("Ya has reservado")) {
                    mensajeError = "❌ Ya has reservado este libro anteriormente. No puedes reservarlo de nuevo.";
                } else if (errorText.includes("No hay copias disponibles")) {
                    mensajeError = "❌ Lo sentimos, este libro no tiene copias disponibles en este momento.";
                }
                
                throw new Error(mensajeError);
            }
        });
    })
    .then(({ libro }) => {
        console.log("Reserva exitosa:", libro);
        mostrarNotificacion(`✅ "${libro.titulo}" reservado con éxito`, "success");
        buscarLibro(); // Actualizar la lista de búsqueda
    })
  .catch(error => {
    console.error("Error en reserva:", error);
    
    // Limpiar el mensaje de error (quitar cosas raras)
    let mensajeError = error.message;
    
    // Si el mensaje viene con HTML o caracteres raros, extraer solo la parte útil
    if (mensajeError.includes("Límite de 5 reservas")) {
        mensajeError = "❌ Has alcanzado el límite de 5 reservas. No puedes reservar más.";
    } else if (mensajeError.includes("Ya has reservado este libro")) {
        mensajeError = "❌ Ya has reservado este libro anteriormente.";
    } else if (mensajeError.includes("No hay copias disponibles")) {
        mensajeError = "❌ No hay copias disponibles de este libro.";
    }
    
    mostrarNotificacion(mensajeError, "error");
});
}

// ==================== FUNCIONES DE HISTORIAL ====================
function cargarHistorial() {
    console.log("=== cargarHistorial EJECUTADA ===");
    
    let usuarioId = localStorage.getItem('usuarioId');
    let usuarioNombre = localStorage.getItem('usuarioNombre') || 'Usuario';
    
    if (!usuarioId) {
        console.error("No hay usuario logueado");
        mostrarNotificacion("⚠️ Debes iniciar sesión primero", "error");
        window.location.href = "index.html";
        return;
    }
    
    console.log("Cargando historial para usuario ID:", usuarioId);
    
    // Mostrar loading
    let historialDiv = document.getElementById("historialResultados");
    if (historialDiv) {
        historialDiv.innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                <p>Cargando historial...</p>
            </div>
        `;
    }
    
    fetch(API + "/reservas/usuario/" + usuarioId)
    .then(response => {
        console.log("Respuesta historial status:", response.status);
        if (!response.ok) {
            throw new Error("Error al cargar historial");
        }
        return response.json();
    })
    .then(async reservas => {
        console.log("Reservas encontradas:", reservas);
        
        let html = `
            <h2>📋 Historial de Reservas - ${usuarioNombre}</h2>
            <p>Total de reservas: ${reservas.length}</p>
        `;
        
        if (reservas.length === 0) {
            html += "<p class='no-results'>📭 No tienes reservas activas</p>";
        } else {
            html += '<div class="reservas-lista">';
            
            // Cargar detalles de cada libro
            for (let reserva of reservas) {
                try {
                    const libroResponse = await fetch(API + "/libros/" + reserva.libroId);
                    if (!libroResponse.ok) continue;
                    
                    const libro = await libroResponse.json();
                    
                    html += `
                        <div class="reserva-card">
                            <div class="reserva-content">
                                <div class="reserva-info">
                                    <h3>📖 ${libro.titulo}</h3>
                                    <p><strong>Autor:</strong> ${libro.autor}</p>
                                    <p><strong>Fecha de reserva:</strong> ${formatearFecha(reserva.fechaReserva)}</p>
                                    <p><strong>Estado:</strong> <span class="estado-activa">Activa ✅</span></p>
                                </div>
                                <div>
                                    <button class="btn-danger" onclick="cancelarReserva(${reserva.id})">
                                        🗑️ Cancelar reserva
                                    </button>
                                </div>
                            </div>
                        </div>
                    `;
                } catch (error) {
                    console.error("Error al cargar libro:", error);
                }
            }
            html += '</div>';
        }
        
        if (historialDiv) {
            historialDiv.innerHTML = html;
        }
    })
    .catch(error => {
        console.error("Error en historial:", error);
        if (historialDiv) {
            historialDiv.innerHTML = `
                <div class="alert alert-error">
                    <p>❌ Error al cargar el historial</p>
                </div>
            `;
        }
        mostrarNotificacion("❌ Error al cargar el historial", "error");
    });
}

// ==================== FUNCIONES DE CANCELACIÓN ====================
function cancelarReserva(reservaId) {
    console.log("=== cancelarReserva EJECUTADA ===");
    
    if (!confirm("¿Estás seguro de cancelar esta reserva?")) {
        return;
    }
    
    console.log("Cancelando reserva ID:", reservaId);
    
    fetch(API + "/reservas/" + reservaId, {
        method: "DELETE"
    })
    .then(async response => {
        if (response.ok) {
            console.log("Reserva cancelada exitosamente");
            mostrarNotificacion("✅ Reserva cancelada con éxito", "success");
            
            // Recargar según la página actual
            if (window.location.pathname.includes("historial.html")) {
                cargarHistorial();
            } else if (window.location.pathname.includes("buscar.html")) {
                buscarLibro();
            }
        } else {
            const error = await response.text();
            throw new Error(error);
        }
    })
    .catch(error => {
        console.error("Error cancelando reserva:", error);
        mostrarNotificacion("❌ Error al cancelar la reserva: " + error.message, "error");
    });
}

// ==================== FUNCIÓN DE NOTIFICACIONES  ====================
function mostrarNotificacion(mensaje, tipo) {
    // Eliminar notificación existente si la hay
    let notificacionExistente = document.getElementById('notificacion');
    if (notificacionExistente) {
        notificacionExistente.remove();
    }
    
    // Crear elemento de notificación
    const notificacion = document.createElement('div');
    notificacion.id = 'notificacion';
    notificacion.textContent = mensaje;
    notificacion.className = tipo;
    
    // Estilos CSS
    const style = document.createElement('style');
    style.textContent = `
        #notificacion {
            position: fixed;
            top: 20px;
            right: 20px;
            min-width: 320px;
            padding: 16px 24px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 500;
            box-shadow: 0 6px 16px rgba(0,0,0,0.2);
            z-index: 9999;
            animation: slideIn 0.3s ease;
            transition: all 0.3s ease;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            border-left: 6px solid rgba(255,255,255,0.5);
        }
        
        #notificacion.success {
            background: linear-gradient(135deg, #4CAF50, #2E7D32);
            color: white;
        }
        
        #notificacion.error {
            background: linear-gradient(135deg, #f44336, #B71C1C);
            color: white;
        }
        
        #notificacion.info {
            background: linear-gradient(135deg, #2196F3, #0D47A1);
            color: white;
        }
        
        #notificacion.warning {
            background: linear-gradient(135deg, #ff9800, #BF360C);
            color: white;
        }
        
        #notificacion.fade-out {
            animation: fadeOut 0.3s ease forwards;
        }
        
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes fadeOut {
            from {
                opacity: 1;
                transform: translateX(0);
            }
            to {
                opacity: 0;
                transform: translateX(100%);
            }
        }
    `;
    
    document.head.appendChild(style);
    document.body.appendChild(notificacion);
    
    // Ocultar después de 4 segundos
    setTimeout(() => {
        notificacion.classList.add('fade-out');
        setTimeout(() => {
            if (notificacion.parentNode) {
                notificacion.remove();
            }
        }, 300);
    }, 4000);
}

// ==================== FUNCIONES AUXILIARES ====================
function formatearFecha(fecha) {
    if (!fecha) return "Fecha no disponible";
    try {
        const options = { 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric'
        };
        return new Date(fecha).toLocaleDateString('es-ES', options);
    } catch (e) {
        return fecha;
    }
}

function mostrarMensajeEnResultados(mensaje, tipo) {
    const resultados = document.getElementById("resultados");
    if (resultados) {
        resultados.innerHTML = `<div class="alert alert-${tipo}">${mensaje}</div>`;
    }
}

// ==================== FUNCIÓN DE LOGOUT ====================
function logout() {
    console.log("=== logout EJECUTADO ===");
    localStorage.removeItem('usuarioId');
    localStorage.removeItem('usuarioNombre');
    mostrarNotificacion("👋 Sesión cerrada correctamente", "info");
    setTimeout(() => {
        window.location.href = "index.html";
    }, 1500);
}

// ==================== FUNCIÓN PARA APLICAR FILTROS ====================
window.aplicarFiltro = function(termino) {
    console.log("Aplicando filtro:", termino);
    let inputBuscar = document.getElementById('buscar');
    if (inputBuscar) {
        inputBuscar.value = termino;
        buscarLibro();
    } else {
        console.error("Error: Input de búsqueda no encontrado");
        mostrarNotificacion("❌ Error al aplicar filtro", "error");
    }
};

// ==================== INICIALIZACIÓN ====================
document.addEventListener('DOMContentLoaded', function() {
    console.log("✅ app.js cargado correctamente");
    console.log("API URL:", API);
    
    // Mostrar nombre de usuario en la navegación
    let userNameSpan = document.getElementById('userName');
    if (userNameSpan) {
        let usuarioNombre = localStorage.getItem('usuarioNombre');
        userNameSpan.innerText = usuarioNombre ? `👋 ${usuarioNombre}` : '👋 Invitado';
    }
    
    // Verificar si estamos en historial.html
    if (window.location.pathname.includes("historial.html")) {
        console.log("Página de historial detectada");
        cargarHistorial();
    }
    
    // Verificar si hay término de búsqueda en URL
    if (window.location.pathname.includes("buscar.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        const searchTerm = urlParams.get('q');
        if (searchTerm) {
            let inputBuscar = document.getElementById('buscar');
            if (inputBuscar) {
                inputBuscar.value = searchTerm;
                setTimeout(buscarLibro, 200);
            }
        }
    }
    
    // Verificar si hay mensaje de registro exitoso
    if (window.location.pathname.includes("index.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('registro') === 'exitoso') {
            const usuario = urlParams.get('usuario');
            mostrarNotificacion(`✅ ¡Registro exitoso! Bienvenido ${usuario}. Por favor inicia sesión.`, 'success');
            let inputUsuario = document.getElementById('usuario');
            if (inputUsuario) {
                inputUsuario.value = usuario;
            }
        }
    }
});