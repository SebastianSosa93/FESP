package com.fesp;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class ProcesadorFESP {

    public String procesar(String codigo) {

        FESPLexer lexer = new FESPLexer(CharStreams.fromString(codigo));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        FESPParser parser = new FESPParser(tokens);

        ManejadorErrores manejador = new ManejadorErrores();

        lexer.removeErrorListeners();
        lexer.addErrorListener(manejador);

        parser.removeErrorListeners();
        parser.addErrorListener(manejador);

        FESPParser.FormularioContext arbol = parser.formulario();

        if (manejador.hayErrores()) {
            throw new RuntimeException("El código FESP contiene errores.");
        }

        GeneradorHtml generador = new GeneradorHtml();

        ParseTreeWalker.DEFAULT.walk(generador, arbol);               

        return generador.obtenerHtml();
    }
}