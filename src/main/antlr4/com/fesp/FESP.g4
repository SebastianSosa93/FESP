grammar FESP;

// Reglas sintacticas

formulario
    : FORM CAD COMA metodo cuerpo FIN EOF
    ;
    
metodo
	: GET
	| POST
	;	

cuerpo
    : elemento*
    ;

elemento
    : campo
    | boton
    ;

campo
    : CAMPO tipo_campo COMA propiedades
    ;

tipo_campo
    : TXT
    | PASS
    | CORREO
    | NUMERO
    | AREA
    ;

propiedades
    : propiedad (COMA propiedad)*
    ;

propiedad
    : etiqueta
    | placeholder
    ;

etiqueta
    : ETIQ CAD
    ;

placeholder
    : PH CAD
    ;

boton
    : BOTON ENVIAR? CAD
    ;


// Reglas lexicas

FORM   : 'formulario';
FIN    : 'fin';
CAMPO  : 'campo';
GET    : 'get';
POST   : 'post';

TXT    : 'texto';
PASS   : 'contraseña';
CORREO : 'correo';
NUMERO : 'numero';
AREA   : 'area';

ETIQ   : 'etiqueta';
PH     : 'placeholder';

BOTON  : 'boton';
ENVIAR : 'enviar';

COMA   : ',';

CAD    : '"' ~["\r\n]* '"';

ESPA   : [ \t\r\n]+ -> skip;