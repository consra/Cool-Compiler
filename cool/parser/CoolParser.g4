parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

program
    : (clas SEMI)* EOF
    ; 

clas
    : CLASS name=TYPE (INHERITS parent=TYPE)? LBRACE (definition SEMI)* RBRACE
    ;

vardef : formal (ASSIGN init=expr)? ;

definition
    :   vardef                  # varDef
    |   name=ID LPAREN (formals+=formal (COMMA formals+=formal)*)? RPAREN TWOP TYPE
             LBRACE body=expr RBRACE      # funcDef
    ;
    
formal
    :   name=ID TWOP type=TYPE
    ;

expr
    : dis=expr (AT TYPE)? (DOT name=ID LPAREN (args+=expr (COMMA args+=expr)*)? RPAREN) # methDispatch
    | name=ID LPAREN (args+=expr (COMMA args+=expr)*)? RPAREN     # call
    |   TLD expr                                                    # tld
    |   MINUS e=expr                                                # unaryMinus
    |   left=expr (MULT | DIV) right=expr                           # multDiv
    |   left=expr (PLUS | MINUS) right=expr                         # plusMinus
    |   left=expr (LT | LE | EQUAL) right=expr                      # relational
    |   IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI   # if
    |   NEW TYPE                                                    # instance
    |   name=ID ASSIGN e=expr                                       # assign
    |   LPAREN e=expr RPAREN                                        # paren
    |   ID                                                          # id
    |   INT                                                         # int
    |   BOOL                                                        # bool
    |   LBRACE (exprs+=expr SEMI)+ RBRACE                           # blocks
    |   STRING                                                      # string
    |   NOT expr                                                    # not
    |   ISVOID expr                                                 # isvoid
    |   WHILE cond=expr LOOP body=expr POOL                         # while
    |   LET first=vardef (COMMA vardefs+=vardef)* IN expr # let
    |   CASE cond=expr OF (formals+=formal ARROW exprs+=expr SEMI)+ ESAC  # case
    ;