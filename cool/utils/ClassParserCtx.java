package cool.utils;

import cool.structures.ClassSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;

public class ClassParserCtx {
    private String className;
    private Token token;
    public int highestOffset = 12;
    public int tag;
    public int endingTag;

    public ArrayList<String> getAttributesName() {
        return attributesName;
    }

    public void setAttributesName(ArrayList<String> attributesName) {
        this.attributesName = attributesName;
    }

    public ArrayList<String> getMethodsName() {
        return methodsName;
    }

    public void setMethodsName(ArrayList<String> methodsName) {
        this.methodsName = methodsName;
    }

    ArrayList<String> attributesName = new ArrayList<>();
    ArrayList<String> methodsName = new ArrayList<>();

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    public void setClassSymbol(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
    }

    public ClassSymbol classSymbol = null;
    public String getClassName() {
        return className;
    }

    public ClassParserCtx(String className, ParserRuleContext ctx) {
        this.className = className;
        this.ctx = ctx;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ParserRuleContext getCtx() {
        return ctx;
    }

    public ClassParserCtx(String className, ParserRuleContext ctx, Token token) {
        this.className = className;
        this.token = token;
        this.ctx = ctx;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setCtx(ParserRuleContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object obj) {
        return this.className.equals(((ClassParserCtx)obj).className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    private ParserRuleContext ctx;

    public static ClassParserCtx getObjCtx() {
        return new ClassParserCtx("Object", null, null);
    }
}
