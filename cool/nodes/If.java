package cool.nodes;


import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class If extends Expression {
    public Expression cond;
    public Expression thenBranch;
    public Expression elseBranch;
    public Token start;
    public ParserRuleContext ctx;
    public If(Expression cond,
       Expression thenBranch,
       Expression elseBranch,
       Token start, ParserRuleContext ctx) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.start = start;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}