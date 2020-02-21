package cool.structures;

public class TypeSymbol extends Symbol {
    public TypeSymbol(String name) {
        super(name);
    }
    public boolean isSelfType = false;

    // Symboluri aferente tipurilor, definite global
    public static final TypeSymbol INT   = new TypeSymbol("Int");
    public static final TypeSymbol FLOAT = new TypeSymbol("Float");
    public static final TypeSymbol BOOL  = new TypeSymbol("Bool");
    public static final TypeSymbol SELF_TYPE = new TypeSymbol("SELF_TYPE");
    public static final TypeSymbol STRING = new TypeSymbol("String");
    public static final TypeSymbol OBJECT = new TypeSymbol("Object");
}