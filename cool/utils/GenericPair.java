package cool.utils;

public class GenericPair<K, V> {
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public GenericPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    private K key;
    private V value;

}
