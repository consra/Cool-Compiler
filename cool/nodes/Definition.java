package cool.nodes;

import org.antlr.v4.runtime.Token;

public abstract class Definition extends ASTNode {
    public Token token;

    public Definition(Token token) {
        super(token);
    }
}

