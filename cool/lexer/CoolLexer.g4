lexer grammar CoolLexer;

tokens { ERROR }
@header{
    package cool.lexer;
}

@members{
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }

    public String expandEscaped(String msg) {
        String res = msg.replace("\\n", "\n");
        res = res.replace("\\t", "\t");
        res = res.replace("\\\\", "\0");
        res = res.replace("\\", "");
        res = res.replace("\0", "\\");
        res = res.substring(1, res.length()-1);
        return res;
    }
}

IF : 'if';
THEN : 'then';
ELSE : 'else';
FI : 'fi';
CLASS : 'class';
INHERITS : 'inherits';
BOOL : 'true' | 'false';
TWOP : ':';
TYPE : 'SELF_TYPE' | [A-Z] [0-9a-zA-Z_]*;
NEW : 'new';
LET : 'let';
IN : 'in';
WHILE : 'while';
LOOP : 'loop';
POOL : 'pool';
CASE : 'case';
ESAC : 'esac';
NOT : 'not';
ISVOID : 'isvoid';
OF : 'of';
ARROW : '=>';

/* Identificator.
 */
fragment LETTER : [a-zA-Z];
fragment LOWLETTER : [a-z];
ID : LOWLETTER(LETTER | '_' | DIGIT)*;

/* Număr întreg.
 *
 * fragment spune că acea categorie este utilizată doar în interiorul
 * analizorului lexical, nefiind trimisă mai departe analizorului sintactic.
 */
fragment DIGIT : [0-9];
INT : DIGIT+;

/* Număr real.
 */
fragment DIGITS : DIGIT+;
fragment EXPONENT : 'e' ('+' | '-')? DIGITS;

UNEDINGSTRING
   : '"'  ('\\\r\n' | '\\\n'|  ~["])*? (
     [\r\n] { raiseError("Unterminated string constant"); }
     | (EOF { raiseError("EOF in string constant"); })
     )
   ;

STRING
    :  '"' ( ('\\\r\n' | '\\\n')
    |  '\u0000' { raiseError("SString contains null characterr"); }
    |  ~[\r\n]
    |  '\\' .)*? '"'
      { if (getText().length() > 1024) {
         raiseError("SString constant too longg");
      }
         setText(expandEscaped(getText()));
      };

SEMI : ';';

COMMA : ',';

ASSIGN : '<-';

LPAREN : '(';

RPAREN : ')';

LBRACE : '{';

RBRACE : '}';

PLUS : '+';

MINUS : '-';

TLD: '~';

MULT : '*';

DIV : '/';

EQUAL : '=';

LT : '<';

LE : '<=';

AT : '@';

DOT : '.';

fragment NEW_LINE : '\r'? '\n';
LINE_COMMENT
    : '--' .*? (NEW_LINE | EOF) -> skip;


EOF_COMM : OPEN_COMMENT (BLOCK_COMMENT | ~[*)])*? EOF
{ raiseError("EOF in comment"); };

BLOCK_COMMENT
    : OPEN_COMMENT (BLOCK_COMMENT | .)*? CLOSE_COMMENT -> skip
    ;

OPEN_COMMENT : '(*' { raiseError("Unmatched (*"); };
CLOSE_COMMENT : '*)' { raiseError("Unmatched *)"); };

WS
    :   [ \n\f\r\t]+ -> skip
    ;

Ef : EOF -> skip;
UNMATCHED_CHAR : . { raiseError("Invalid character: " + getText()); };