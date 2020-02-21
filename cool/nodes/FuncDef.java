package cool.nodes;


import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class FuncDef extends Definition {
    public Id id;
    public Type type;
    public LinkedList<Definition> formals;
    public Expression body;
    public Token start;
    public ParserRuleContext ctx;
    public String className;
    public FuncDef(LinkedList<Definition> formals, Expression body, Id id, Type type, Token start, ParserRuleContext ctx) {
        super(start);
        this.body = body;
        this.id = id;
        this.type = type;
        this.formals = formals;
        this.start = start;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

