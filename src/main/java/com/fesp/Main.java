package com.fesp;
import com.fesp.FESPLexer;
import com.fesp.FESPParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
public class Main {

	public static void main(String[] args) throws IOException {
        String codigo = """
			 formulario "Contacto", post
    campo texto, etiqueta "Nombre", placeholder "Ingrese nombre"
    campo texto, placeholder "Ingrese apellido"
    campo numero, etiqueta "Telefono", placeholder "Ej. 1122334455"
    campo correo, etiqueta "Correo", placeholder "Ej. micorreo@ejemplo.com"
    campo contraseña, etiqueta "Clave"
    campo area, etiqueta "Descripcion"
    boton enviar "Registrar"
    boton "Cancelar"
 fin
            """;

        // Reconocer los caracteres y formar los tokens
        FESPLexer lexer = new FESPLexer(CharStreams.fromString(codigo));

        // Pasar los tokens al parser
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FESPParser parser = new FESPParser(tokens);
        
        ManejadorErrores manejador = new ManejadorErrores();
        
        lexer.removeErrorListeners();
        lexer.addErrorListener(manejador);
        
        parser.removeErrorListeners();
        parser.addErrorListener(manejador);
        
        // Analizar el programa según la regla inicial        
        FESPParser.FormularioContext arbol = parser.formulario();
         
        if(manejador.hayErrores()) {
        	System.out.println("No se pudo generar el HTML debido a errores en el "
        					  + "código FESP.");
        	return;
        }
        
        
        GeneradorHtml generador = new GeneradorHtml();
        ParseTreeWalker.DEFAULT.walk(generador, arbol);
         
        String html = generador.obtenerHtml();

        Path archivo = Path.of("formulario.html");
        
        Files.writeString(
        	archivo,
        	html,
        	StandardCharsets.UTF_8
        );
        
        System.out.println("HTML generado en: "+archivo.toAbsolutePath());
    }
}