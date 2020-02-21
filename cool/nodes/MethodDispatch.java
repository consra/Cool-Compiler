package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class MethodDispatch extends Expression {
    public Expression dispatcher;
    public Expression methCall;
    public Token type;
    public Token dot;
    public Token start;
    public ParserRuleContext ctx;
    public MethodDispatch(Expression dispatcher, Expression methCall, Token type, Token dot, Token start,
                          ParserRuleContext ctx) {
        super(start);
        this.dispatcher = dispatcher;
        this.methCall = methCall;
        this.type = type;
        this.dot = dot;
        this.start = start;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

