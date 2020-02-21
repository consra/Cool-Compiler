package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class TldNode extends Expression {
    public Expression arithmetic_expr;
    public ParserRuleContext ctx;
    public TldNode(Expression expr, Token token, ParserRuleContext ctx) {
        super(token);
        this.arithmetic_expr = expr;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

