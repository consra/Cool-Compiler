package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class InstanceNode extends Expression {
    public Token n;
    public Token type;
    public Token start;
    public ParserRuleContext ctx;
    public String selfTypeValue = null;
    public InstanceNode(Token n, Token token, Token start, ParserRuleContext ctx) {
        super(start);
        this.n = n;
        this.type = token;
        this.start = start;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

