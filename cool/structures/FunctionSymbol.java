package cool.structures;

import cool.nodes.FuncDef;

import java.util.*;

// O functie este atât simbol, cât și domeniu de vizibilitate pentru parametrii
// săi formali.

// TODO 1: Implementați clasa FunctionSymbol, suprascriind metodele din interfață
// și adăugându-i un nume.
public class FunctionSymbol extends IdSymbol implements Scope {

    // LinkedHashMap reține ordinea adăugării.
    protected Map<String, Symbol> symbols = new LinkedHashMap<>();
    protected FuncDef funcDef;
    protected Scope parent;
    public int offset = 0;
    public FuncDef getFuncDef() {
        return funcDef;
    }

    public void setFuncDef(FuncDef funcDef) {
        this.funcDef = funcDef;
    }

    public FunctionSymbol(Scope parent, String name, FuncDef funcDef) {
        super(name);
        this.parent = parent;
        this.funcDef = funcDef;
    }

    @Override
    public boolean add(String name, Symbol sym) {
        // Ne asigurăm că simbolul nu există deja în domeniul de vizibilitate
        // curent.
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