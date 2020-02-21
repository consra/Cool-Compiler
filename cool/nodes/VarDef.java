package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class VarDef extends Definition {
    public Id id;
    public Type type;
    public Expression initValue;
    public String vardefType = null;
    public Token start;
    public ParserRuleContext ctx;
    public boolean throwedErrorInDefinitonVisitor = false;
    public int offset = 0;
    public String className = null;
    public VarDef(Id id, Type type, Expression expr, String vardefType, Token start, ParserRuleContext ctx) {
        super(start);
        this.id = id;
        this.vardefType = vardefType;
        this.initValue = expr;
        this.type = type;
        this.start = start;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

