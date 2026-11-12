package com.fesp;

public class GeneradorHtml extends FESPBaseListener{
	private StringBuilder elementos = new StringBuilder();	
	private String tituloFormulario;
	private String metodoFormulario;
	private GeneradorOllama generadorOllama = new GeneradorOllama();
	private int contadorCampos = 0;
	//campo de texto
	
	@Override
	public void exitCampo(FESPParser.CampoContext ctx) {
		
	    String tipo = ctx.tipo_campo().getText();

	    String etiqueta = null;
	    
	    String placeholder = null;	    

	    // Buscamos si el campo tiene una etiqueta
	    for (FESPParser.PropiedadContext propiedad :
	        ctx.propiedades().propiedad()) {

		    if (propiedad.etiqueta() != null) {
	
		        if (etiqueta != null) {
		            throw new RuntimeException(
		                "Un campo no puede tener más de una etiqueta."
		            );
		        }
	
		        etiqueta = propiedad.etiqueta().CAD().getText();
		        etiqueta = etiqueta.substring(1, etiqueta.length() - 1);
		    }
	
		    if (propiedad.placeholder() != null) {
	
		        if (placeholder != null) {
		            throw new RuntimeException(
		                "Un campo no puede tener más de un placeholder."
		            );
		        }
	
		        placeholder = propiedad.placeholder().CAD().getText();
		        placeholder = placeholder.substring(1, placeholder.length() - 1);
		    }
	    }	    
	    
	    if (etiqueta == null && placeholder == null) {
	        throw new RuntimeException(
	            "El campo necesita una etiqueta o un placeholder."
	        );
	    }

	    String id = generarID();
	    // Genera la etiqueta HTML
	    if (etiqueta != null) {

	        String etiquetaHtml = escaparHtml(etiqueta);

	        elementos.append("\t\t<label for=\"").append(id).append("\">")
	                 .append(etiquetaHtml).append("</label>\n");
	    }
	    // Convertimos el tipo de campo de FESP al tipo HTML
	    String tipoHtml;

	    switch (tipo) {
	        case "texto":
	            tipoHtml = "text";
	            break;

	        case "contraseña":
	            tipoHtml = "password";
	            break;
	            
	        case "correo":

	            tipoHtml = "email";

	            break;
	        
	        case "numero":

	            tipoHtml = "number";

	            break;
	        
	        case "area":

	            tipoHtml = "textarea";

	            break;

	        default:
	            throw new RuntimeException("Tipo de campo no implementado: " + tipo);
	    }

	 // Ollama genera el input
	    try {
	        String htmlInput = generadorOllama.generarTagHtml(
	            tipoHtml,
	            id,
	            placeholder
	        );

	        elementos.append("\t\t").append(htmlInput).append("\n");

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("No se pudo generar el campo con Ollama.", e);
	    }
	}
	
	
	//boton enviar
	@Override
	public void exitBoton(FESPParser.BotonContext ctx) {

	    String texto = ctx.CAD().getText();

	    texto = texto.substring(1, texto.length() - 1);
	    texto = escaparHtml(texto);
	    
	    String tipoBoton = ctx.ENVIAR() != null ? "submit" : "button";

	    try {


	    	String htmlBoton = generadorOllama.generarTagHtml(
	    			"button", 
	    			texto, 
	    			tipoBoton);
	    	

	        elementos.append("\t\t").append(htmlBoton).append("\n");

	    } catch (Exception e) {

	        throw new RuntimeException(
	            "No se pudo generar el botón con Ollama.", e
	        );
	    }
	}
	
	//obtener el html
	public String obtenerHtml() {
		return "<!DOCTYPE html>\n"
				+"<html lang=\"es\">\n"
				+"<head>\n"
				+"	<meta charset=\"UTF-8\">\n"
				+"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"				
				+"	<title>"+ tituloFormulario + "</title>\n"				
				+"</head>\n"
				+"<body>\n"
				+"	<form method=\""+metodoFormulario+"\">\n"
				+		elementos
				+"	</form>\n"				
				+"</body>\n"
				+"</html>";
	}
	
	@Override
	public void exitFormulario(FESPParser.FormularioContext ctx) {
		tituloFormulario = ctx.CAD().getText();
		tituloFormulario = tituloFormulario.substring(1,tituloFormulario.length()-1);
		tituloFormulario = escaparHtml(tituloFormulario);
		
		 metodoFormulario = ctx.metodo().getText();
	}

	private String generarID() {
	    contadorCampos++;
	    return "campo_" + contadorCampos;
	}
	
	private String escaparHtml(String texto) {
		return texto.replace("&","&amp;")
					.replace("<", "&lt;")
					.replace(">", "&gt;")
					.replace("\"","&quot;")
					.replace("'", "&#39;");							
	}
}
