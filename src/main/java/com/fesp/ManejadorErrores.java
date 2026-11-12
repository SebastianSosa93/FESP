package com.fesp;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;

public class ManejadorErrores extends BaseErrorListener {
	private boolean hayErrores = false;
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object simbolo,int linea,
							int columna, String mensaje,RecognitionException exception) 
	{
		hayErrores = true;
		
		String tipoError;
		
		if(recognizer instanceof org.antlr.v4.runtime.Lexer) {
			tipoError = "Error Léxico";
		}else {
			tipoError = "Error sintáctico";
		}
		
				
		System.out.println(tipoError+" en línea "+ linea	+", columna " + columna
							+": " + mensaje);
	}
	
	public boolean hayErrores() {
		return hayErrores;
	}
	
	
}
