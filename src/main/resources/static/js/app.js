const API = "http://localhost:8080";

function login(){
    let usuario = document.getElementById("usuario").value;
    let password = document.getElementById("password").value;

    fetch(API + "/usuarios/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({nombre: usuario, password: password})
    })
    .then(r => r.json())
    .then(data => {
        if(data && data.id){
            alert("Login correcto");
            // Guardar datos del usuario si los necesitas después
            localStorage.setItem('usuarioId', data.id);
            localStorage.setItem('usuarioNombre', data.nombre);
            // REDIRIGIR SOLO SI ES CORRECTO
            window.location.href = "buscar.html";
        } else {
            alert("Usuario o contraseña incorrectos");
            // NO REDIRIGIR
        }
    })
    .catch(err => {
        alert("Error al conectar con el servidor");
        console.error(err);
    });
}

function buscarLibro(){
    let titulo = document.getElementById("buscar").value;
    fetch(API + "/libros/buscar/" + titulo)
    .then(r => r.json())
    .then(data => {
        let html = "";
        data.forEach(libro => {
            html += `<p>${libro.titulo} - ${libro.autor} | Disponible: ${libro.disponible}</p>`;
        });
        document.getElementById("resultados").innerHTML = html;
    });
}

function reservar(){
    let usuario = document.getElementById("usuarioId").value;
    let libro = document.getElementById("libroId").value;

    fetch(API + "/reservas", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({usuarioId: usuario, libroId: libro, fecha: "2026-03-10"})
    })
    .then(r => r.json())
    .then(data => alert("Reserva creada correctamente"))
    .catch(err => alert("Error al reservar: " + err));
}

function historial(){
    let usuario = document.getElementById("usuarioHistorial").value;

    fetch(API + "/reservas/usuario/" + usuario)
    .then(r => r.json())
    .then(data => {
        let html = "";
        data.forEach(r => {
            html += `<p>Libro ID: ${r.libroId} | Fecha: ${r.fecha}</p>`;
        });
        document.getElementById("historialResultados").innerHTML = html;
    });
}