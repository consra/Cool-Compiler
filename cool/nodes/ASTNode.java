package cool.nodes;
import org.antlr.v4.runtime.Token;

// Rădăcina ierarhiei de clase reprezentând nodurile arborelui de sintaxă
// abstractă (AST). Singura metodă permite primirea unui visitor.
public abstract class ASTNode {
    public Token token;
    public String debugStr = null;
    public ASTNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

