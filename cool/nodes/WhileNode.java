package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class WhileNode extends Expression {
    public Expression cond;
    public Expression body;
    public Token start;
    public ParserRuleContext ctx;
    public WhileNode(Expression cond, Expression body, Token token, Token start, ParserRuleContext ctx) {
        super(token);
        this.cond = cond;
        this.body = body;
        this.start = start;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

