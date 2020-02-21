package cool.nodes;

import org.antlr.v4.runtime.Token;

// Orice expresie.
public abstract class Expression extends ASTNode {
    // Reținem un token descriptiv al expresiei, pentru a putea afișa ulterior
    // informații legate de linia și coloana eventualelor erori semantice.
    public Token token;

    public Expression(Token token) {
        super(token);
    }
}

