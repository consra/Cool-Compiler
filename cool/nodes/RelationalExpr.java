package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class RelationalExpr extends Expression {
    public Expression expr1;
    public Expression expr2;
    public ParserRuleContext ctx;
    public RelationalExpr(Expression expr1, Expression expr2, Token token, ParserRuleContext ctx) {
        super(token);
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

