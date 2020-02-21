package cool.structures;


public interface Scope {
    // Adaugă un simbol în domeniul de vizibilitate curent.
    public boolean add(String name, Symbol s);

    // Caută un simbol în domeniul de vizibilitate curent sau în cele superioare.
    public Symbol lookup(String s);

    // Întoarce domeniul de vizibilitate de deasupra.
    public Scope getParent();

    public boolean addId(Symbol s);
    public boolean addMethod(Symbol s);
    public Symbol lookupId(String s);
    public Symbol lookupMethod(String s);

}