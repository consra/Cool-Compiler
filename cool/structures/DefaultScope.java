package cool.structures;

import cool.structures.Scope;
import cool.structures.Symbol;

import java.util.*;

public class DefaultScope implements Scope {
    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    // LinkedHashMap reține ordinea adăugării.
    protected Map<String, Symbol> symbols = new LinkedHashMap<>();

    protected Scope parent;

    public DefaultScope(Scope parent) {
        this.parent = parent;
    }

    public boolean add(String name, Symbol sym) {
        // Ne asigurăm că simbolul nu există deja în domeniul de vizibilitate
        // curent.
        if (symbols.containsKey(name))
            return false;

        symbols.put(name, sym);

        return true;
    }

    public Symbol lookup(String s) {
        var sym = symbols.get(s);

        if (sym != null)
            return sym;

        // Dacă nu găsim simbolul în domeniul de vizibilitate curent, îl căutăm
        // în domeniul de deasupra.
        if (parent != null)
            return parent.lookup(s);

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
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

    @Override
    public String toString() {
        return symbols.values().toString();
    }
}