package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;

import java.util.Iterator;
import java.util.LinkedList;

public class HashMapIndirecte<K extends Comparable<K>, V> implements IHashMap<K, V> {
    private LinkedList<Entry<K, V>>[] table;
    private int numElements;
    private static final int INITIAL_CAPACITY = 10;
    private static final float MAX_LOAD_FACTOR = 0.75f;

    private void initializeLists() {
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
    }

    public HashMapIndirecte() {
        table = new LinkedList[INITIAL_CAPACITY];
        numElements = 0;
        initializeLists();
    }

    public HashMapIndirecte(int mida) {
        table = new LinkedList[mida];
        numElements = 0;
        initializeLists();
    }

    @Override
    public void inserir(K key, V value) {
        int index = calculateIndex(key);
        LinkedList<Entry<K, V>> list = table[index];

        if (list == null) {
            list = new LinkedList<>();
            table[index] = list;
        }

        for (Entry<K, V> entry : list) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        list.add(new Entry<>(key, value));
        numElements++;
        if (factorCarrega() > MAX_LOAD_FACTOR) {
            rehash();
        }
    }

    @Override
    public V consultar(K key) throws ElementNoTrobat {
        int index = calculateIndex(key);
        LinkedList<Entry<K, V>> list = table[index];
        if (list == null) {
            System.out.println("List associated with index " + index + " is null");
            throw new ElementNoTrobat();
        }
        for (Entry<K, V> entry : list) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        throw new ElementNoTrobat();
    }

    @Override
    public void esborrar(K key) throws ElementNoTrobat {
        int index = calculateIndex(key);
        LinkedList<Entry<K, V>> list = table[index];
        Iterator<Entry<K, V>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            if (entry.key.equals(key)) {
                iterator.remove();
                numElements--;
                return;
            }
        }
        throw new ElementNoTrobat();
    }

    public boolean buscar(K key) {
        int index = calculateIndex(key);
        LinkedList<Entry<K, V>> list = table[index];
        if (list == null) {
            return false;
        }
        for (Entry<K, V> entry : list) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean esBuida() {
        return numElements == 0;
    }

    @Override
    public int numElements() {
        return numElements;
    }

    @Override
    public LlistaNoOrdenada<K> obtenirClaus() {
        LlistaNoOrdenada<K> keys = new LlistaNoOrdenada<>();
        for (LinkedList<Entry<K, V>> list : table) {
            for (Entry<K, V> entry : list) {
                keys.inserir(entry.key);
            }
        }
        return keys;
    }

    @Override
    public float factorCarrega() {
        return (float) numElements / table.length;
    }

    @Override
    public int midaTaula() {
        return table.length;
    }

    @Override
    public Iterator<V> iterator() {
        return new HashMapIterator();
    }

    private int calculateIndex(K key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % table.length;
    }

    private void rehash() {
        LinkedList<Entry<K, V>>[] oldTable = table;
        table = new LinkedList[oldTable.length * 2];
        initializeLists();
        numElements = 0;
        for (LinkedList<Entry<K, V>> list : oldTable) {
            if (list != null) {
                for (Entry<K, V> entry : list) {
                    inserir(entry.key, entry.value);
                }
            }
        }
    }

    public boolean contains(K key) {
        for (int i = 0; i < table.length; i++) {
            LinkedList<Entry<K, V>> list = table[i];
            if (list != null) {
                for (Entry<K, V> entry : list) {
                    if (entry.key.equals(key)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                table[i].clear();
            }
        }
        numElements = 0;
    }

    public HashMapIndirecte<K, V> deepCopy() throws PosicioForaRang {
        HashMapIndirecte<K, V> copy = new HashMapIndirecte<>();
        ILlistaGenerica<K> keys = obtenirClaus();
        for (int i = 0; i < keys.numElements(); i++) {
            K key = keys.consultar(i);
            try {
                V value = consultar(key);
                copy.inserir(key, value);
            } catch (ElementNoTrobat e) {
                e.printStackTrace();
            }
        }
        return copy;
    }

    private static class Entry<K, V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private class HashMapIterator implements Iterator<V> {
        int currentIndex = -1;
        Iterator<Entry<K, V>> currentListIterator = null;

        public HashMapIterator() {
            advance();
        }

        private void advance() {
            while (currentIndex < table.length - 1) {
                currentIndex++;
                if (table[currentIndex].size() > 0) {
                    currentListIterator = table[currentIndex].iterator();
                    return;
                }
            }
            currentListIterator = null;
        }

        @Override
        public boolean hasNext() {
            return currentListIterator != null && currentListIterator.hasNext();
        }

        @Override
        public V next() {
            if (hasNext()) {
                return currentListIterator.next().value;
            }
            return null;
        }
    }

    public boolean existeix(K v1) {
        int index = calculateIndex(v1);
        LinkedList<Entry<K, V>> list = table[index];
        if (list == null) {
            return false;
        }
        for (Entry<K, V> entry : list) {
            if (entry.key.equals(v1)) {
                return true;
            }
        }
        return false;
    }

}
