package cool.structures;

import cool.structures.Symbol;
import cool.structures.TypeSymbol;

public class IdSymbol extends Symbol {
    // Fiecare identificator posedă un tip.
    protected TypeSymbol type;
    public int offset = 0;
    public String idType = null;
    public IdSymbol(String name) {
        super(name);
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    public TypeSymbol getType() {
        return type;
    }
}