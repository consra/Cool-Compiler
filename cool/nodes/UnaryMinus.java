package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class UnaryMinus extends Expression {
    public Expression expr;
    public ParserRuleContext ctx;
    public UnaryMinus(Expression expr, Token token, ParserRuleContext ctx) {
        super(token);
        this.expr = expr;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

