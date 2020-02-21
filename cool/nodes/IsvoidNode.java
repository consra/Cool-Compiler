package cool.nodes;

import org.antlr.v4.runtime.Token;

public class IsvoidNode extends Expression {
    public Expression expr;
    public IsvoidNode(Expression expr, Token token) {
        super(token);
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

