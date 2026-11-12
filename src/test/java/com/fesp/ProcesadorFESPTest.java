package com.fesp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ProcesadorFESPTest {

    @Test
    public void generarFormularioConGET() {

        String codigo = """
            formulario "Registro", get
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        String html = procesador.procesar(codigo);

        assertTrue(html.contains("<form method=\"get\">"));
    }
    
    @Test
    public void generarFormularioConPOST() {

        String codigo = """
            formulario "Registro", post
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        String html = procesador.procesar(codigo);

        assertTrue(html.contains("<form method=\"post\">"));
    }

    @Test
    public void generarBotones() {

        String codigo = """
            formulario "Registro", post
            boton enviar "Registrarse"
            boton "Volver"
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        String html = procesador.procesar(codigo);

        assertTrue(html.contains("<button type=\"submit\">Registrarse</button>"));
        assertTrue(html.contains("<button type=\"button\">Volver</button>"));
    }
    
    @Test
    public void generarCampoTexto() {

        String codigo = """
            formulario "Registro", post
            campo texto, etiqueta "Nombre"
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        String html = procesador.procesar(codigo);

        assertTrue(html.contains("<label for="));
        assertTrue(html.contains("type=\"text\""));
    }
    
    @Test
    public void generarCampoContrasena() {

        String codigo = """
            formulario "Registro", post
            campo contraseña, etiqueta "Clave"
            fin
            """;

        String html = new ProcesadorFESP().procesar(codigo);
        
        assertTrue(html.contains("type=\"password\""));
    }

    @Test
    public void generarCampoCorreo() {

        String codigo = """
            formulario "Registro", post
            campo correo, etiqueta "Correo"
            fin
            """;

        String html = new ProcesadorFESP().procesar(codigo);

        assertTrue(html.contains("type=\"email\""));
    }

    @Test
    public void generarCampoNumero() {

        String codigo = """
            formulario "Registro", post
            campo numero, etiqueta "Edad"
            fin
            """;

        String html = new ProcesadorFESP().procesar(codigo);

        assertTrue(html.contains("type=\"number\""));
    }
    
    @Test
    public void generarCampoConPlaceholder() {

        String codigo = """
            formulario "Registro", post
            campo texto, placeholder "Ingrese su nombre"
            fin
            """;

        String html = new ProcesadorFESP().procesar(codigo);
       
        assertTrue(html.contains("placeholder=\"Ingrese su nombre\""));
    }
    
    @Test
    public void generarAreaTexto() {

        String codigo = """
            formulario "Contacto", post
            campo area, etiqueta "Mensaje"
            fin
            """;

        String html = new ProcesadorFESP().procesar(codigo);

        assertTrue(html.contains("<textarea"));
    }
    
    @Test
    public void generarCampoConEtiquetaYPlaceholder() {

        String codigo = """
            formulario "Registro", post
            campo texto, etiqueta "Nombre", placeholder "Ingrese su nombre"
            fin
            """;

        String html = new ProcesadorFESP().procesar(codigo);

        assertTrue(html.contains("<label for="));
        assertTrue(html.contains("<input type=\"text\""));
        assertTrue(html.contains("placeholder=\"Ingrese su nombre\""));
    }
    
    @Test
    public void rechazarCampoSinPropiedades() {

        String codigo = """
            formulario "Registro", post
            campo texto,
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        assertThrows(RuntimeException.class, () -> {
            procesador.procesar(codigo);
        });
    }
    
    @Test
    public void rechazarPalabraInvalida() {

        String codigo = """
            formulario "Registro", post            
            campus texto
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        assertThrows(RuntimeException.class, () -> {
            procesador.procesar(codigo);
        });
    }
    
    @Test
    public void rechazarPlaceholdersRepetidos() {

        String codigo = """
            formulario "Registro", post
            campo texto, placeholder "Ingrese nombre", placeholder "Nombre completo"
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        assertThrows(RuntimeException.class, () -> {
            procesador.procesar(codigo);
        });
    }
    
    @Test
    public void rechazarEtiquetasRepetidas() {

        String codigo = """
            formulario "Registro", post
            campo texto, etiqueta "Nombre", etiqueta "Apellido"
            fin
            """;

        ProcesadorFESP procesador = new ProcesadorFESP();

        assertThrows(RuntimeException.class, () -> {
            procesador.procesar(codigo);
        });
    }
}