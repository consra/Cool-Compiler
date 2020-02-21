package cool.structures;

import cool.utils.GenericPair;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSymbol extends TypeSymbol implements Scope {

    protected Map<String, Symbol> symbols = new LinkedHashMap<>();

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public IdSymbol getSelfSymbol() {
        return selfSymbol;
    }

    public void setSelfSymbol(IdSymbol selfSymbol) {
        this.selfSymbol = selfSymbol;
    }

    private IdSymbol selfSymbol = null;
    protected Scope parent;
    protected ClassSymbol extendedClassScope;

    public Scope getExtendedClassScope() {
        return extendedClassScope;
    }

    public void setExtendedClassScope(ClassSymbol extendedClassScope) {
        this.extendedClassScope = extendedClassScope;
    }

    public ClassSymbol(Scope parent, ClassSymbol extendedClassScope, String name) {
        super(name);
        this.parent = parent;
        this.extendedClassScope = extendedClassScope;
        selfSymbol = new IdSymbol("self");
        selfSymbol.setType(this);
    }

    @Override
    public boolean add(String name, Symbol sym) {
        if (symbols.containsKey(name))
            return false;

        symbols.put(name, sym);

        return true;
    }

    @Override
    public Symbol lookup(String s) {
        var sym = symbols.get(s);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookup(s);

        return null;
    }

    public boolean isIdInExtendedScope(String s) {
        if(extendedClassScope == null)
            return false;

        var sym = extendedClassScope.lookupId(s);
        if(sym != null)
            return true;

        return false;
    }

    public boolean isMethodInExtendedScope(String s) {
        if(extendedClassScope == null)
            return false;

        var sym = extendedClassScope.lookupMethod(s);
        if(sym != null)
            return true;

        return false;
    }

    public Symbol lookupOnlyParentClassMethod(String s) {
        // check in the extended class scope
        var sym = extendedClassScope.lookupMethod(s);
        if(sym != null)
            return sym;
        return null;
    }

    public Symbol lookupMethodParentClass(String s) {
        String methodName = s + "_method";
        return lookupParentClass(methodName);
    }
    // Thie methods returns a Pair, the value representing if the variable was found in the extended class
    public Symbol lookupParentClass(String s) {
        //check in current scope
        var sym = symbols.get(s);
        if (sym != null)
            return sym;

        // check in the extended class scope
        if(extendedClassScope != null) {
            sym = extendedClassScope.lookup(s);
            if (sym != null)
                return sym;
        }

        // check in the global scope
        sym = parent.lookup(s);
        if(sym != null)
            return sym;

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    public Map<String, Symbol> getFormals() {
        return symbols;
    }

    @Override
    public boolean addId(Symbol s) {
        String idName = s.getName() + "_id";
        return add(idName, s);
    }

    @Override
    public boolean addMethod(Symbol s) {
        String methodName = s.getName() + "_method";
        return add(methodName, s);
    }

    @Override
    public Symbol lookupId(String s) {
        String idName = s + "_id";
        return lookup(idName);
    }

    @Override
    public Symbol lookupMethod(String s) {
        String methodName = s + "_method";
        return lookup(methodName);
    }

}
