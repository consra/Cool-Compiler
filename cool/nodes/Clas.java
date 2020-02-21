package cool.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Clas extends ASTNode {
    public LinkedList<Definition> defs;
    public Token name;
    public Token parent;
    public Token start;
    public ParserRuleContext ctx;
    public Clas(Token name, Token parent, LinkedList<Definition> defs, Token start, ParserRuleContext ctx) {
        super(start);
        this.defs = defs;
        this.name = name;
        this.parent = parent;
        this.ctx = ctx;
        this.start = start;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

