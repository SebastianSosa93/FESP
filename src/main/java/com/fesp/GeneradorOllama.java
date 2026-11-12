package com.fesp;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeneradorOllama {

	private static final HttpClient CLIENTE = HttpClient.newHttpClient();
	
	public String generarTagHtml(String tag, String texto, String atributo)
            throws Exception {

        // Construir el mensaje para Ollama
    	String instruccion;

    	if (!tag.equals("button") &&
    		    !tag.equals("text") &&
    		    !tag.equals("password") &&
    		    !tag.equals("email") &&
    		    !tag.equals("number")&&
    		    !tag.equals("textarea")) {

    		    throw new IllegalArgumentException(
    		        "Tipo de elemento HTML no permitido: " + tag
    		    );
    		}
    	
    	if (tag.equals("button")) {

    		instruccion = """
			    Devuelve únicamente código HTML.			   
			    Genera un elemento HTML button.

			    Reglas obligatorias:
			    - Solamente agrega esto: atributo type= "%s" y luego de '>' %s. Nada más.			     			  
			    - No utilices input.
			    - Conserva exactamente el texto indicado.
			    - No agregues explicaciones ni markdown.<

			    """.formatted(
			    		atributo,
			    		texto
			    		);
    	} else if (tag.equals("textarea")) {

    	    instruccion = """
    	        Genera únicamente el elemento HTML textarea.
    	        No uses markdown ni explicaciones.
    	        
    	        El elemento debe cumplir estas reglas:    	      
    	        -obligatorio incluir atributo id y name con %s
    	        %s
    	        - No agregues otros atributos.
    	        - Devuelve solamente el elemento HTML.
    	        - No utilices table, div, p, ni ningún otro elemento contenedor.
    	    	- No agregues ningún elemento HTML adicional.
    	    	- Es obligatorio comenzar con <textarea> y obligatorio escribir </textarea> antes de terminar.
    	        """.formatted(  
    	        	texto.toLowerCase().replace(" ","_"),
    	            atributo != null
    	                ? "- Agrega el atributo placeholder con el texto: \"" + atributo + "\".\n"
    	                : ""
    	        );

    	} else {

    	    String atributoPlaceholder = "";

    	    if (atributo != null) {
    	        atributoPlaceholder = "- Agrega el atributo placeholder con el texto: \"" 
    	                             + atributo + "\".\n";
    	    }

    	    instruccion = """
    	        Genera únicamente un elemento HTML input.
    	        No uses markdown ni explicaciones.
    	        El elemento debe cumplir estas reglas:
    	        - type debe ser "%s".
    	        %s
    	        - No agregues otros atributos.
    	        Devuelve solamente el elemento HTML.
    	        """.formatted(
    	            tag,    	           
    	            atributoPlaceholder
    	        );

    	}

        // Construir la solicitud JSON
        JsonObject mensaje = new JsonObject();
        mensaje.addProperty("role", "user");
        mensaje.addProperty("content", instruccion);

        JsonArray mensajes = new JsonArray();
        mensajes.add(mensaje);

        JsonObject solicitudJson = new JsonObject();
        solicitudJson.addProperty("model", "llama3.2:1b");
        solicitudJson.add("messages", mensajes);
        solicitudJson.addProperty("stream", false);

        // Convertir el JSON a String
        String json = new Gson().toJson(solicitudJson);

        // Enviar solicitud HTTP    

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(3))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> respuesta = CLIENTE.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );

        if (respuesta.statusCode() != 200) {
            throw new RuntimeException(
                "Error al comunicarse con Ollama: "
                + respuesta.statusCode()
            );
        }

        // Extraer el contenido generado
        JsonObject jsonRespuesta = JsonParser
                .parseString(respuesta.body())
                .getAsJsonObject();

        String html = jsonRespuesta
                .getAsJsonObject("message")
                .get("content")
                .getAsString();

        // Eliminar posibles bloques Markdown
        html = html.replace("```html", "")
                   .replace("```", "")
                   .trim();

        
        System.out.println("HTML recibido de Ollama:");
        System.out.println("[" + html + "]");
     
        // Validar el elemento generado

        if (tag.equals("button")) {
            html = validarBoton(html, texto, atributo);
         
        } else {
        	html = validarCampo(html, tag, texto, atributo);
        	
        }

        return html;
    }
    
    private String validarBoton(String html,String texto, String atributo) {

        if (!html.contains("type=\""+atributo+"\"")) {
            html = html.replaceFirst(
                "<button>",
                "<button type=\"" +atributo+"\">"
            );

            System.out.println("HTML corregido:");
            System.out.println("[" + html + "]");
        }

        if (!html.matches(
            "(?s)<button type=\""+atributo+"\">.*</button>"
        )) {
            throw new RuntimeException(
                "Ollama generó un botón HTML no válido."
            );
        }

        return html;
    }
    
    private String validarCampo(String html, String tag,
            String id, String placeholder) {
		
		if (tag.equals("textarea")) {
		
			if(!html.contains("</textarea>")){
				html += "</textarea>";
				
				 System.out.println("HTML corregido:");
		         System.out.println("[" + html + "]");
			}
			
			if (!html.matches("(?s)\\s*<textarea\\b[^>]*>.*</textarea>\\s*")) {
				throw new RuntimeException(
						"Ollama generó un área de texto HTML no válida."
				);
			}
			
			String atributoPlaceholder = placeholder != null
			? " placeholder=\"" + escaparHtml(placeholder) + "\""
			: "";
			
			return "<textarea id=\"" + escaparHtml(id)
					+ "\" name=\"" + escaparHtml(id) + "\""
					+ atributoPlaceholder + "></textarea>";
		
		} 
		else {
		
			if (!html.matches("(?s)\\s*<input\\b[^>]*?/?>\\s*")) {
				throw new RuntimeException(
						"Ollama generó un campo HTML no válido."
						);
			}
			
			String atributoPlaceholder = placeholder != null
			? " placeholder=\"" + escaparHtml(placeholder) + "\""
			: "";
			
			return "<input type=\"" + tag
					+ "\" id=\"" + escaparHtml(id)
					+ "\" name=\"" + escaparHtml(id) + "\""
					+	 atributoPlaceholder + ">";
		}
	}
    
    private String escaparHtml(String texto) {
		return texto.replace("&","&amp;")
					.replace("<", "&lt;")
					.replace(">", "&gt;")
					.replace("\"","&quot;")
					.replace("'", "&#39;");							
	}
}