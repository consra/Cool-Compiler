package cool.utils;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class SemanticError {
    private String errorMsg;
    private ParserRuleContext ctx;
    private Token token;

    public SemanticError(String errorMsg, ParserRuleContext ctx, Token token) {
        this.errorMsg = errorMsg;
        this.ctx = ctx;
        this.token = token;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ParserRuleContext getCtx() {
        return ctx;
    }

    public void setCtx(ParserRuleContext ctx) {
        this.ctx = ctx;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
