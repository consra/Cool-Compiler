package cool.nodes;


import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Formal extends Definition {
    public Id id;
    public Type type;
    public Token start;
    public String formalType = null;
    public ParserRuleContext ctx;
    public Formal(Id id, Type type, Token token, String formalType, ParserRuleContext ctx) {
        super(token);
        this.start = token;
        this.id = id;
        this.type = type;
        this.formalType = formalType;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

