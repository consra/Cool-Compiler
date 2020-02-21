package cool.nodes;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Assign extends Expression {
    public Expression expr;
    public Expression id;
    public ParserRuleContext ctx;

    public Assign(Expression expr, Expression id, Token token, ParserRuleContext ctx) {
        super(token);
        this.expr = expr;
        this.id = id;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

